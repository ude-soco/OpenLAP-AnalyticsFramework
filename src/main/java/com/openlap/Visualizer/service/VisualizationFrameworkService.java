package com.openlap.Visualizer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openlap.OpenLAPAnalyaticsFramework;
import com.openlap.Visualizer.dtos.VisualizationTypeConfiguration;
import com.openlap.Visualizer.exceptions.*;
import com.openlap.Visualizer.framework.factory.VisualizationCodeGeneratorFactory;
import com.openlap.Visualizer.framework.factory.VisualizationCodeGeneratorFactoryImpl;
import com.openlap.Visualizer.framework.validators.VisualizationLibraryUploadValidator;
import com.openlap.Visualizer.model.VisualizationDataTransformerMethod;
import com.openlap.Visualizer.model.VisualizationLibrary;
import com.openlap.Visualizer.model.VisualizationType;
import com.openlap.dataset.OpenLAPDataSet;
import com.openlap.dataset.OpenLAPPortConfig;
import com.openlap.template.VisualizationCodeGenerator;
import com.openlap.exceptions.DataSetValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.transaction.TransactionManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A service which provides functions to perform CRUD operations on the Visualization Frameworks or Visualization Types. In addition, also Types to get information about the stored
 * frameworks
 *
 * @author Bassim Bashir
 */
@Service
public class VisualizationFrameworkService {

    private static final Logger log = LoggerFactory.getLogger(OpenLAPAnalyaticsFramework.class);

    //private VisualizationCodeGeneratorFactory visualizationCodeGeneratorFactory;
    @Autowired
    private FileManager fileManager;

    TransactionManager tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
    EntityManagerFactory factory = Persistence.createEntityManagerFactory("OpenLAP");
    EntityManager em = factory.createEntityManager();

   ObjectMapper objectMapper;

    /**
     * @return The list of VisualizationFrameworks existing in the system
     */
    public List<VisualizationLibrary> findAllVisualizationLibraries() {

        List<VisualizationLibrary> visualizationLibraries = em.createQuery("From VisualizationLibrary ORDER BY name ASC", VisualizationLibrary.class).getResultList();
        return visualizationLibraries;
    }

    /**
     * @param idOfLibrary The id of the VisualizationLibrary to retrieve
     * @return The VisualizationLibrary represented by the provided id
     * @throws VisualizationLibraryNotFoundException when the framework was not found
     */
    public VisualizationLibrary findVisualizationLibraryById(String idOfLibrary) throws VisualizationLibraryNotFoundException {

        VisualizationLibrary visualizationLibrary = em.find(VisualizationLibrary.class, idOfLibrary);

        return visualizationLibrary;
    }
    /**
     * @param idOfType The id of the VisualizationType to retrieve
     * @return The VisualizationType represented by the provided id
     * @throws VisualizationTypeNotFoundException when the Type was not found
     */
    public VisualizationType findVisualizationTypeById(String idOfType) throws VisualizationTypeNotFoundException {
        VisualizationType  visualizationType = em.find(VisualizationType.class, idOfType);
        return visualizationType;
    }

    public String getLibraryScript(String idOfLibrary, String idOfType) {
        VisualizationType visualizationType = em.find(VisualizationType.class, idOfType);
        if (visualizationType != null) {
            VisualizationCodeGeneratorFactory visualizationCodeGeneratorFactory = new VisualizationCodeGeneratorFactoryImpl(visualizationType.getVisualizationLibrary().getFrameworkLocation());
            VisualizationCodeGenerator codeGenerator = visualizationCodeGeneratorFactory.createVisualizationCodeGenerator(visualizationType.getImplementingClass());
            return encodeURIComponent(codeGenerator.getVisualizationLibraryScript());
        } else {
            throw new DataSetValidationException("The visualization Type represented by the id: " + idOfType + " not found.");
        }
    }

