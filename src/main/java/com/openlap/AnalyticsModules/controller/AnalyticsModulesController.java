package com.openlap.AnalyticsModules.controller;

import com.openlap.AnalyticsMethods.exceptions.AnalyticsMethodsBadRequestException;
import com.openlap.AnalyticsMethods.exceptions.AnalyticsMethodsUploadErrorException;
import com.openlap.AnalyticsMethods.model.AnalyticsMethods;
import com.openlap.AnalyticsModules.exceptions.AnalyticsGoalNotFoundException;
import com.openlap.AnalyticsModules.exceptions.AnalyticsModulesBadRequestException;
import com.openlap.AnalyticsModules.exceptions.TriadNotFoundException;
import com.openlap.AnalyticsModules.model.AnalyticsGoal;
import com.openlap.AnalyticsModules.model.Triad;
import com.openlap.AnalyticsModules.service.AnalyticsModulesService;
import com.openlap.Common.controller.GenericResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * A spring Controller that acts as a facade, exposing an API for handling JSON requests to the Analytics Modules
 * macro component of the OpenLAP
 *
 * Created by Faizan Riaz 12.06.2019
 */
@Controller
@RequestMapping("/analyticsmodule/")
public class AnalyticsModulesController {

    public static final String ANALYTICS_GOAL_ACTION_ACTIVATE = "activate";
    public static final String ANALYTICS_GOAL_ACTION_DEACTIVATE = "deactivate";
    @Autowired
    AnalyticsModulesService modulesService;

    //region Triads

    /**
     * HTTP endpoint handler method to save a Triad.
     *
     * @param triad to be saved
     * @return JSON representation of the saved Triad with an ID.
     */
    @RequestMapping(
            value = "/AnalyticsModules/Triads/",
            method = RequestMethod.POST
    )
    public
    @ResponseBody
    Triad saveTriad(@RequestBody Triad triad) {
        return modulesService.saveTriad(triad);
    }

    /**
     * HTTP endpoint handler method to get a Triad by its ID.
     *
     * @param id of the requested Triad
     * @return JSON representation of the Triad with the requested ID
     */
    @RequestMapping(
            value = "/AnalyticsModules/Triads/{id}",
            method = RequestMethod.GET
    )
    public
    @ResponseBody
    Triad getTriadById(@PathVariable String id) {
        return modulesService.getTriadById(id);
    }

    /**
     * HTTP endpoint handler method to get all Triads
     *
     * @return JSON representation of all the Triads
     */

    @RequestMapping(
            value = "/AnalyticsModules/TriadsByUser",
            method = RequestMethod.GET
    )
    public
    @ResponseBody
    List<Triad> getTriadsByUser(@RequestParam String userName) {
        return modulesService.getTriadsByUser(userName);
    }

    @RequestMapping(
            value = "/AnalyticsModules/Triads",
            method = RequestMethod.GET
    )
    public
    @ResponseBody
    List<Triad> getAllTriads() {
        return modulesService.getAllTriads();
    }


    /**
     * HTTP endpoint handler method for updating Triad
     *
     * @param triad Data of the Triad to be updated.
     * @param id    of the Triad to be updated
     * @return updated Triad
     */
    @RequestMapping(
            value = "/AnalyticsModules/Triads/{id}",
            method = RequestMethod.PUT
    )
    public
    @ResponseBody
    Triad updateTriad(@RequestBody Triad triad,
                      @PathVariable String id) {
        return modulesService.updateTriad(triad, id);
    }

    /**
     * HTTP endpoint handler method for deleting Triad
     *
     * @param id id of the Triad to be deleted
     * @return GenericResponseDTO with deletion confirmation
     */
    @RequestMapping(
            value = "/AnalyticsModules/Triads/{id}",
            method = RequestMethod.DELETE
    )
    public
    @ResponseBody
    GenericResponseDTO deleteTriad(@PathVariable String id) {
        modulesService.deleteTriad(id);
        return new GenericResponseDTO(HttpStatus.OK.value(),
                "Triad with id {" + id + "} deleted");
    }

    //endregion

    //region AnalyticsGoals

    @RequestMapping(
            value = "/AnalyticsModules/AnalyticsGoals/PopulateSampleGoals",
            method = RequestMethod.GET
    )
    public
    @ResponseBody
    boolean populateSampleGoals(){
    return modulesService.populateSampleGoals();
    }


    /**
     * HTTP endpoint handler method to get a AnalyticsGoal by its ID.
     *
     * @param id of the requested AnalyticsGoal
     * @return JSON representation of the AnalyticsGoal with the requested ID
     */
    @RequestMapping(
            value = "/AnalyticsModules/AnalyticsGoals/{id}",
            method = RequestMethod.GET
    )
    public
    @ResponseBody
    AnalyticsGoal getAnalyticsGoalById(@PathVariable String id) {
        return modulesService.getAnalyticsGoalById(id);
    }

    /**
     * HTTP endpoint handler method to save a AnalyticsGoal.
     *
     * @param AnalyticsGoal to be saved
     * @return JSON representation of the saved AnalyticsGoal with an ID.
     */
    @RequestMapping(
            value = "/AnalyticsModules/AnalyticsGoals/",
            method = RequestMethod.POST
    )
    public
    @ResponseBody
    AnalyticsGoal saveAnalyticsGoal(@RequestBody AnalyticsGoal AnalyticsGoal) {
        return modulesService.saveAnalyticsGoal(AnalyticsGoal);
    }

