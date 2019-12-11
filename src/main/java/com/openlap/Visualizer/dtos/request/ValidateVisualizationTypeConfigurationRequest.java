package com.openlap.Visualizer.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.openlap.dataset.OpenLAPPortConfig;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidateVisualizationTypeConfigurationRequest {

    private OpenLAPPortConfig configurationMapping;

    public OpenLAPPortConfig getConfigurationMapping() {

        return configurationMapping;
    }

    public void setConfigurationMapping(OpenLAPPortConfig configurationMapping) {
        this.configurationMapping = configurationMapping;
    }
}
