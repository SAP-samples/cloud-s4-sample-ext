package com.sap.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalTime;

public class LocalTimeSerializer extends JsonSerializer<LocalTime> {
    @Override
    public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        final String timeString = value.toString();

        gen.writeString(timeString);
    }
}
