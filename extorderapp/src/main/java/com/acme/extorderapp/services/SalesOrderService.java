package com.acme.extorderapp.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.acme.extorderapp.model.SalesOrder;

@Service
public class SalesOrderService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SalesOrderService.class);

	// Path to the OData endpoint. On S/4 side this requires a Communication Arrangement based on Communication Scenario SAP_COM_0109 (Process Sales Orders)
	private static final String SERVICE_PATH = "/sap/opu/odata/sap/API_SALES_ORDER_SRV";
	private static final String SERVICE_RESOURCE = "A_SalesOrder";

	@Autowired
	private ODataClient oDataClient;

	public SalesOrderService () {
	}

	public SalesOrder create(SalesOrder salesOrder) throws RuntimeException {

		// Retrieve a fresh security token
		SecurityToken secToken = oDataClient.fetchCsrfToken(SERVICE_PATH);

		// POST the new sales order to S/4HANA
		ResponseEntity<SalesOrder> responseEntity = oDataClient.createEntity(
				SERVICE_PATH + "/" + SERVICE_RESOURCE,
				salesOrder,
				SalesOrder.class,
				secToken);

		HttpStatus statusCode = responseEntity.getStatusCode();
		if (statusCode != HttpStatus.CREATED) {
			LOGGER.warn("Sales Order creation failed, HTTP status code: {}", statusCode);
			throw new IllegalStateException("Unable to create a sales order. Please try again.");

		return responseEntity.getBody();
	}


}