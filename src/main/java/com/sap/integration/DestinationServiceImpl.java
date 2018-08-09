package com.sap.integration;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.Charset;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cloud.account.TenantContext;
import com.sap.core.connectivity.api.configuration.ConnectivityConfiguration;
import com.sap.core.connectivity.api.configuration.DestinationConfiguration;

@Component
public class DestinationServiceImpl implements DestinationService {

	private static final String PASSWORD = "Password";

	private static final String USER = "User";

	private static final String JAVA_TENANT_CONTEXT_JNDI = "java:comp/env/TenantContext";

	private static final String URL = "URL";

	private static final String JAVA_CONNECTIVITY_CONFIGURATION_JNDI = "java:comp/env/connectivityConfiguration";

	private static final String ON_PREMISE_PROXY = "OnPremise";

	private static final Logger log = LoggerFactory.getLogger(DestinationServiceImpl.class);

	@Override
	public String getDestinationURL(String destinationName) {
		DestinationConfiguration destinationConfiguration;
		try {
			destinationConfiguration = getDestinationConfiguration(destinationName);
		} catch (Exception e) {
			throw new RuntimeException("On premise destination not reachable1 - "+e.getMessage());
		}
		return destinationConfiguration.getProperty(URL);
	}

	@Override
	public Proxy getProxy(String proxyType) {
		String proxyHost = null;
		int proxyPort;
		if (ON_PREMISE_PROXY.equals(proxyType)) {
			// Get proxy for on-premise destinations
			proxyHost = System.getenv("HC_OP_HTTP_PROXY_HOST");
			proxyPort = Integer.parseInt(System.getenv("HC_OP_HTTP_PROXY_PORT"));
		} else {
			// Get proxy for internet destinations
			proxyHost = System.getProperty("http.proxyHost");
			proxyPort = Integer.parseInt(System.getProperty("http.proxyPort"));
		}
		return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> HttpEntity<T> getHttpEntity(String destinationName, T requestObject,
	        MultiValueMap<String, String>... newHeaders) {

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		// To get the CSRF token through a GET from the remote, then to place it
		// in a POST
		// headers.add("X-Requested-With", "XMLHttpRequest");
		// headers.add("X-CSRF-Token", "Fetch");

		headers.add("Content-Type", "application/json");
		headers.add("SAP-Connectivity-ConsumerAccount", getTenantContext().getTenant().getAccount().getId());
		headers.add("DataServiceVersion", "2.0");

		String auth;
		try {
			auth = getDestinationConfiguration(destinationName).getProperty(USER) + ":"
			        + getDestinationConfiguration(destinationName).getProperty(PASSWORD);
		} catch (NamingException e) {
			throw new RuntimeException("On premise destination not reachable");
		}

		String encodedAuth = DatatypeConverter.printBase64Binary(auth.getBytes(Charset.forName("US-ASCII")));
		String authHeader = "Basic " + encodedAuth;

		headers.add("Authorization", authHeader);

		if (newHeaders != null)
			for (MultiValueMap<String, String> newHeader : newHeaders)
				headers.putAll(newHeader);

		printHeaders(headers);
		printBodyAsString(requestObject);

		HttpEntity<T> httpEntity = new HttpEntity<T>(requestObject, headers);
		return httpEntity;

	}

	private <T> void printBodyAsString(T requestObject) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			log.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestObject));
			log.info("----------------------------------------------------");
		} catch (JsonProcessingException e) {
			throw new RuntimeException(
			        "MAPPING_OBJECT_TO_JSON_EXCEPTION_FOR_OBJECT_" + requestObject.getClass().getName());
		}
	}

	public void printHeaders(MultiValueMap<String, String> headers) {

		for (String headerName : headers.keySet())
			if (headers.get(headerName).size() == 1)
				log.info(headerName + " : " + headers.getFirst(headerName));
			else
				for (String headerValue : headers.get(headerName))
					log.info(headerName + " : " + headerValue);
		log.info("----------------------------------------------------");
	}

	public TenantContext getTenantContext() {
		Context ctx;
		TenantContext tenantCtx;
		try {
			ctx = new InitialContext();
			tenantCtx = (TenantContext) ctx.lookup(JAVA_TENANT_CONTEXT_JNDI);
		} catch (NamingException e) {
			throw new RuntimeException("On premise destination not reachable");
		}

		return tenantCtx;
	}

	private DestinationConfiguration getDestinationConfiguration(String destinationName) throws NamingException {
		// Look up the connectivity configuration API
		Context ctx = new InitialContext();
		log.info("a:"+ctx);
		ConnectivityConfiguration configuration = (ConnectivityConfiguration) ctx
		        .lookup(JAVA_CONNECTIVITY_CONFIGURATION_JNDI);
		log.info("b:"+configuration);
		// Get destination configuration for "destinationName"
		DestinationConfiguration destConfiguration = configuration.getConfiguration(destinationName);
		if (destConfiguration == null) {
			throw new RuntimeException("On premise destination not reachable2");
		}
		return destConfiguration;

	}

	@Override
	public MultiValueMap<String, String> getNewHeaders(RestTemplate restTemplate, String url, String destinationName) {

		MultiValueMap<String, String> newHeaders = new LinkedMultiValueMap<String, String>();
		newHeaders.add("X-Requested-With", "XMLHttpRequest");
		newHeaders.add("Accept", "application/json");
		// newHeaders.putAll(getResponseXCSRFTokenAndSetCookieHeaders(destinationRestTemplate,
		// url, destinationName));
		return newHeaders;
	}

	@SuppressWarnings("unchecked")
	private MultiValueMap<String, String> getResponseXCSRFTokenAndSetCookieHeaders(RestTemplate restTemplate,
	        String url, String destinationName) {

		// Retrieve the X-CSRF-Token through a GET
		final MultiValueMap<String, String> newHeadersGET = new LinkedMultiValueMap<String, String>();

		newHeadersGET.add("X-Requested-With", "XMLHttpRequest");
		newHeadersGET.add("X-CSRF-Token", "Fetch");

		final ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
		        getHttpEntity(destinationName, "", newHeadersGET), String.class);

		return getResponseXCSRFTokenAndCookieHeaders(responseEntity);

	}

	private MultiValueMap<String, String> getResponseXCSRFTokenAndCookieHeaders(ResponseEntity<String> responseEntity) {

		MultiValueMap<String, String> selectedHeaders = new LinkedMultiValueMap<String, String>();
		String xCSRFTokenValue = responseEntity.getHeaders().getFirst("X-CSRF-Token");
		if (xCSRFTokenValue == null)
			throw new RuntimeException("NO_X_CSRF_Token_RETURNED_FOR_HEADER");

		selectedHeaders.add("X-CSRF-Token", xCSRFTokenValue);
		selectedHeaders.put("Cookie", responseEntity.getHeaders().get("Set-Cookie"));
		return selectedHeaders;
	}
}
