package com.openlap.AnalyticsEngine.dto.Request;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.openlap.AnalyticsModules.model.AnalyticsGoal;

import java.util.List;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
public class QuestionSaveRequest {
    private String question;
    List<AnalyticsGoal> goal;
    private List<IndicatorSaveRequest> indicators;

    public QuestionSaveRequest() {
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<AnalyticsGoal> getGoal() {
        return this.goal;
    }

    public void setGoalID(List<AnalyticsGoal> goal) {
        this.goal = goal;
    }

    public List<IndicatorSaveRequest> getIndicators() {
        return this.indicators;
    }

    public void setIndicators(List<IndicatorSaveRequest> indicators) {
        this.indicators = indicators;
    }
}
