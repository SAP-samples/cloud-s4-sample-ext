package com.sap.integration;

import java.net.Proxy;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public interface DestinationService {

    public String getDestinationURL(String destinationName);

    public Proxy getProxy(String SAPProxyType);

    @SuppressWarnings("unchecked")
    public <T> HttpEntity<T> getHttpEntity(String destinationName, T requestObject,
	    MultiValueMap<String, String>... headers);

    public MultiValueMap<String, String> getNewHeaders(RestTemplate restTemplate, String url, String destinationName);
    
    public void printHeaders(MultiValueMap<String, String> headers);
}
