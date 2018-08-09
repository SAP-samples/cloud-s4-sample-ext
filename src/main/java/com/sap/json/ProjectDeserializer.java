package com.sap.json;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.sap.model.Project;

import java.io.IOException;

import static com.sap.config.Constants.ENGAGEMENT_PROJECT;
import static com.sap.config.Constants.ENGAGEMENT_PROJECT_NAME;

public class ProjectDeserializer extends JsonDeserializer<Project> {

    @Override
    public Project deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectCodec codec = p.getCodec();
        JsonNode node = codec.readTree(p);
        return Project.create(node.get(ENGAGEMENT_PROJECT).asText(),
                node.get(ENGAGEMENT_PROJECT_NAME).asText());
    }
}
