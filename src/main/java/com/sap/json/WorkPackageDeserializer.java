package com.sap.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.sap.model.WorkPackage;

import java.io.IOException;

import static com.sap.config.Constants.WORK_PACKAGE;
import static com.sap.config.Constants.WORK_PACKAGE_NAME;

public class WorkPackageDeserializer extends JsonDeserializer<WorkPackage> {

    @Override
    public WorkPackage deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectCodec codec = p.getCodec();
        JsonNode node = codec.readTree(p);
        return WorkPackage.create(node.get(WORK_PACKAGE).asText(), node.get(WORK_PACKAGE_NAME).asText()
        );
    }
}
