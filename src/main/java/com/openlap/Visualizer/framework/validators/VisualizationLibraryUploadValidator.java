package com.openlap.Visualizer.framework.validators;

import com.openlap.Visualizer.exceptions.DataTransformerCreationException;
import com.openlap.Visualizer.exceptions.VisualizationCodeGeneratorCreationException;
import com.openlap.Visualizer.exceptions.VisualizationLibraryUploadException;
import com.openlap.Visualizer.framework.factory.DataTransformerFactory;
import com.openlap.Visualizer.framework.factory.DataTransformerFactoryImpl;
import com.openlap.Visualizer.framework.factory.VisualizationCodeGeneratorFactory;
import com.openlap.Visualizer.framework.factory.VisualizationCodeGeneratorFactoryImpl;
import com.openlap.Visualizer.model.VisualizationLibrary;
import com.openlap.Visualizer.model.VisualizationType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

/**
 * Class contains the logic and functions to validate the config object of visualization frameworks
 *
 * @author Bassim Bashir
 */
public class VisualizationLibraryUploadValidator {

    public boolean validateVisualizationLibraryUploadConfiguration(List<VisualizationLibrary> LibraryConfig, MultipartFile LibraryJar) throws VisualizationLibraryUploadException {
        // first step of validation, check if all the fields are not null
        if (LibraryConfig.stream().filter(new Predicate<VisualizationLibrary>() {
            @Override
            public boolean test(VisualizationLibrary visualizationLibrary) {
                if (visualizationLibrary.getName() != null && visualizationLibrary.getDescription() != null
                        && visualizationLibrary.getCreator() != null && !visualizationLibrary.getName().isEmpty()
                        && !visualizationLibrary.getCreator().isEmpty() && !visualizationLibrary.getDescription().isEmpty()
                        && visualizationLibrary.getVisualizationTypes() != null) {
                    //lets go deeper into the visualization methods definition
                    if (visualizationLibrary.getVisualizationTypes().size() > 0) {
                        for (VisualizationType visualizationType : visualizationLibrary.getVisualizationTypes()) {
                            if (visualizationType.getName() != null && visualizationType.getImplementingClass() != null
                                    && !visualizationType.getName().isEmpty() && !visualizationType.getImplementingClass().isEmpty()) {
                                return false; // do not include in the filtered list
                            } else {
                                return true;
                            }
                        }
                    } else {
                        return true;
                    }
                }
                return true;
            }
        }).count() > 0) {
            return false;
        }
        //now try loading the classes and confirm if they implement the required interfaces
        try {
            //DataTransformerFactory dataTransformerFactory = new DataTransformerFactoryImpl(frameworksJar.getInputStream());
            //VisualizationCodeGeneratorFactory visualizationCodeGeneratorFactory = new VisualizationCodeGeneratorFactoryImpl(frameworksJar.getInputStream());
            for (VisualizationLibrary visualizationLibrary : LibraryConfig) {
                for (VisualizationType visualizationType : visualizationLibrary.getVisualizationTypes()) {
                    //lets first check the code generator
                    try {
                        //Need to optimize TODO
                        VisualizationCodeGeneratorFactory visualizationCodeGeneratorFactory = new VisualizationCodeGeneratorFactoryImpl(LibraryJar.getInputStream());
                        //DataTransformerFactory dataTransformerFactory = new DataTransformerFactoryImpl(LibraryJar.getInputStream());

                        if (visualizationCodeGeneratorFactory.createVisualizationCodeGenerator(visualizationType.getImplementingClass()) == null)
                            return false;
                        //now the data transformer
                        //if (dataTransformerFactory.createDataTransformer(visualizationType.getVisualizationDataTransformerMethod().getImplementingClass()) == null)
                        //    return false;
                    } catch (DataTransformerCreationException | VisualizationCodeGeneratorCreationException exception) {
                        throw new VisualizationLibraryUploadException(exception.getMessage());
                    }
                }
            }
        } catch (IOException exception) {
            throw new VisualizationLibraryUploadException(exception.getMessage());
        }
        return true;
    }
}
