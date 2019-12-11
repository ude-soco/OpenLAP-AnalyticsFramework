package com.openlap.AnalyticsEngine.service;

import java.io.IOException;

import org.bson.types.ObjectId;

import com.openlap.dataset.OpenLAPDataSet;
import com.openlap.exceptions.OpenLAPDataColumnException;

public interface PersonasService {
	OpenLAPDataSet listOfPersonNamesByOrganization(ObjectId OrganizationId)
			throws IOException, OpenLAPDataColumnException;
}
