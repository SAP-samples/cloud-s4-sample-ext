package com.acme.extorderapp.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.acme.extorderapp.model.OneTimeCustomerRecord;

@Service
public class OneTimeCustomerRecordService {

	private static final Logger LOGGER = LoggerFactory.getLogger(OneTimeCustomerRecordService.class);
	
	// Path to the OData endpoint. On S/4 side this requires a Communication Arrangement based on a Custom Communication Scenario (see guide for details)		
	private static String SERVICE_PATH;	
	private static String SERVICE_RESOURCE;	

	@Autowired
	private ODataClient oDataClient;
	
	public OneTimeCustomerRecordService(
			@Value("${s4cld.onetimecustomerrecord_servicepath}") String path, 
			@Value("${s4cld.onetimecustomerrecord_resource}") String resource) {		
		SERVICE_PATH = path;
		SERVICE_RESOURCE = resource;
	}

	public OneTimeCustomerRecord create(OneTimeCustomerRecord otcRecord) throws RuntimeException {
				
		// Before sending a POST request, we need to fetch a security token 
		SecurityToken secToken = oDataClient.fetchCsrfToken(SERVICE_PATH);
			
		// POST the one-time-customer record data to the S/4HANA custom business object
		ResponseEntity<OneTimeCustomerRecord> responseEntity = oDataClient.createEntity(
				SERVICE_PATH + '/' + SERVICE_RESOURCE, 
				otcRecord, 
				OneTimeCustomerRecord.class,
				secToken);		
		
		HttpStatus statusCode = responseEntity.getStatusCode();
		if (statusCode != HttpStatus.CREATED) { 
			LOGGER.warn("OneTimeCustomerRecord creation failed, HTTP status code: {}", statusCode);
			throw new IllegalStateException("Unsuccessful outgoing request");
		}			

		return responseEntity.getBody();		
	}


}