    /**
     * Performs the upload of the visualization Library by copying over the jar bundle and making the relevant Database entries
     *
     * @param LibraryList The configuration of the Libraries in the provided jar file
     * @param jarFile       The jar bundle which contains the package Library implementation
     * @throws VisualizationLibraryUploadException If the validation of the provided configuration failed or copying the provided jar file was not successful
     */
    @Transactional(rollbackFor = {RuntimeException.class})
    public void uploadVisualizationLibraries(List<VisualizationLibrary> LibraryList, MultipartFile jarFile) throws VisualizationLibraryUploadException {
        //validate the jar classes first before passing it on to the File Manager
        VisualizationLibraryUploadValidator visualizationLibrariesUploadValidator = new VisualizationLibraryUploadValidator();
        if (visualizationLibrariesUploadValidator.validateVisualizationLibraryUploadConfiguration(LibraryList, jarFile)) {
            //if the configuration is valid then perform the upload, i.e db entries and copying of the jar
            // first copy the file over
            try {
                Path fileName = Paths.get(jarFile.getOriginalFilename()).getFileName();
                String savedFilePath;
                if (fileName != null) {
                    if (!fileManager.fileExists(fileName.toString()))
                        savedFilePath = fileManager.saveJarFile(fileName.toString(), jarFile);
                    else
                        throw new VisualizationLibraryUploadException("The file being uploaded : " + fileName.toString() + "  already exists.");
                } else {
                    savedFilePath = fileManager.saveJarFile("", jarFile);
                }
                //second save the visualization Library being uploaded
                // set the file location in all the Library objects
                LibraryList.forEach(Library -> Library.setFrameworkLocation(savedFilePath));
                // set the Library in each of the Types as it is a bidirectional relationship
                LibraryList.forEach(Library -> Library.getVisualizationTypes().forEach(Type -> Type.setVisualizationLibrary(Library)));


                //Validating all the values before starting to save in the database.
                String validateLibraryException = "";
                for (VisualizationLibrary Library : LibraryList) {
                    VisualizationLibrary availableLibrary = em.find(VisualizationLibrary.class, Library.getName());
                    if(availableLibrary != null)
                        validateLibraryException += ";The visualization Library with name '" + Library.getName() + "'  already exists.";

                    List<VisualizationDataTransformerMethod> dataTransformerMethodList = new ArrayList<VisualizationDataTransformerMethod>();

                    for (VisualizationType Type : Library.getVisualizationTypes()) {

                        VisualizationDataTransformerMethod newDataTransformer = new VisualizationDataTransformerMethod();
                        newDataTransformer.setName(Type.getVisualizationDataTransformerMethod().getName());
                        newDataTransformer.setImplementingClass(Type.getVisualizationDataTransformerMethod().getImplementingClass());

                        //Optional oldDataTransformer = dataTransformerMethodList.stream().filter(o -> o.getName().equals(newDataTransformer.getName()) && o.getImplementingClass().equals(newDataTransformer.getImplementingClass())).findFirst();
                        Optional oldDataTransformer = dataTransformerMethodList.stream().filter(o -> o.getImplementingClass().equals(newDataTransformer.getImplementingClass())).findFirst();

                        if (!oldDataTransformer.isPresent()) {
                            VisualizationDataTransformerMethod classDataTransformer = em.find(VisualizationDataTransformerMethod.class, Type.getVisualizationDataTransformerMethod().getImplementingClass());
                            if(classDataTransformer != null)
                                validateLibraryException += ";The Data Transformer with implementing class name '" + Type.getVisualizationDataTransformerMethod().getImplementingClass() + "'  already exists.";

                        }

                        VisualizationType classVisualizationMethod = em.find(VisualizationType.class, Type.getImplementingClass());
                        if(classVisualizationMethod != null)
                            validateLibraryException += ";The Visualization Type with implementing class name '" + Type.getImplementingClass() + "'  already exists.";
                    }
                }

                //commenting out this line since it is not able to support new visualization Library which have multiple visualization Types using the same data transformer class
                //visualizationFrameworkRepository.save(frameworkList);

                if(validateLibraryException == null || validateLibraryException.isEmpty()) {
                    //Manually saving the Library, related Types and data transformers
                    for (VisualizationLibrary Library : LibraryList) {
                        VisualizationLibrary newLibrary = new VisualizationLibrary();
                        newLibrary.setName(Library.getName());
                        newLibrary.setCreator(Library.getCreator());
                        newLibrary.setDescription(Library.getDescription());
                        newLibrary.setFrameworkLocation(Library.getFrameworkLocation());

                        VisualizationLibrary savedLibrary = em.merge(newLibrary);

                        em.getTransaction().begin();
                        em.persist(newLibrary);
                        em.flush();
                        em.getTransaction().commit();
                        //VisualizationLibraryDAO savedFramework = visualizationFrameworkRepository.save(newFramework);

                        List<VisualizationDataTransformerMethod> dataTransformerMethodList = new ArrayList<VisualizationDataTransformerMethod>();

                        for (VisualizationType type : Library.getVisualizationTypes()) {

                            VisualizationDataTransformerMethod newDataTransformer = new VisualizationDataTransformerMethod();
                            newDataTransformer.setName(type.getVisualizationDataTransformerMethod().getName());
                            newDataTransformer.setImplementingClass(type.getVisualizationDataTransformerMethod().getImplementingClass());

                            //Optional oldDataTransformer = dataTransformerMethodList.stream().filter(o -> o.getName().equals(newDataTransformer.getName()) && o.getImplementingClass().equals(newDataTransformer.getImplementingClass())).findFirst();
                            Optional oldDataTransformer = dataTransformerMethodList.stream().filter(o -> o.getImplementingClass().equals(newDataTransformer.getImplementingClass())).findFirst();

                            VisualizationDataTransformerMethod savedDataTransformer;

                            if (oldDataTransformer.isPresent()) {
                                savedDataTransformer = (VisualizationDataTransformerMethod) oldDataTransformer.get();
                            } else {
                                em.getTransaction().begin();
                                savedDataTransformer = em.merge(newDataTransformer);
                                dataTransformerMethodList.add(savedDataTransformer);
                                em.persist(dataTransformerMethodList);

                            }

                            VisualizationType newType = new VisualizationType();
                            newType.setName(type.getName());
                            newType.setImplementingClass(type.getImplementingClass());
                            newType.setVisualizationDataTransformerMethod(savedDataTransformer);
                            newType.setVisualizationLibrary(savedLibrary);

                            em.persist(newType);
                            em.flush();
                            em.getTransaction().commit();
                        }
                    }
                }
                else {
                    if (fileManager.fileExists(fileName.toString()))
                        fileManager.deleteJarFile(fileName.toString());
                    throw new VisualizationLibraryUploadException(validateLibraryException.substring(1));
                }

                //TODO this needs to be fixed since its giving error
                //third create the visualization suggestion entries for all the Types
                //add the input datasets of the uploaded visualization code generators, for automatic suggestions
                VisualizationCodeGeneratorFactory visualizationCodeGeneratorFactory = new VisualizationCodeGeneratorFactoryImpl(jarFile.getInputStream());
                //List<VisualizationSuggestion> visualizationSuggestions = new ArrayList<>();
                //save all the suggestions in the database
                //visualizationSuggestionRepository.save(visualizationSuggestions);
            } catch (FileManagerException | IOException exception) {
                throw new VisualizationLibraryUploadException(exception.getMessage());
            }
        } else {
            throw new VisualizationLibraryUploadException("Upload configuration is not correct");
        }
    }

