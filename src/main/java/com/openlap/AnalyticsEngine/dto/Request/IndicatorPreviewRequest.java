package com.openlap.AnalyticsEngine.dto.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.openlap.AnalyticsEngine.dto.QueryParameters;
import com.openlap.AnalyticsModules.model.OpenLAPDataSetMergeMapping;
import com.openlap.dataset.OpenLAPPortConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
public class IndicatorPreviewRequest {
    private Map<String, QueryParameters> queries = new HashMap();
    private Map<String, String> analyticsMethodId = new HashMap();
    private String visualizationLibraryId;
    private String visualizationTypeId;
    private Map<String, OpenLAPPortConfig> queryToMethodConfig = new HashMap();
    private OpenLAPPortConfig methodToVisualizationConfig;
    private Map<String, String> methodInputParams;
    private Map<String, String> visualizationInputParams;
    private Map<String, Object> additionalParams;
    private String indicatorType;
    List<OpenLAPDataSetMergeMapping> dataSetMergeMappingList = new ArrayList();

    public IndicatorPreviewRequest() {
    }

    public Map<String, QueryParameters> getQueries() {
        return queries;
    }

    public void setQueries(Map<String, QueryParameters> queries) {
        this.queries = queries;
    }

    public Map<String, String> getAnalyticsMethodId() {
        return this.analyticsMethodId;
    }

    public void setAnalyticsMethodId(Map<String, String> analyticsMethodId) {
        this.analyticsMethodId = analyticsMethodId;
    }

    public String getVisualizationLibraryId() {
        return this.visualizationLibraryId;
    }

    public void setVisualizationLibraryId(String visualizationFrameworkId) {
        this.visualizationLibraryId = visualizationFrameworkId;
    }

    public String getVisualizationTypeId() {
        return this.visualizationTypeId;
    }

    public void setVisualizationTypeId(String visualizationTypeId) {
        this.visualizationTypeId = visualizationTypeId;
    }

    public Map<String, OpenLAPPortConfig> getQueryToMethodConfig() {
        return this.queryToMethodConfig;
    }

    public void setQueryToMethodConfig(Map<String, OpenLAPPortConfig> queryToMethodConfig) {
        this.queryToMethodConfig = queryToMethodConfig;
    }

    public OpenLAPPortConfig getMethodToVisualizationConfig() {
        return this.methodToVisualizationConfig;
    }

    public void setMethodToVisualizationConfig(OpenLAPPortConfig methodToVisualizationConfig) {
        this.methodToVisualizationConfig = methodToVisualizationConfig;
    }

    public Map<String, Object> getAdditionalParams() {
        return this.additionalParams;
    }

    public void setAdditionalParams(Map<String, Object> additionalParams) {
        this.additionalParams = additionalParams;
    }

    public String getIndicatorType() {
        return this.indicatorType;
    }

    public void setIndicatorType(String indicatorType) {
        this.indicatorType = indicatorType;
    }

    public Map<String, String> getMethodInputParams() {
        return this.methodInputParams;
    }

    public void setMethodInputParams(Map<String, String> methodInputParams) {
        this.methodInputParams = methodInputParams;
    }

    public Map<String, String> getVisualizationInputParams() {
        return this.visualizationInputParams;
    }

    public void setVisualizationInputParams(Map<String, String> visualizationInputParams) {
        this.visualizationInputParams = visualizationInputParams;
    }

    public List<OpenLAPDataSetMergeMapping> getDataSetMergeMappingList() {
        return this.dataSetMergeMappingList;
    }

    public void setDataSetMergeMappingList(List<OpenLAPDataSetMergeMapping> dataSetMergeMappingList) {
        this.dataSetMergeMappingList = dataSetMergeMappingList;
    }
}
