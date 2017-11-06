package com.acme.intreportapp.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.acme.intreportapp.exception.ServiceException;
import com.acme.intreportapp.model.OneTimeCustomerRecord;
import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.odatav2.connectivity.ODataQuery;
import com.sap.cloud.sdk.odatav2.connectivity.ODataQueryBuilder;

/**
 * This class helps to make an OData call and fetches the OneTimeCustomer
 * records
 * 
 * @author SAP 
 *
 */
@Service
public class OneTimeCustomerRecordService {

	private static final Logger LOGGER = LoggerFactory.getLogger(OneTimeCustomerRecordService.class);

	// Path to the OData endpoint. On S/4 side this requires a Communication
	// Arrangement based on a Custom Communication Scenario (see guide for details)

	@Value("${s4cld.destination_name}")
	private String s4cldDestinationName;

	private static String SERVICE_PATH;
	private static String SERVICE_RESOURCE;

	public OneTimeCustomerRecordService(@Value("${s4cld.onetimecustomerrecord_servicepath}") String path,
			@Value("${s4cld.onetimecustomerrecord_resource}") String resource) {
		SERVICE_PATH = path;
		SERVICE_RESOURCE = resource;
	}

	/**
	 * This method returns one time customer records by making an OData call
	 * 
	 * @return customerList
	 * @throws ServiceException
	 */
	public List<OneTimeCustomerRecord> findAll() throws ServiceException {

		try {
			ODataQuery query = ODataQueryBuilder.withEntity(SERVICE_PATH, SERVICE_RESOURCE).build();

			List<OneTimeCustomerRecord> customerList = query.execute(s4cldDestinationName)
					.asList(OneTimeCustomerRecord.class);

			return customerList;

		} catch (ODataException e) {
			LOGGER.error("Error during OData query", e);
			throw new IllegalStateException("Unable to read plus entities, please try again later.", e);
		} catch (Exception e) {
			LOGGER.error("Error during OData query.", e);
			throw new ServiceException("Unable to read the entities, please try again later.", e);
		}

	}

}