    /**
     * Removes a previously added VisualizationLibrary from the system. This includes all the Database entries alongwith the
     * stored JAR (if no other Library is referencing it). DataTransformers will not be deleted as other VisualizationTypes in other Liberaries might reference them
     *
     * @param idOfLibrary The id of the VisualizationLibrary to delete.
     * @return true if the deletion of the VisualizationLibrary was successful
     * @throws VisualizationLibraryDeletionException if the deletion of the VisualizationLibrary encountered problems such as the file couldn't be removed
     */
    @Transactional(rollbackFor = {RuntimeException.class})
    public void deleteVisualizationLibrary(String idOfLibrary) throws VisualizationLibraryDeletionException {
        VisualizationLibrary visualizationLibrary = em.find(VisualizationLibrary.class, idOfLibrary);
        if (visualizationLibrary == null) throw new VisualizationLibraryDeletionException
                ("Could not delete the Library with id: " + idOfLibrary + ", not found");
        //first load the Library to get the jar location
        //VisualizationLibrary visualizationFramework = visualizationFrameworkRepository.findOne(idOfFramework);
        String LibraryLocation = visualizationLibrary.getFrameworkLocation();
        // remove all the database entries
        em.getTransaction().begin();
        //finally delete the vis Library and its Types
        em.remove(visualizationLibrary);
        em.getTransaction().commit();
        em.close();
        //finally delete the jar, if no other Library references it
        List<VisualizationLibrary> LibrariesReferenced = findAllVisualizationLibraries().stream()
                .filter((framework) -> framework.getFrameworkLocation().equals((LibraryLocation)))
                .collect(Collectors.toList());
        if (LibrariesReferenced.size() > 0) {
            try {
                fileManager.deleteFile(visualizationLibrary.getFrameworkLocation());
            } catch (FileManagerException fileManagerException) {
                throw new VisualizationLibraryDeletionException(fileManagerException.getMessage());
            }
        }
    }