    /**
     * HTTP endpoint handler method for Activating/Deactivating a AnalyticsGoal
     *
     * @param id     of the AnalyticsGoal
     * @param action "activate" or "deactivate"
     * @return the updated AnalyticsGoal with the sent status
     */
    @RequestMapping(
            value = "/AnalyticsModules/AnalyticsGoals/{id}/{action}",
            method = RequestMethod.PUT
    )
    public
    @ResponseBody
    AnalyticsGoal authorizeAnalyticsGoal(@PathVariable String id, @PathVariable String action) {
        if (action.equals(ANALYTICS_GOAL_ACTION_ACTIVATE)) {
            return modulesService.setAnalyticsGoalActive(id, true);
        } else if (action.equals(ANALYTICS_GOAL_ACTION_DEACTIVATE)) {
            return modulesService.setAnalyticsGoalActive(id, false);
        } else throw new AnalyticsMethodsBadRequestException("Invalid request for Analytics Goal");
    }

    /**
     * HTTP endpoint handler method to get all AnalyticsGoals
     *
     * @return JSON representation of all the AnalyticsGoals
     */
    @RequestMapping(
            value = "/AnalyticsModules/AnalyticsGoals/",
            method = RequestMethod.GET
    )
    public
    @ResponseBody
    List<AnalyticsGoal> getAllAnalyticsGoals() {
        return modulesService.getAllAnalyticsGoals();
    }

    /**
     * HTTP endpoint handler method to get all AnalyticsGoals
     *
     * @return JSON representation of all the AnalyticsGoals
     */
    @RequestMapping(
            value = "/AnalyticsModules/ActiveAnalyticsGoals/",
            method = RequestMethod.GET
    )
    public
    @ResponseBody
    List<AnalyticsGoal> getActiveAnalyticsGoals() {
        return modulesService.getActiveAnalyticsGoals();
    }

    /**
     * HTTP endpoint handler method for attaching an AnalyticsMethod to a AnalyticsGoal
     *
     * @param AnalyticsGoalId         id of the AnalyticsGoal
     * @param analyticsMethods of the AnalyticsMethod to be related with the AnalyticsGoal
     * @return the AnalyticsGoal with the attached analyticsMethodMetadata
     */
    @RequestMapping(
            value = "/AnalyticsModules/AnalyticsGoals/{AnalyticsGoalId}/addAnalyticsMethod",
            method = RequestMethod.PUT
    )
    public
    @ResponseBody
    AnalyticsGoal addAnalyticsMethodToAnalyticsGoal(
            @PathVariable String AnalyticsGoalId,
            @RequestBody AnalyticsMethods analyticsMethods) {
        return modulesService.addAnalyticsMethodToAnalyticsGoal(AnalyticsGoalId, analyticsMethods);
    }

    /**
     * HTTP endpoint handler method for updating AnalyticsGoal
     *
     * @param AnalyticsGoal Data of the AnalyticsGoal to be updated. Note that the isActive, id and the AnalyticsMethods
     *                      will not be updated using this method.
     * @param id            of the AnalyticsGoal to be updated
     * @return updated AnalyticsGoal
     */
    @RequestMapping(
            value = "/AnalyticsModules/AnalyticsGoals/{id}",
            method = RequestMethod.PUT
    )
    public
    @ResponseBody
    AnalyticsGoal updateAnalyticsGoal(@RequestBody AnalyticsGoal AnalyticsGoal,
                                      @PathVariable String id) {
        return modulesService.updateAnalyticsGoal(AnalyticsGoal, id);
    }

    /**
     * HTTP endpoint handler method for deleting AnalyticsGoal
     *
     * @param id id of the AnalyticsGoal to be deleted
     * @return GenericResponseDTO with deletion confirmation
     */
    @RequestMapping(
            value = "/AnalyticsModules/AnalyticsGoals/{id}",
            method = RequestMethod.DELETE
    )
    public
    @ResponseBody
    GenericResponseDTO deleteAnalyticsGoal(@PathVariable String id) {
        modulesService.deleteAnalyticsGoal(id);
        return new GenericResponseDTO(HttpStatus.OK.value(),
                "Analytics Goal with id {" + id + "} deleted");
    }
    //endregion

    //region ExceptionHandlers

    /**
     * Handler for TriadNotFoundException, AnalyticsMethodsUploadErrorExceptio and AnalyticsGoalNotFoundException.
     * It returns the appropriate HTTP Error code.
     *
     * @param e       exception
     * @param request HTTP request
     * @return A GenericResponseDTO with the information about the exception and its cause.
     */
    @ExceptionHandler({TriadNotFoundException.class,
            AnalyticsMethodsUploadErrorException.class,
            AnalyticsGoalNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public
    @ResponseBody
    GenericResponseDTO handleMethodNotFoundException(Exception e,
                                                     HttpServletRequest request) {
        GenericResponseDTO errorObject = new GenericResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                e.getClass().getName(),
                e.getMessage(),
                request.getServletPath()
        );

        return errorObject;
    }

    /**
     * Handler for AnalyticsModulesBadRequestException.
     * It returns the appropriate HTTP Error code.
     *
     * @param e       exception
     * @param request HTTP request
     * @return A GenericResponseDTO with the information about the exception and its cause.
     */
    @ExceptionHandler(AnalyticsModulesBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public
    @ResponseBody
    GenericResponseDTO handleMethodNotFoundException(AnalyticsModulesBadRequestException e,
                                                     HttpServletRequest request) {
        GenericResponseDTO errorObject = new GenericResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                e.getClass().getName(),
                e.getMessage(),
                request.getServletPath()
        );

        return errorObject;
    }

    //endregion
}
