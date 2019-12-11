package com.openlap.AnalyticsEngine.dto.Response;

import com.openlap.AnalyticsEngine.model.Indicator;
import com.openlap.AnalyticsModules.model.AnalyticsMethodReference;
import com.openlap.AnalyticsModules.model.IndicatorReference;
import com.openlap.AnalyticsModules.model.OpenLAPDataSetMergeMapping;
import com.openlap.AnalyticsModules.model.VisualizerReference;
import com.openlap.dataset.OpenLAPPortConfig;

import java.util.List;
import java.util.Map;

public class IndicatorResponse {
    private String id;
    private String name;
    private Map<String, String> query;
    private IndicatorReference indicatorReference;
    private AnalyticsMethodReference analyticsMethodReference;
    private VisualizerReference visualizationReference;
    private Map<String, OpenLAPPortConfig> queryToMethodConfig;
    private OpenLAPPortConfig methodToVisualizationConfig;
    private Map<String, String> methodInputParams;
    private Map<String, String> visualizationInputParams;
    private Map<String, Object> additionalParams;
    List<OpenLAPDataSetMergeMapping> dataSetMergeMappingList;
    private String parameters;
    private String indicatorType;
    private String createdBy;

    public IndicatorResponse() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getQuery() {
        return this.query;
    }

    public void setQuery(Map<String, String> query) {
        this.query = query;
    }

    public IndicatorReference getIndicatorReference() {
        return this.indicatorReference;
    }

    public void setIndicatorReference(IndicatorReference indicatorReference) {
        this.indicatorReference = indicatorReference;
    }

    public AnalyticsMethodReference getAnalyticsMethodReference() {
        return this.analyticsMethodReference;
    }

    public void setAnalyticsMethodReference(AnalyticsMethodReference analyticsMethodReference) {
        this.analyticsMethodReference = analyticsMethodReference;
    }

    public VisualizerReference getVisualizationReference() {
        return this.visualizationReference;
    }

    public void setVisualizationReference(VisualizerReference visualizationReference) {
        this.visualizationReference = visualizationReference;
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

    public Map<String, Object> getAdditionalParams() {
        return this.additionalParams;
    }

    public void setAdditionalParams(Map<String, Object> additionalParams) {
        this.additionalParams = additionalParams;
    }

    public String getParameters() {
        return this.parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getIndicatorType() {
        return this.indicatorType;
    }

    public void setIndicatorType(String indicatorType) {
        this.indicatorType = indicatorType;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public List<OpenLAPDataSetMergeMapping> getDataSetMergeMappingList() {
        return this.dataSetMergeMappingList;
    }

    public void setDataSetMergeMappingList(List<OpenLAPDataSetMergeMapping> dataSetMergeMappingList) {
        this.dataSetMergeMappingList = dataSetMergeMappingList;
    }
}