    /**
     * Removes a previously added VisualizationType from the system along with all the VisualizationSuggestions which refer to it.
     * DataTransformers will not be deleted as other VisualizationTypes might reference them
     *
     * @param idOfType The id of the VisualizationType to delete.
     * @return true if the deletion of the VisualizationType was successful
     * @throws VisualizationTypeDeletionException if the deletion of the VisualizationType encountered problems such as the file couldn't be removed
     */
    @Transactional(rollbackFor = {RuntimeException.class})
    public void deleteVisualizationType(String idOfType) throws VisualizationTypeDeletionException {
        //first load the Type
        VisualizationType visualizationType = em.find(VisualizationType.class, idOfType);
        if (visualizationType == null)
            throw new VisualizationTypeDeletionException("Could not delete the Type with id: " + idOfType + ", not found");

        em.remove(visualizationType);

    }

    /**
     * Removes a previously added DataTransformer from the system if no VisualizationType references it
     *
     * @param idOfTransformer The id of the DataTransformer to delete.
     * @return true if the deletion of the DataTransformer was successful
     * @throws DataTransformerDeletionException if the deletion of the DataTransformer encountered problems
     */
    @Transactional(rollbackFor = {RuntimeException.class})
    public void deleteDataTransformer(String idOfTransformer) throws DataTransformerDeletionException {
        VisualizationDataTransformerMethod visualizationDataTransformerMethod = em.find(VisualizationDataTransformerMethod.class, idOfTransformer);
        if (visualizationDataTransformerMethod == null)
            throw new DataTransformerDeletionException("Could not delete the data transformer with id: " + idOfTransformer + ", not found");

        em.remove(visualizationDataTransformerMethod);
    }

    /**
     * Updates the attributes of a VisualizationType
     *
     *
     * @param visualizationDataTransformerMethod
     * @param newAttributes An instance filled with the new values of the visualization Type. Except for the visualization Library, the Type id and the data transformer id
     *                      all other attributes can be updated
     * @param id   the id of the VisualizationType to be updated
     * @return VisualizationType the updated object of the VisualizationType
     * @throws VisualizationTypeNotFoundException if the VisualizationType to update was not found
     */
    public VisualizationType updateVisualizationTypeAttributes( VisualizationType newAttributes, String id) throws VisualizationTypeNotFoundException {
        VisualizationType visualizationType = em.find(VisualizationType.class, id);
        if (visualizationType == null)
            throw new VisualizationTypeNotFoundException("The VisualizationType with the id: " + id + " does not exist!");

        // update the name of the Type
        if (newAttributes.getName() != null && !newAttributes.getName().isEmpty())
            visualizationType.setName(newAttributes.getName());

        // update the implementing class
        if (newAttributes.getImplementingClass() != null && !newAttributes.getImplementingClass().isEmpty())
            visualizationType.setImplementingClass(newAttributes.getImplementingClass());
           // VisualizationTypeConfiguration visualizationTypeConfiguration = getTypeConfiguration(id);


        if (newAttributes.getVisualizationDataTransformerMethod() != null) {
            VisualizationDataTransformerMethod visualizationDataTransformerMethod = em.find(VisualizationDataTransformerMethod.class, newAttributes.getVisualizationDataTransformerMethod().getId());
            if (visualizationDataTransformerMethod == null)
                //finally set the data transformer method
                visualizationType.setVisualizationDataTransformerMethod(visualizationDataTransformerMethod);
        }
        //commit the changes
        em.getTransaction().begin();
        em.persist(newAttributes);
        em.flush();
        em.getTransaction().commit();

        return visualizationType;
    }

