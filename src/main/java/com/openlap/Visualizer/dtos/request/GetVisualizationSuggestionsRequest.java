package com.openlap.Visualizer.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.openlap.dataset.OpenLAPDataSet;
import com.openlap.dataset.OpenLAPPortConfig;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetVisualizationSuggestionsRequest {

    private OpenLAPPortConfig olapPortConfiguration;
    private OpenLAPDataSet dataSetConfiguration;

    public OpenLAPPortConfig getOlapPortConfiguration() {
        return olapPortConfiguration;
    }

    public void setOlapPortConfiguration(OpenLAPPortConfig olapPortConfiguration) {
        this.olapPortConfiguration = olapPortConfiguration;
    }

    public OpenLAPDataSet getDataSetConfiguration() {
        return dataSetConfiguration;
    }

    public void setDataSetConfiguration(OpenLAPDataSet dataSetConfiguration) {
        this.dataSetConfiguration = dataSetConfiguration;
    }
}
