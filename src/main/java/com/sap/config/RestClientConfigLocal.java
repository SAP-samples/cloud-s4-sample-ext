package com.sap.config;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

@Profile("local")
@Configuration
public class RestClientConfigLocal {

    @Bean
    public RestTemplate restTemplate() {

        RestTemplate restTemplate = new RestTemplate();
        HttpComponentsClientHttpRequestFactory requestFactory
                = new HttpComponentsClientHttpRequestFactory(
                HttpClientBuilder.create()
                        .setProxy(new HttpHost("proxy.wdf.sap.corp", 8080, "http"))
                        .build());

        restTemplate.setRequestFactory(requestFactory);
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        return restTemplate;
    }
}
