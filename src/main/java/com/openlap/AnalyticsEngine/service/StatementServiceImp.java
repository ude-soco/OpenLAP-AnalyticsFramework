package com.openlap.AnalyticsEngine.service;

import java.lang.reflect.Array;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mchange.v1.util.ArrayUtils;
import com.openlap.AnalyticsEngine.dto.Platforms;
import org.apache.commons.collections4.map.MultiValueMap;
import org.apache.lucene.util.ArrayUtil;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.openlap.AnalyticsEngine.dto.OpenLapDataConverter;
import com.openlap.AnalyticsEngine.dto.QueryParameters;
import com.openlap.AnalyticsEngine.dto.Verb;
import com.openlap.AnalyticsEngine.model.Statement;
import com.openlap.AnalyticsEngine.repo.StatementRepo;

import com.openlap.dataset.OpenLAPColumnDataType;
import com.openlap.dataset.OpenLAPDataSet;
import com.openlap.exceptions.OpenLAPDataColumnException;

@Service
public class StatementServiceImp implements StatementService {
	@Autowired
	private StatementRepo statementsRepo;

	public OpenLAPDataSet getallplatforms(ObjectId organizationId, ObjectId lrsId)
			throws JSONException, OpenLAPDataColumnException {
		OpenLapDataConverter dataConveter = new OpenLapDataConverter();

		ArrayList<Object> listOfplatforms = new ArrayList<Object>();
		for (Platforms platforms : statementsRepo.findallplatformsByOrganizationAndLrs(organizationId, lrsId)){
			String statement = new Gson().toJson(platforms.getStatement());
			JSONObject statementObject = new JSONObject(statement);
			JSONObject platformObject = statementObject.getJSONObject("context");

			if (!listOfplatforms.contains(platformObject.get("platform"))) {
				listOfplatforms.add(platformObject.get("platform"));
				System.out.println(listOfplatforms);
			}
			break;
		}
		dataConveter.SetOpenLapDataColumn("Platform", OpenLAPColumnDataType.Text, true, listOfplatforms, "", "");
		OpenLAPDataSet xApiPlatformDataset = dataConveter.getDataSet();
		return xApiPlatformDataset;
	}
	@Override
	public OpenLAPDataSet getAllVerbsFromStatements(ObjectId organizationId, ObjectId lrsId)
			throws JSONException, OpenLAPDataColumnException {

		OpenLapDataConverter dataConveter = new OpenLapDataConverter();

		ArrayList<Object> listOfVerbs = new ArrayList<Object>();
		ArrayList<Object> listOfVerbsIds = new ArrayList<Object>();
		for (Verb verb : statementsRepo.findAllVerbsByOrganizationAndLrs(organizationId, lrsId)) {

			String statement = new Gson().toJson(verb.getStatement());
			JSONObject statementObject = new JSONObject(statement);
			JSONObject verbObject = statementObject.getJSONObject("verb");
			if (!listOfVerbsIds.contains(verbObject.get("id"))) {
				listOfVerbsIds.add(verbObject.get("id"));
			}

			JSONObject displayObject = verbObject.getJSONObject("display");
			Iterator<?> displaykey = displayObject.keys();

			while (displaykey.hasNext()) {
				// loop to get the dynamic key
				String DynamicLanguageKey = (String) displaykey.next();

				// get the value of the dynamic key
				if (!listOfVerbs.contains(displayObject.get(DynamicLanguageKey))) {
					listOfVerbs.add(displayObject.get(DynamicLanguageKey));
				}

			}

		}
		dataConveter.SetOpenLapDataColumn("VerbDisplayNames", OpenLAPColumnDataType.Text, true, listOfVerbs, "", "");
		dataConveter.SetOpenLapDataColumn("VerbIds", OpenLAPColumnDataType.Text, true, listOfVerbsIds, "", "");
		OpenLAPDataSet xAPIVerbsOpenLAPDataSet = dataConveter.getDataSet();
		return xAPIVerbsOpenLAPDataSet;
	}

