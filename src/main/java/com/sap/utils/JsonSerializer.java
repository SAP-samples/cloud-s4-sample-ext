package com.sap.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.sap.json.CustomerDeserializer;
import com.sap.json.ProjectDeserializer;
import com.sap.json.WorkPackageDeserializer;
import com.sap.json.WorkforceDeserializer;
import com.sap.model.Customer;
import com.sap.model.Project;
import com.sap.model.WorkPackage;
import com.sap.model.Workforce;

import java.io.IOException;
import java.util.Optional;

public class JsonSerializer<T> {

    public static String toJsonString(Object obj) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }

    public static String readValueFromPath(String json, String path) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode root = mapper.readTree(json);
        return root.at(path).asText("");
    }

    public static <T> Optional<T> unmarshallFrom(String json, Class<T> valueType) {
        final ObjectMapper mapper = new ObjectMapper();
        final SimpleModule module = new SimpleModule("CustomModule", new Version(1, 0, 0, "", "", ""));
        module.addDeserializer(Workforce.class, new WorkforceDeserializer());
        module.addDeserializer(Project.class, new ProjectDeserializer());
        module.addDeserializer(Customer.class, new CustomerDeserializer());
        module.addDeserializer(WorkPackage.class, new WorkPackageDeserializer());
        mapper.registerModule(module);

        try {
            return Optional.of(mapper.readValue(json, valueType));
        } catch (IOException e) {
            throw new RuntimeException("Could not create object from json:" + e);
        }
    }

    public static Optional<String> getSerializedEntity(Object entity) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return Optional.of(mapper.writeValueAsString(entity));
        } catch (Exception ex) {
            throw new RuntimeException("Could not serialize object:" + ex);
        }
    }

}
