package com.openlap.Visualizer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openlap.Visualizer.dtos.error.BaseErrorDTO;
import com.openlap.Visualizer.dtos.request.UpdateVisualizationLibraryRequest;
import com.openlap.Visualizer.dtos.request.UploadVisualizationLibraryRequest;
import com.openlap.Visualizer.dtos.request.ValidateVisualizationTypeConfigurationRequest;
import com.openlap.Visualizer.dtos.response.*;
import com.openlap.Visualizer.exceptions.*;
import com.openlap.Visualizer.model.VisualizationLibrary;
import com.openlap.Visualizer.model.VisualizationType;
import com.openlap.Visualizer.service.VisualizationFrameworkService;
import com.openlap.Common.controller.GenericResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * A Spring controller which exposes an API for the client to upload new VisualizationFrameworks as well as
 * perform CRUD operations on VisualizationType and their VisualizationFrameworks
 *
 * @author Bassim Bashir
 */
@RestController
@RequestMapping("/frameworks")
public class VisualizationFrameworkController {

    @Autowired
    VisualizationFrameworkService visualizationFrameworkService;

    @RequestMapping(value = "/{idOfLibrary}/update", method = RequestMethod.PUT)
    public UpdateVisualizationLibraryResponse updateVisualizationLibrary(@PathVariable String idOfLibrary, @RequestBody UpdateVisualizationLibraryRequest updateVisualizationLibraryRequest) {
        UpdateVisualizationLibraryResponse response = new UpdateVisualizationLibraryResponse();
        response.setVisualizationLibrary(visualizationFrameworkService.updateVisualizationLibraryAttributes(updateVisualizationLibraryRequest.getVisualizationLibrary(), idOfLibrary));
        return response;
    }