    /**
     * Updates the attributes of a VisualizationLibrary
     *
     * @param newAttributes An instance filled with the new values of the visualization Library. Only the attributes namely, description and uploadedBy can be
     *                      updated
     * @param idOfLibraray the id of the VisualizationLibrary to be updated
     * @return The updated VisualizationLibrary object
     * @throws VisualizationLibraryNotFoundException If the VisualizationLibrary to update was not found
     */
    public VisualizationLibrary updateVisualizationLibraryAttributes(VisualizationLibrary newAttributes, String idOfLibraray) throws VisualizationLibraryNotFoundException {
        VisualizationLibrary visualizationLibrary = em.find(VisualizationLibrary.class, idOfLibraray);
        if (visualizationLibrary==null)
            throw new VisualizationLibraryNotFoundException("The Library with id: " + idOfLibraray + " does not exist!");

        if (newAttributes.getDescription() != null && !newAttributes.getDescription().isEmpty())
            visualizationLibrary.setDescription(newAttributes.getDescription());
        if (newAttributes.getCreator() != null && !newAttributes.getCreator().isEmpty())
            visualizationLibrary.setCreator(newAttributes.getCreator());

        em.getTransaction().begin();
        em.persist(visualizationLibrary);
        em.flush();
        em.getTransaction().commit();
        return visualizationLibrary;
    }

    /**
     * Validates the configuration of the VisualizationType (i.e. the inputs that it accepts) with the provided OpenLAPPortConfig.
     *
     * @param visualizationTypeId The id of the VisualizationType for which to validate the configuration
     * @param olapPortConfiguration The OpenLAPPortConfig against which to validate the Type configuration
     * @return true if the the provided port configuration matches the configuration of the VisualizationType
     * @throws DataSetValidationException If the validation encountered an error
     */
    public boolean validateVisualizationTypeConfiguration(String visualizationTypeId, OpenLAPPortConfig olapPortConfiguration) throws DataSetValidationException {
        VisualizationType visualizationType = em.find(VisualizationType.class,visualizationTypeId);
        if (visualizationType != null) {
            //ask the factories for the instance
            VisualizationCodeGeneratorFactory visualizationCodeGeneratorFactory = new VisualizationCodeGeneratorFactoryImpl(visualizationType.getVisualizationLibrary().getFrameworkLocation());
            VisualizationCodeGenerator codeGenerator = visualizationCodeGeneratorFactory.createVisualizationCodeGenerator(visualizationType.getImplementingClass());
            return codeGenerator.isDataProcessable(olapPortConfiguration);
        } else {
            throw new DataSetValidationException("The visualization Type represented by the id: " + visualizationTypeId + " not found.");
        }
    }

    /**
     * Gets the configuration of a VisualizationType
     *
     * @param idOfType The id of the VisualizationType for which to get the configuration
     * @return The VisualizationTypeConfiguration instance
     * @throws VisualizationTypeNotFoundException if the VisualizationType was not found
     */
    public VisualizationTypeConfiguration getTypeConfiguration(String idOfType) throws VisualizationTypeNotFoundException {
        VisualizationType visualizationType = em.find(VisualizationType.class,idOfType);
        if (visualizationType==null)
            throw new VisualizationTypeNotFoundException("The visualization Type with the id : " + idOfType + " does not exist.");

        //ask the factories for the instance
        VisualizationCodeGeneratorFactory visualizationCodeGeneratorFactory = new VisualizationCodeGeneratorFactoryImpl(visualizationType.getVisualizationLibrary().getFrameworkLocation());
        VisualizationCodeGenerator codeGenerator = visualizationCodeGeneratorFactory.createVisualizationCodeGenerator(visualizationType.getImplementingClass());
        // serialize and de-serialize the DataSet to avoid the issue with the ClassCastException, because the VisualizationCodeGenerator is loaded in another class loader
        //and the OpenLAPDataSet in this piece code is loaded in another.
        OpenLAPDataSet inputDataSet = null;
        OpenLAPDataSet outputDataSet = null;
        try {
            inputDataSet = codeGenerator.getInput();
            inputDataSet = objectMapper.readValue(codeGenerator.getInputAsJsonString(), OpenLAPDataSet.class);
            outputDataSet = objectMapper.readValue(codeGenerator.getOutputAsJsonString(), OpenLAPDataSet.class);
        } catch (Exception ex) {
            log.error("Error in deserializing codegenerator input/output config.", ex);
        }
        VisualizationTypeConfiguration visualizationTypeConfiguration = new VisualizationTypeConfiguration();
        visualizationTypeConfiguration.setInput(inputDataSet);
        visualizationTypeConfiguration.setOutput(outputDataSet);
        return visualizationTypeConfiguration;
    }

    public String encodeURIComponent(String component) {
        String result = null;

        try {
            result = URLEncoder.encode(component, "UTF-8")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%7E", "~");
        } catch (UnsupportedEncodingException e) {
            result = component;
        }
        return result;
    }
}
