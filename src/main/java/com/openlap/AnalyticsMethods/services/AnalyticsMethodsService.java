package com.openlap.AnalyticsMethods.services;


import com.openlap.AnalyticsMethods.exceptions.AnalyticsMethodLoaderException;
import com.openlap.AnalyticsMethods.exceptions.AnalyticsMethodNotFoundException;
import com.openlap.AnalyticsMethods.exceptions.AnalyticsMethodsBadRequestException;
import com.openlap.AnalyticsMethods.exceptions.AnalyticsMethodsUploadErrorException;
import com.openlap.AnalyticsMethods.model.AnalyticsMethods;
import com.openlap.OpenLAPAnalyaticsFramework;
import com.openlap.template.AnalyticsMethod;
import com.openlap.dataset.OpenLAPColumnConfigData;
import com.openlap.dataset.OpenLAPDataSetConfigValidationResult;
import com.openlap.dataset.OpenLAPPortConfig;
import com.openlap.dynamicparam.OpenLAPDynamicParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.TransactionManager;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * This service handles the "business logic" of the macro component. It also works as a facade for other
 * macro components that happen to be running on the same server, i.e. the Analytics Engine and Analytics Modules
 *
 * Created by Faizan Riaz on 12/06/19.
 */

@Service
//@ContextConfiguration(classes = OpenLAPAnalyaticsFramework.class)
public class AnalyticsMethodsService {

    // Strings for method names
    private static final String JAR_EXTENSION = ".jar";
    private static final String TEMP_FILE_SUFIX = "_temp";
    private static final String INPUT_PORTS = "input";
    private static final String OUTPUT_PORTS = "output";
    private static final Logger log =  LoggerFactory.getLogger(OpenLAPAnalyaticsFramework.class);

    @Value("${analytics.jars.folder}")
    String analyticsMethodsJarsFolder;


    //AnalyticsMethodsClassPathLoader classPathLoader = null;

    @Autowired
    AnalyticsMethodsUploadValidator validator;

/*    @PostConstruct
    public void loadJarFiles(){
        classPathLoader =  new AnalyticsMethodsClassPathLoader(analyticsMethodsJarsFolder + "");
    }*/

    public AnalyticsMethodsClassPathLoader getFolderNameFromResources()
    {
        System.out.println(analyticsMethodsJarsFolder);
        AnalyticsMethodsClassPathLoader classPathLoader =  new AnalyticsMethodsClassPathLoader(analyticsMethodsJarsFolder);
      return classPathLoader;
    }

    public AnalyticsMethod loadAnalyticsMethodInstance(String analyticsMethodId, AnalyticsMethodsClassPathLoader classPathLoader) throws AnalyticsMethodLoaderException {
        AnalyticsMethods analyticsMethodMetadata = em.find(AnalyticsMethods.class, analyticsMethodId);
        if (analyticsMethodMetadata == null || analyticsMethodId == null) {
            throw new AnalyticsMethodNotFoundException("Analytics Method with id not found: " + analyticsMethodId);
        } else {

            AnalyticsMethod method;

           // AnalyticsMethodsClassPathLoader classPathLoader =  new AnalyticsMethodsClassPathLoader(analyticsMethodsJarsFolder);
            method = classPathLoader.loadClass(analyticsMethodMetadata.getImplementing_class());
            return method;
        }
    }
    TransactionManager tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
    EntityManagerFactory factory = Persistence.createEntityManagerFactory("OpenLAP");
    EntityManager em = factory.createEntityManager();


    /**
     * Lists all the Metadata of the  AnalyticsMethods available
     *
     * @return A List of the available AnalyticsMethods
     */
    public List<AnalyticsMethods> viewAllAnalyticsMethods() {
        String query = "From AnalyticsMethods a ORDER by a.filename  ASC";
        List<AnalyticsMethods> analyticsMethodsModels = em.createQuery(query, AnalyticsMethods.class).getResultList();
        return  analyticsMethodsModels;
    }
    /**
     * Returns the Metadata of the Analytics Method of the specified ID
     *
     * @param id ID of the AnalyticsMethods to view
     * @return The AnalyticsMethods with Metadata of the specified ID
     * @throws AnalyticsMethodNotFoundException
     */
    public AnalyticsMethods viewAnalyticsMethod(String id) throws AnalyticsMethodNotFoundException {
        AnalyticsMethods analyticsMethods = em.find(AnalyticsMethods.class, id);
        return analyticsMethods;
    }

