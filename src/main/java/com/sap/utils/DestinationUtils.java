package com.sap.utils;

import com.sap.integration.DestinationService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DestinationUtils {

    private static final Logger log = LoggerFactory.getLogger(DestinationUtils.class);
    private final DestinationService destinationService;
    private final RestTemplate destinationRestTemplate;

    @Autowired
    public DestinationUtils(DestinationService destinationService, RestTemplate destinationRestTemplate) {
        this.destinationService = destinationService;
        this.destinationRestTemplate = destinationRestTemplate;
    }

    public String getDestinationServiceUrlForPath(String destinationName, String entityPath) {

        final String destinationUrl = destinationService.getDestinationURL(destinationName);
        failIfDestinationUrlIsNull(destinationUrl);
        final String serviceUrl = destinationUrl + entityPath;
        log.info("Onpremise service-Url:" + serviceUrl);
        return serviceUrl;
    }

    private void failIfDestinationUrlIsNull(String destinationUrl) {
        if (Objects.isNull(destinationUrl))
            throw new RuntimeException("Could not reach destination -> " + null);
    }

    @SuppressWarnings("unchecked")
    public ResponseEntity<String> getDataFromServiceAsString(String destinationName, String reqUrl, HttpMethod method,
                                                             MultiValueMap<String, String> newHeaders) {
        ResponseEntity<String> responseEntity = null;
        try {
            log.info("Req url:" + reqUrl);
            destinationService.printHeaders(newHeaders);

            responseEntity = destinationRestTemplate.exchange(reqUrl, method,
                    destinationService.getHttpEntity(destinationName, String.class, newHeaders),
                    String.class);
            return responseEntity;

        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            throw new RuntimeException("An exception appeared while trying to replicate the data");
        }
    }

    public <U> List<U> convertODataJsonToListOfObjects(String dataAsJSON, Class<U> valueType) throws JSONException {

        JSONObject object = new JSONObject(dataAsJSON);
        JSONObject d = object.getJSONObject("d");
        JSONArray results = d.getJSONArray("results");
        List<U> eccDataObjects = new ArrayList<U>();
        log.info("Body:" + dataAsJSON);

        for (int i = 0; i < results.length(); i++) {
            JSONObject jsonObj = results.getJSONObject(i);
            Optional<U> maybeEccObject = JsonSerializer.unmarshallFrom(jsonObj.toString(), valueType);
            maybeEccObject.orElseThrow(
                    () -> new RuntimeException("Data from the OnPremise systems could not be unmarshalled."));
            eccDataObjects.add(maybeEccObject.get());
        }
        return eccDataObjects;
    }

    public <U> U convertODataJsonToObject(String dataAsJSON, Class<U> valueType) {
        JSONObject object = new JSONObject(dataAsJSON);
        JSONObject d = object.getJSONObject("d");
        log.info("Body:" + dataAsJSON);
        Optional<U> maybeEccObject = JsonSerializer.unmarshallFrom(d.toString(), valueType);

        return maybeEccObject.orElseThrow(
                () -> new RuntimeException("Data from the OnPremise systems could not be unmarshalled."));
    }

    public MultiValueMap<String, String> getNewHeaders(final String retrieveEccSalesOrgServiceURL, String destinationName) {
        final MultiValueMap<String, String> newHeaders = destinationService
                .getNewHeaders(destinationRestTemplate, retrieveEccSalesOrgServiceURL, destinationName);
        return newHeaders;
    }
}
