package com.openlap.AnalyticsEngine.controller;

import java.io.IOException;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.openlap.AnalyticsEngine.service.LrsService;

import com.openlap.dataset.OpenLAPDataSet;
import com.openlap.exceptions.OpenLAPDataColumnException;

@RestController
@RequestMapping("/v1/lrs/")
public class LrsController {

	@Autowired
	private LrsService lrsService;

	/**
	 * 
	 * @param organizationId
	 * @return List of LRS of given organization
	 * @throws IOException
	 * @throws OpenLAPDataColumnException
	 * @throws JSONException
	 */
	@PreAuthorize("hasRole('site_admin')")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public OpenLAPDataSet listOfLrsByOrganization(@RequestParam("OrganizationId") ObjectId organizationId)
			throws IOException, OpenLAPDataColumnException, JSONException {
		return lrsService.listOfLrsByOrganization(organizationId);
	}
}