    /**
     * Post an AnalyticsMethods to the Server to be validated and made available for usage.
     *
     * @param analyticsMethod The metadata to upload as manifest of the AnalyticsMethods
     * @param jarBundle      The JAR file with the implementation of the AnalyticsMethods
     * @return The Metadata of the uploaded AnalyticsMethods if deemed valid by the OpenLAP
     */
    public AnalyticsMethods uploadAnalyticsMethod(
            AnalyticsMethods analyticsMethod, MultipartFile jarBundle) {

        AnalyticsMethodsValidationInformation validationInformation;
        AnalyticsMethodsFileHandler fileHandler = new AnalyticsMethodsFileHandler(log);

        if (!jarBundle.isEmpty()) {
            try {
                // Save the jar in the filesystem
                fileHandler.saveFile(jarBundle, analyticsMethodsJarsFolder, analyticsMethod.getFilename()
                        + JAR_EXTENSION);

                //Validation
                validationInformation = validator.validatemethod(analyticsMethod, analyticsMethodsJarsFolder);
                if (!validationInformation.isValid()) {
                    // If the submitted jar is not valid, remove it from the filesystem
                    fileHandler.deleteFile(analyticsMethodsJarsFolder, analyticsMethod.getFilename() + JAR_EXTENSION);
                    // Throw exception (that can be used by the controller to send the bad request method)
                    throw new AnalyticsMethodsUploadErrorException(validationInformation.getMessage());
                } else {
                    log.info(validationInformation.getMessage());
                    // Stamp the location of the method in metadata and save it
                    analyticsMethod.setBinaries_location(analyticsMethodsJarsFolder);
                    tm.begin();
                    em.persist(analyticsMethod);
                    //Commit
                    em.flush();
                    tm.commit();
                    return analyticsMethod;
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new AnalyticsMethodsUploadErrorException(e.getMessage());
            } catch (DataIntegrityViolationException sqlException) {
                sqlException.printStackTrace();
                throw new AnalyticsMethodsBadRequestException("Analytics Method already exists.");
            } catch (Exception e) {
                e.printStackTrace();
                throw new AnalyticsMethodsBadRequestException(e.getMessage());
            }
        }
        throw new AnalyticsMethodsBadRequestException("Empty jar bundle.");
    }

    /**
     * Update an AnalyticsMethods to the Server to be validated and made available for usage.
     *
     * @param analyticsMethods The metadata to upload as manifest of the AnalyticsMethods
     * @param id             ID of the AnalyticsMethods Metadata that is to be updated.
     * @param jarBundle      The JAR file with the implementation of the AnalyticsMethods
     * @return The Metadata of the uploaded AnalyticsMethods if deemed valid by the OpenLAP
     */
    public AnalyticsMethods updateAnalyticsMethod(AnalyticsMethods analyticsMethods, String id, MultipartFile jarBundle) {
        AnalyticsMethodsValidationInformation validationInformation;
        AnalyticsMethodsFileHandler fileHandler = new AnalyticsMethodsFileHandler(log);

        //Try to fetch the method, if does not exist, throw exception
        AnalyticsMethods result = em.find(AnalyticsMethods.class, id);
        //AnalyticsMethodsModel result = analyticsMethodsRepository.findByid(id);

        if (result == null) {
            throw new AnalyticsMethodNotFoundException("Analytics Method with id not found: " + analyticsMethods.getId());
        } else {
            //log.info("Attemting to update Analytics method: " + methodMetadata.getId());
            //Make bundle required.
            if (!jarBundle.isEmpty()) {
                // Save the jar in the filesystem
                try {
                    AnalyticsMethods tempMetadata = (AnalyticsMethods) result.clone();
                    //Name bundle method_temp.jar
                    tempMetadata.setName(tempMetadata.getFilename() + TEMP_FILE_SUFIX);
                    tempMetadata.setImplementing_class(analyticsMethods.getImplementing_class());
                    fileHandler.saveFile(jarBundle, analyticsMethodsJarsFolder, tempMetadata.getFilename()
                            + JAR_EXTENSION);
                    //Perform validation with than name.
                    //If valid, delete the old jar with the new one, check that there are no leftovers
                    validationInformation = validator.validatemethod(tempMetadata, analyticsMethodsJarsFolder);
                    if (validationInformation.isValid()) {
                        //delete temp file
                        fileHandler.deleteFile(analyticsMethodsJarsFolder, tempMetadata.getFilename()
                                + JAR_EXTENSION);
                        //write real file
                        fileHandler.saveFile(jarBundle, analyticsMethodsJarsFolder, analyticsMethods.getFilename()
                                + JAR_EXTENSION);
                        //update metadata

                        result.updateWithMetadata(analyticsMethods);
                        return em.merge(result);
                    } else {
                        //delete temp file
                        fileHandler.deleteFile(analyticsMethodsJarsFolder, tempMetadata.getFilename()
                                + JAR_EXTENSION);
                        throw new AnalyticsMethodsBadRequestException(validationInformation.getMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new AnalyticsMethodsUploadErrorException(e.getMessage());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                    throw new AnalyticsMethodsUploadErrorException(e.getMessage());
                }
            } else {
                throw new AnalyticsMethodsBadRequestException("Empty jar bundle.");
            }
        }
    }

    /**
     * Method that allows to validate an OpenLAPPortConfig of a specific AnalyticsMethods.
     *
     * @param analyticsMethodId ID of the AnalyticsMethods Metadata to be validated against the OpenLAPPortConfig.
     * @param configuration     The OpenLAPPortConfig to be validated
     * @return An Object with the validation information of the OpenLAPPortConfig against the specified Analytics
     * Method.
     * @throws AnalyticsMethodLoaderException
     */
    public OpenLAPDataSetConfigValidationResult validateConfiguration(
            String analyticsMethodId, OpenLAPPortConfig configuration) throws AnalyticsMethodLoaderException {
        //log.info("Attempting to validateConfiguration :" + configuration.getMapping()
        //        + "for method with id: " + analyticsMethodId);

        AnalyticsMethod method = loadAnalyticsMethodInstance(analyticsMethodId, this.getFolderNameFromResources());
        return method.getInput().validateConfiguration(configuration);
    }

    /**
     * A Method that creates an instance of an AnalyticsMethods object from the JAR location contained in the
     * corresponding AnalyticsMethodMetadata.
     *
     * @param analyticsMethodId The ID of the Analytics Method to instantiate
     * @return A new instance of the specified Analytics Method.
     * @throws AnalyticsMethodLoaderException
     */
   /* public AnalyticsMethod loadAnalyticsMethodInstance(String analyticsMethodId) throws AnalyticsMethodLoaderException {
        AnalyticsMethods analyticsMethodMetadata = em.find(AnalyticsMethods.class, analyticsMethodId);
        if (analyticsMethodMetadata == null || analyticsMethodId ==null) {
            throw new AnalyticsMethodNotFoundException("Analytics Method with id not found: " + analyticsMethodId);
        } else {

            AnalyticsMethod method;

            //AnalyticsMethodsClassPathLoader classPathLoader =  new AnalyticsMethodsClassPathLoader("./analyticsMethodsJars/" );
            method = classPathLoader.loadClass(analyticsMethodMetadata.getImplementing_class());
            return method;
        }*/


    /**
     * Method that returns the OpenLAPColumnConfigData of the input ports of a specific AnalyticsMethods
     *
     * @param id ID of the AnalyticsMethods Metadata
     * @return A list of OpenLAPColumnConfigData corresponding to the input ports of the AnalyticsMethods
     */
    public List<OpenLAPColumnConfigData> GetInputPortsForMethod(String id) {
        return getPortsForMethod(id, INPUT_PORTS);
    }

    /**
     * Method that returns the OpenLAPColumnConfigData of the output ports of a specific AnalyticsMethods
     *
     * @param id ID of the AnalyticsMethods Metadata
     * @return A list of OpenLAPColumnConfigData corresponding to the output ports of the AnalyticsMethods
     */
    public List<OpenLAPColumnConfigData> GetOutputPortsForMethod(String id) {
        return getPortsForMethod(id, OUTPUT_PORTS);
    }

    public List<OpenLAPDynamicParam> GetDynamicParamsForMethod(String id) {
        AnalyticsMethod method = loadAnalyticsMethodInstance(id, this.getFolderNameFromResources());

        return method.getParams().getParamsAsList(false);
    }

    /**
     * Returns a List of OpenLAPColumnConfigData of either the Input ports or output ports of the Analytics Method
     * of the given <code>id</code>.
     *
     * @param id            of the Analytics Method
     * @param portParameter Either <code>INPUT_PORT</code> or <code>OUTPUT_PORTS</code>
     * @return List of the OpenLAPColumnConfigData corresponding to the inputs or outputs of the Analytics Method
     * @throws AnalyticsMethodLoaderException
     */
    private List<OpenLAPColumnConfigData> getPortsForMethod(String id, String portParameter)
            throws AnalyticsMethodLoaderException {

        AnalyticsMethod method = loadAnalyticsMethodInstance(id,this.getFolderNameFromResources());
        //log.info("Attempting to return " + portParameter + " ports of the method with id {" + id + "}");

        List<OpenLAPColumnConfigData> ports;

        switch (portParameter) {
            case INPUT_PORTS:
                ports = method.getInputPorts();
                break;
            case OUTPUT_PORTS:
                ports = method.getOutputPorts();
                break;
            default:
                throw new AnalyticsMethodsBadRequestException("Only can return Inputs or Outputs");
        }

        Collections.sort(ports, (OpenLAPColumnConfigData o1, OpenLAPColumnConfigData o2) -> (o1.getTitle().compareTo(o2.getTitle())));

        return ports;
    }

    /**
     * Delete the specified AnalyticsMethods
     *
     * @param id id of the AnalyticsMethods to be deleted
     */
    public void deleteAnalyticsMethod(String id) {
        try {
            AnalyticsMethodsFileHandler fileHandler = new AnalyticsMethodsFileHandler(log);
            em.getTransaction().begin();
            AnalyticsMethods analyticsMethods = em.find( AnalyticsMethods.class, id);
            if (analyticsMethods == null || id == null) {
                throw new AnalyticsMethodNotFoundException("Analytics Method with id = {"
                        + id + "} not found.");
            }
            // Delete Files
            fileHandler.deleteFile(analyticsMethodsJarsFolder, analyticsMethods.getFilename());
            em.remove( analyticsMethods );
            em.getTransaction().commit();
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
