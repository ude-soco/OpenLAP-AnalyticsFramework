package com.openlap.AnalyticsEngine.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import java.io.IOException;

/**
 * An object Mapper for the DataAccessLayer to convert an IndicatorQuery to a String during persistence
 * operations
 */
public class IndicatorQueryConverter implements AttributeConverter<Indicator, String> {

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Indicator indicator) {
        try {
            return mapper.writeValueAsString(indicator);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return indicator.toString();
        }
    }

    @Override
    public Indicator convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, Indicator.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
