package com.sap.services;

import com.sap.exceptions.SAPException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Base64;

@Component
public class OAuthClientImpl implements OAuthClient {

    private static final Logger log = LoggerFactory.getLogger(OAuthClientImpl.class);
    private final RestTemplate restTemplate;

    @Value("${auth.tokenEndpoint}")
    private String tokenEndpoint;

    @Value("${auth.clientId}")
    private String client_id;

    @Value("${auth.clientSecret}")
    private String client_secret;

    @Value("${auth.grantType}")
    private String grant_type;

    @Value("${auth.scope}")
    private String scope;


    @Autowired
    public OAuthClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getToken() {
        log.info("Getting OAuth token started...");

        final String b = "grant_type=" + grant_type + "&scope=" + scope;
        final String base64Credentials = Base64.getEncoder().encodeToString((client_id + ":"
                + client_secret).getBytes());

        final RequestEntity<String> request = RequestEntity.post(URI.create(tokenEndpoint))
                .header("Authorization", "Basic " + base64Credentials)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(b);

        log.info("Request: " + request);

        try {
            final ResponseEntity<String> responseEntity = restTemplate.postForEntity(tokenEndpoint, request, String.class);
            final JSONObject response = new JSONObject(responseEntity.getBody());
            return response.getString("access_token");
        } catch (RestClientException e) {
            throw new SAPException("Retrieving token for ads rest service failed: " + e.getMessage());
        } finally {
            log.info("Getting OAuth token finished...");
        }
    }

}
