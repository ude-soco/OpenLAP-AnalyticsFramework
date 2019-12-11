package com.openlap.AnalyticsMethods.exceptions;

import com.openlap.OpenLAPAnalyaticsFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom generic Exception for AnalyticsMethods.
 */
public abstract class AnalyticsMethodException extends Exception {

    protected static final Logger log =
            LoggerFactory.getLogger(OpenLAPAnalyaticsFramework.class);

    public AnalyticsMethodException(String message) {
        super(message);
    }
}
