package com.openlap.AnalyticsEngine.service;

import java.io.IOException;

import org.json.JSONArray;

import com.fasterxml.jackson.databind.MappingIterator;
import com.openlap.AnalyticsEngine.dto.XapiStatement;


public interface CsvToJsonService {

	public MappingIterator<XapiStatement> readStatementsFromCsv(String InptFile);

	public JSONArray convertCsvStatementsToXapiStatements(MappingIterator<XapiStatement> csvStatements) throws IOException;
}