    /**
     * HTTP endpoint handler method for deleting VisulizationLibrary
     *
     * @param id id of the VisulizationLibrary to be deleted
     * @return GenericResponseDTO with deletion confirmation
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    GenericResponseDTO deleteVisualizationLibrary(@PathVariable String id) {
        visualizationFrameworkService.deleteVisualizationLibrary(id);
        return new GenericResponseDTO(HttpStatus.OK.value(),
                "Visualization Library with id {" + id + "} deleted");
    }

    @RequestMapping(value = "/{idOfLibrary}", method = RequestMethod.GET)
    public VisualizationLibrary getLibraryDetails(@PathVariable String idOfLibrary) {

        return visualizationFrameworkService.findVisualizationLibraryById(idOfLibrary);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public UploadVisualizationLibraryResponse uploadNewVisualizationLibrary(@RequestParam("LibraryJarBundle") MultipartFile LibraryJarBundle,
                                                                              @RequestParam ("fLibraryConfig") String LibrariesUploadRequest) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UploadVisualizationLibraryRequest request = objectMapper.readValue(LibrariesUploadRequest, UploadVisualizationLibraryRequest.class);
            visualizationFrameworkService.uploadVisualizationLibraries(request.getVisualizationLibraries(), LibraryJarBundle);
            UploadVisualizationLibraryResponse response = new UploadVisualizationLibraryResponse();
            response.setSuccess(true);
            return response;
        } catch (VisualizationLibraryUploadException exception){
            throw  new VisualizationLibraryUploadException(exception.getMessage());
        }catch (IOException exception){
            throw new RuntimeException (exception.getMessage());
        }
    }

    @RequestMapping(value = "/{idOfLibrary}/methods/{idOfType}/LibraryScript", method = RequestMethod.GET)
    public String GetLibraryScript(@PathVariable String idOfLibrary, @PathVariable String idOfType) {
        return visualizationFrameworkService.getLibraryScript(idOfLibrary, idOfType);
    }

    @RequestMapping(value = "/{idOfLibrary}/methods/{idOfType}", method = RequestMethod.GET)
    public VisualizationType getFrameworkMethodDetails(@PathVariable String idOfLibrary, @PathVariable String idOfType) {
        return visualizationFrameworkService.findVisualizationTypeById(idOfType);
    }

    @RequestMapping(value = "/{idOfLibrary}/methods/{idOfType}", method = RequestMethod.DELETE)
    public DeleteVisualizationTypeResponse deleteVisualizationType(@PathVariable String idOfType) {
        DeleteVisualizationTypeResponse response = new DeleteVisualizationTypeResponse();
      // response.setSuccess(visualizationFrameworkService.deleteVisualizationType(idOfType));
        return response;
    }

    @RequestMapping(value = "/{idOfLibrary}/data_transformer/{idOfTransformer}", method = RequestMethod.DELETE)
    public DeleteDataTransformerResponse deleteDataTransformer(@PathVariable String idOfTransformer) {
        DeleteDataTransformerResponse response = new DeleteDataTransformerResponse();
      //  response.setSuccess(visualizationFrameworkService.deleteDataTransformer(idOfTransformer));
        return response;
    }

    @RequestMapping(value = "/{idOfLibrary}/methods/{id}", method = RequestMethod.PUT)
    public VisualizationType updateVisualizationTypeAttributes(@PathVariable String idOfLibrary, @PathVariable String id, @RequestBody  VisualizationType visualizationType) {
       // UpdateVisualizationTypeResponse response = new UpdateVisualizationTypeResponse();
        return  visualizationFrameworkService.updateVisualizationTypeAttributes(visualizationType, id);
    }

    @RequestMapping(value = "/{idOfLibrary}/methods/{idOfType}/validateConfiguration", method = RequestMethod.POST)
    public ValidateVisualizationTypeConfigurationResponse validateMethodConfiguration(@PathVariable String idOfLibrary, @PathVariable String idOfType, @RequestBody ValidateVisualizationTypeConfigurationRequest validateVisualizationTypeConfigurationRequest) {
        ValidateVisualizationTypeConfigurationResponse response = new ValidateVisualizationTypeConfigurationResponse();
        try {
            response.setConfigurationValid(visualizationFrameworkService.validateVisualizationTypeConfiguration(idOfType, validateVisualizationTypeConfigurationRequest.getConfigurationMapping()));
        } catch (DataSetValidationException exception) {
            response.setConfigurationValid(false);
            response.setValidationMessage(exception.getMessage());
        }
        catch (Exception exception) {
            response.setConfigurationValid(false);
            response.setValidationMessage(exception.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "/{idOfLibrary}/methods/{idOfType}/configuration", method = RequestMethod.GET)
    public VisualizationTypeConfigurationResponse getTypeConfiguration(@PathVariable String idOfLibrary, @PathVariable String idOfType) {
        VisualizationTypeConfigurationResponse response = new VisualizationTypeConfigurationResponse();
        response.setTypeConfiguration(visualizationFrameworkService.getTypeConfiguration(idOfType));
        return response;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<VisualizationLibrary> getVisualizationLibraries() {
        return visualizationFrameworkService.findAllVisualizationLibraries();
    }


    @RequestMapping(value = "/PopulateVisualizations", method = RequestMethod.GET)
    public boolean populateVisualizations() {
        return visualizationFrameworkService.populateVisualizations();
    }

    @ExceptionHandler(VisualizationLibraryUploadException.class)
    public ResponseEntity<Object> handleVisualizationLibraryUploadException(VisualizationLibraryUploadException exception, HttpServletRequest request) {
        BaseErrorDTO error = BaseErrorDTO.createBaseErrorDTO(exception.getMessage(), "", "");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(error, headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(VisualizationLibraryDeletionException.class)
    public ResponseEntity<Object> handleVisualizationLibraryDeletionException(VisualizationLibraryDeletionException exception, HttpServletRequest request) {
        BaseErrorDTO error = BaseErrorDTO.createBaseErrorDTO(exception.getMessage(), "", "");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(error, headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(VisualizationLibraryNotFoundException.class)
    public ResponseEntity<Object> handleVisualizationLibraryNotFoundException(VisualizationLibraryNotFoundException exception, HttpServletRequest request) {
        BaseErrorDTO error = BaseErrorDTO.createBaseErrorDTO(exception.getMessage(), "", "");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(error, headers, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(VisualizationTypeNotFoundException.class)
    public ResponseEntity<Object> handleVisualizationMethodNotFoundException(VisualizationTypeNotFoundException exception, HttpServletRequest request) {
        BaseErrorDTO error = BaseErrorDTO.createBaseErrorDTO(exception.getMessage(), "", "");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(error, headers, HttpStatus.NOT_FOUND);
    }

}
