package com.sap.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.sap.config.Constants;
import com.sap.model.Customer;

import java.io.IOException;

import static com.sap.config.Constants.CUSTOMER;

public class CustomerDeserializer extends JsonDeserializer<Customer> {

    @Override
    public Customer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectCodec codec = p.getCodec();
        JsonNode node = codec.readTree(p);
        return Customer.create(node.get(CUSTOMER).asText(),
                node.get(Constants.CUSTOMER_FULL_NAME).asText());
    }
}
