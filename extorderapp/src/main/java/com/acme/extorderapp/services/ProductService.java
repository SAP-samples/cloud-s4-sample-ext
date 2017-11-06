package com.acme.extorderapp.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.acme.extorderapp.model.Product;
import com.acme.extorderapp.model.Products;

@Service
public class ProductService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

	// Path to the OData endpoint. On S/4 side this requires a Communication Arrangement based on Communication Scenario SAP_COM_0009 (Product Integration)
	private static final String SERVICE_PATH = "/sap/opu/odata/sap/API_PRODUCT_SRV";
	private static final String SERVICE_RESOURCE = "A_Product";

	@Autowired
	private ODataClient oDataClient;

	public ProductService() {

	}

	public Product findById(String productId) throws RuntimeException {
		String url = SERVICE_PATH + "/" + SERVICE_RESOURCE + "('" + productId + "')";

		// On the order page we also want to show the product description,
		// in OData we can just use the expand modifier to also retrieve the associated descriptions
		url += "?$expand={expand}";

		ResponseEntity<Product> responseEntity = oDataClient.readEntity(url, Product.class, /*$expand=*/"to_Description");

		HttpStatus statusCode = responseEntity.getStatusCode();
		if (!statusCode.is2xxSuccessful()) {
			LOGGER.warn("received HTTP status code: {}", statusCode);
			throw new IllegalStateException("Unable to read product data, please try again later.");
		}

		return responseEntity.getBody();
	}

	public Products findByProductGroup(String productGroup) throws RuntimeException {
		String url = SERVICE_PATH + "/" + SERVICE_RESOURCE;

		// We only want to retrieve specific products here hence we add an OData filter modifier
		url += "?$filter={filter}";

		ResponseEntity<Products> responseEntity = oDataClient.readEntity(url, Products.class,
				/*$filter=*/"ProductGroup eq '" + productGroup + "'");

		HttpStatus statusCode = responseEntity.getStatusCode();
		if (!statusCode.is2xxSuccessful()) {
			LOGGER.warn("received HTTP status code: {}", statusCode);
			throw new IllegalStateException("Unable to read products, please try again later.");
		}

		return responseEntity.getBody();
	}

}