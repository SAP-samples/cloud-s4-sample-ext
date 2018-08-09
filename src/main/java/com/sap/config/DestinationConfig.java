package com.sap.config;

import com.sap.integration.DestinationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class DestinationConfig {

	//private static final String ON_PREMISE_SAP_PROXY = "OnPremise";

	@Bean  //TODO use this destinationRestTemplate initializer in case destination type is OnPremise instead of internet.
	@Profile("cloud")
	public RestTemplate destinationRestTemplate(DestinationService destinationService) {
		SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
		simpleClientHttpRequestFactory.setProxy(destinationService.getProxy("northwind"));
		return new RestTemplate(simpleClientHttpRequestFactory);
	}

	@Bean
    //@Profile("default")
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}


}