	@Override
	public OpenLAPDataSet getAllStatementsByCustomQuery(ObjectId organizationId, ObjectId lrsId,
														QueryParameters queryParameters) throws JSONException, OpenLAPDataColumnException, JsonProcessingException {

		//MultiValueMap map = new MultiValueMap();

		Map<String, ArrayList<Object>> map = new HashMap();
		String[] xAPIObjectsToReturn = null;
		OpenLapDataConverter dataConveter = new OpenLapDataConverter();
		Gson gson = new Gson();
		ObjectMapper objectMapper = new ObjectMapper();


		/**
		 * Converting Java Object to Json
		 */
		String query = gson.toJson(queryParameters.getQuery());
		String statementDuration = gson.toJson(queryParameters.getStatementDuration());
		String parametersToReceive = gson.toJson(queryParameters.getParametersToBeReturnedInResult());

		/**
		 * Converting Json to DBObject for MongoDB
		 */
		DBObject queryObject = (DBObject) JSON.parse(query);
		@SuppressWarnings("deprecation")
		DBObject statementDurationObject = (DBObject) JSON.parse(statementDuration);
		@SuppressWarnings("deprecation")
		DBObject parametersToReceiveObject = (DBObject) JSON.parse(parametersToReceive);

		// Converting Returned parameters given in the query to JsonObject
		JSONObject xAPIStatement = new JSONObject(parametersToReceive);
		// Extracting keys from xAPIStatement Object
		Iterator<?> xAPIStatementProperties = xAPIStatement.keys();
		ArrayList<String> listOfReturnedxAPIStatementProperties = new ArrayList<String>();
		while (xAPIStatementProperties.hasNext()) {
			// loop to get the dynamic key
			String valuesToReturn = (String) xAPIStatementProperties.next();
			listOfReturnedxAPIStatementProperties.add(valuesToReturn);
		}


		/*
		 * Getting query results from database and returning in the form of List of
		 * Statement Model.And reading each statement one by one
		 */
		List<Statement>  allStatements = statementsRepo.findDataByCustomQuery(queryObject, statementDurationObject,
				parametersToReceiveObject, organizationId, lrsId);

		for (Statement statment : allStatements) {

			/**
			 * Getting statement object from Statement Collection and converting to
			 * JsonObject
			 */

			String statement = new Gson().toJson(statment.getStatement());
			JSONObject statementObject = new JSONObject(statement);
			JSONObject xAPIObject = null;
			// looping through List of parameters available in parametersToReceive Object
			for (int countReturnParamters = 0; countReturnParamters < listOfReturnedxAPIStatementProperties
					.size(); countReturnParamters++) {
				/*
				 * getting return keys like (statement.verb.name or statement.actor.name) from
				 * list
				 */

				String returnKeys = listOfReturnedxAPIStatementProperties.get(countReturnParamters);
				/*
				 * Splitting return keys(statement.verb.name or statement.actor.name) with dot
				 * operator to extract statement,verb,name or statement,actor,name separately
				 *
				 */

				xAPIObjectsToReturn = returnKeys.split("\\.");

				int returnObjectCount;

				// Reading each Object like statement,verb,actor these are three objects reading
				// them one by one from returned results from database and reading only those
				// objects that are given in parametersToReceive
				for (returnObjectCount = 1; returnObjectCount < xAPIObjectsToReturn.length - 1; returnObjectCount++) {
					if (returnObjectCount == 1) {
						xAPIObject = statementObject.getJSONObject(xAPIObjectsToReturn[returnObjectCount]);
					} else {
						xAPIObject = xAPIObject.getJSONObject(xAPIObjectsToReturn[returnObjectCount]);
					}
				}
				/**
				 * After reading setting name of objects to be returned as key and values of
				 * those objects as values in multimap
				 */

				if (xAPIObject.has(xAPIObjectsToReturn[xAPIObjectsToReturn.length - 1])) {

			//		if (!map.containsValue(xAPIObject.get(xAPIObjectsToReturn[xAPIObjectsToReturn.length - 1])))
					if (map.containsKey(returnKeys))
						map.get(returnKeys).add(xAPIObject.get(xAPIObjectsToReturn[xAPIObjectsToReturn.length - 1]));
					else {
						map.put(returnKeys, new ArrayList<Object>());
						map.get(returnKeys).add(xAPIObject.get(xAPIObjectsToReturn[xAPIObjectsToReturn.length - 1]));
					}//map.put(returnKeys, xAPIObject.get(xAPIObjectsToReturn[xAPIObjectsToReturn.length - 1]));

					}
			}

		}


		/**
		 * Iterating throw multimap keys and values to convert the data into
		 * OpenLAP-DataSET
		 */

		Set<String> columnNames = map.keySet();

		for(String columnName: columnNames){
			dataConveter.SetOpenLapDataColumn(columnName,	OpenLAPColumnDataType.Text, true, map.get(columnName), "", "");
		}


		/*List<Object> xAPIObjectsList;
		Set<?> entrySet = map.entrySet();
		Iterator<?> it = entrySet.iterator();
		ArrayList<Object> listOfxAPIObjectKeys = new ArrayList<Object>();
		ArrayList<Object> listOfxAPIObjectValues;
		ArrayList<String> mylist = null;
		while (it.hasNext()) {
			Map.Entry mapEntry = (Map.Entry) it.next();
			xAPIObjectsList = (List) map.get(mapEntry.getKey());

			for (int xAPIObjectCount = 0; xAPIObjectCount < xAPIObjectsList.size(); xAPIObjectCount++) {
				if (!listOfxAPIObjectKeys.contains(mapEntry.getKey().toString())) {
					listOfxAPIObjectKeys.add(mapEntry.getKey().toString());

					listOfxAPIObjectValues = new ArrayList<Object>();
					listOfxAPIObjectValues.add(mapEntry.getValue());
					String xapi = objectMapper.writeValueAsString(listOfxAPIObjectValues);
					String xapitrim = xapi.substring(2,xapi.length()-2);
					mylist = new ArrayList<String>(Arrays.asList(xapitrim));

					dataConveter.SetOpenLapDataColumn(mapEntry.getKey().toString(),	OpenLAPColumnDataType.Text, true, mylist, "", "");
				}
			}
		}*/
		OpenLAPDataSet queryResults = dataConveter.getDataSet();
		return queryResults;
	}

}
