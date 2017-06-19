package com.acme.extorderapp.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

/**
 * Basic OData Client to communicate with an S/4HANA Cloud backend system. 
 * Just for illustration purposes and is not intended for production.
 */
@Service
public class ODataClient {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ODataClient.class);
	
	
	private final RestTemplate restTemplate;
	
	public ODataClient (@Value("${s4cld.api_host}") String host,
			@Value("${s4cld.api_user}") String user,
			@Value("${s4cld.api_pass}") String pass) {		
		// Initialize the rest template client instance
		restTemplate = new RestTemplateBuilder()
				.rootUri(host)
				.messageConverters(new MappingJackson2HttpMessageConverter(ODataClient.getObjectMapper()))
				// Provide the technical user/password (this needs to be maintained in the S/4HANA Cloud System as Communication User)
				.basicAuthorization(user, pass)
				.build();
	}
	
	/**
	 * Create a OData entity
	 * @param url OData service resource, e.g. "/sap/opu/odata/sap/API_PRODUCT_SRV/A_Product"
	 * @param entity Instance of a plain Java Object which will be converted to JSON
	 * @param clazz Class of the Java object
	 * @param secToken SecurityToken gathered by ODataClient.fetchCsrfToken();
	 * @return Response of the backend system, when successfully a instance of type clazz.
	 */
	public <T> ResponseEntity<T> createEntity(String url, T entity, Class<T> clazz, SecurityToken secToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("x-csrf-token", secToken.getCsrfToken());
		headers.set("cookie", secToken.getSessionCookie());
		HttpEntity<T> entityAndHeader = new HttpEntity<T>(entity, headers);		
		
		try {
			ResponseEntity<T> responseEntity = restTemplate.postForEntity(url, entityAndHeader, clazz);
			return responseEntity;
		} catch (HttpStatusCodeException e) {
			// For simplicity this sample does not implement error handling, hence we just log request/response in case of unsuccessful responses from the backend. 
			LOGGER.error("Error creating entity={} at url={}", clazz.getCanonicalName(), url);
			LOGGER.error("Error: statusCode={}", e.getStatusCode());
			LOGGER.error("Response body={}", e.getResponseBodyAsString());
			throw e;
		}
	}

	/**
	 * Read an entity or list of entities from the S/4HANA backend
	 * @param url OData service resource, e.g. "/sap/opu/odata/sap/API_PRODUCT_SRV/A_Product" to read a list of products 
	 *            or e.g. "/sap/opu/odata/sap/API_PRODUCT_SRV/A_Product('TG12')" to only read a single entity
	 * @param clazz Java class of the object to be read
	 * @param parameters Additional query parameters
	 * @return Instance of type clazz
	 */
	public <T> ResponseEntity<T> readEntity (String url, Class<T> clazz, String parameters) {
		ResponseEntity<T> responseEntity = restTemplate.getForEntity(url, clazz, parameters);		    
		return responseEntity;
	}
	
	/**
	 * Retrieve a security token (CSRF token and Cookie) from the S/4HANA Cloud backend
	 * @param url OData service endpoint, e.g. "/sap/opu/odata/sap/API_PRODUCT_SRV"  
	 * @return SecurityToken required for manipulating service calls.  
	 * @throws RuntimeException in case of empty csrf tokens
	 */
	public SecurityToken fetchCsrfToken (String url) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("x-csrf-token", "fetch");	
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.HEAD, entity, String.class);
		
		String csrfToken = response.getHeaders().getFirst("x-csrf-token");
		String sessionCookie = "";
		List<String> cookies = response.getHeaders().get("set-cookie");
		for (String cookie : cookies) {
			if (cookie.startsWith("SAP_SESSION")) {
				sessionCookie = cookie;
			}
		}		
		LOGGER.debug("Received csrf token ({}) and session cookie ({})", csrfToken, sessionCookie);		
		
		if (csrfToken == null || sessionCookie == null || "".equals(csrfToken) || "".equals(sessionCookie)) {
			LOGGER.error("Unable to retrieve a valid csrf token ({}) and session cookie ({})", csrfToken, sessionCookie);
			throw new RuntimeException("Unable to obtain security token from the backend system.");
		}					
		
		return new SecurityToken(csrfToken, sessionCookie);
	}	
	
	/**
	 * Instantiate an ObjectMapper to convert OData JSON objects to/from Java objects
	 */
	public static ObjectMapper getObjectMapper() {		
		ObjectMapper mapper = new ObjectMapper();		
		
		// The OData service uses a property named "d" as wrapper, this allows to unwrap it before mapping it to the POJO
		mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);					
		
		// OData uses UpperCamelCase, Java camelCase - this strategy will convert the names accordingly.
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);			

		// Don't send null values (e.g. when creating a SalesOrder the number of the sales order must not be sent as null value)
		mapper.setSerializationInclusion(Include.NON_NULL);					
		
		return mapper;
	}

}