package com.openlap.AnalyticsEngine.dto.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
public class IndicatorSaveRequest extends IndicatorPreviewRequest {
    private String name;
    private String parameters;
    private String createdBy;
    private String indicatorClientID;
    private Map<String, Long> serverID = new HashMap();

    public IndicatorSaveRequest() {
    }

    public String getIndicatorClientID() {
        return this.indicatorClientID;
    }

    public void setIndicatorClientID(String indicatorClientID) {
        this.indicatorClientID = indicatorClientID;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParameters() {
        return this.parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Map<String, Long> getServerID() {
        return this.serverID;
    }

    public void setServerID(Map<String, Long> serverID) {
        this.serverID = serverID;
    }

}
