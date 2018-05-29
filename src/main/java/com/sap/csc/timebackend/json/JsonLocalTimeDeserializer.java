package com.sap.csc.timebackend.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalTime;

public class JsonLocalTimeDeserializer extends JsonDeserializer<LocalTime> {
    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final String time = p.getText();
        if (time.isEmpty()) {
            return LocalTime.parse("00:00");
        }
        return LocalTime.parse(time);
    }
}
