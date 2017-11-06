package com.acme.intreportapp.controller;

import static com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.ProcessSalesOrdersNamespace.SalesOrder.CREATION_DATE;
import static com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.ProcessSalesOrdersNamespace.SalesOrder.DISTRIBUTION_CHANNEL;
import static com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.ProcessSalesOrdersNamespace.SalesOrder.ORGANIZATION_DIVISION;
import static com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.ProcessSalesOrdersNamespace.SalesOrder.PURCHASE_ORDER_BY_CUSTOMER;
import static com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.ProcessSalesOrdersNamespace.SalesOrder.SALES_ORDER;
import static com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.ProcessSalesOrdersNamespace.SalesOrder.SALES_ORDER_TYPE;
import static com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.ProcessSalesOrdersNamespace.SalesOrder.SOLD_TO_PARTY;
import static com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.ProcessSalesOrdersNamespace.SalesOrder.TOTAL_NET_AMOUNT;
import static com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.ProcessSalesOrdersNamespace.SalesOrder.TRANSACTION_CURRENCY;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acme.intreportapp.exception.ServiceException;
import com.acme.intreportapp.model.OneTimeCustomerRecord;
import com.acme.intreportapp.services.OneTimeCustomerRecordService;
import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.s4hana.connectivity.ErpConfigContext;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.ProcessSalesOrdersNamespace.SalesOrder;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.ProcessSalesOrdersNamespace.SalesOrderFluentHelper;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.ProcessSalesOrdersService;

@RestController
/**
 * This controller serves as the end point service class and provide request
 * mapping methods
 * 
 * @author SAP 
 * 
 * @version 1.0 @
 */
public class ApiController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiController.class);

	private static final String salesOrderMethod = "salesOrders";

	private static final String oneTimeRecordsMethod = "oneTimeRecords";

	@Value("${s4cld.destination_name}")
	private String s4cldDestinationName;

	@Autowired
	private OneTimeCustomerRecordService oneTimeRecordService;

	/**
	 * This method acts as end point url and fetches the list of sales order
	 * 
	 * @return orderList
	 * @throws ODataException
	 */
	@RequestMapping("/api/salesOrders")
	public List<SalesOrder> salesOrders() throws ODataException {

		LOGGER.info("Method entry {}", salesOrderMethod);

		// The soldToParty is hardcoded since we only want to show orders for the
		// on-time customer and it must not be changed in the UI layer

		SalesOrderFluentHelper query = ProcessSalesOrdersService.getAllSalesOrder().filter(SOLD_TO_PARTY.eq("10401010"))
				.select(SALES_ORDER, SALES_ORDER_TYPE, DISTRIBUTION_CHANNEL, ORGANIZATION_DIVISION, SOLD_TO_PARTY,
						PURCHASE_ORDER_BY_CUSTOMER, TOTAL_NET_AMOUNT, TRANSACTION_CURRENCY, CREATION_DATE);

		List<SalesOrder> orderList = query.execute(new ErpConfigContext(s4cldDestinationName));

		LOGGER.info("Method exit {}", salesOrderMethod);

		return orderList;
	}

	/**
	 * This Method fetches one time customer records by calling the
	 * OneTimeRecordService
	 * 
	 * @return customerRecordsList
	 */
	@RequestMapping("/api/oneTimeRecords")
	public List<OneTimeCustomerRecord> oneTimeRecords() {

		LOGGER.info("Method entry {}", oneTimeRecordsMethod);

		List<OneTimeCustomerRecord> customerRecordsList = null;

		try {

			// Invoke onetimerecordservice method
			customerRecordsList = oneTimeRecordService.findAll();

		} catch (ServiceException e) {
			LOGGER.error("Exception occurred while fetching the data.", e);
		}

		LOGGER.info("Method exit {}", oneTimeRecordsMethod);

		return customerRecordsList;

	}

}