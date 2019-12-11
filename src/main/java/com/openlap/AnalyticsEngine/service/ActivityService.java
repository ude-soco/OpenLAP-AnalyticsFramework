package com.openlap.AnalyticsEngine.service;

import org.bson.types.ObjectId;
import org.json.JSONException;

import com.openlap.dataset.OpenLAPDataSet;
import com.openlap.exceptions.OpenLAPDataColumnException;

public interface ActivityService {
	OpenLAPDataSet getActivities(ObjectId OrganizationId, ObjectId lrsId)
			throws OpenLAPDataColumnException, JSONException;

	OpenLAPDataSet getActivitiesExtensionContextValues(ObjectId OrganizationId, ObjectId lrsId,
			String extensionId, String extensionContextKey)
			throws OpenLAPDataColumnException, JSONException;
}
