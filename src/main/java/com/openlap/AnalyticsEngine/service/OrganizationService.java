package com.openlap.AnalyticsEngine.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.openlap.dataset.OpenLAPDataSet;
import com.openlap.exceptions.OpenLAPDataColumnException;

public interface OrganizationService {
	OpenLAPDataSet getOrganizationForLoggedUser(Authentication authentication)
			throws IOException, OpenLAPDataColumnException;
}
