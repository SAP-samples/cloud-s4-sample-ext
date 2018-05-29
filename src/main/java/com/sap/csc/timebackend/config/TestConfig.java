package com.sap.csc.timebackend.config;

import com.sap.csc.timebackend.security.AuthenticationFacade;
import com.sap.csc.timebackend.security.SAPUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class TestConfig {

    @Bean
    public AuthenticationFacade authenticationFacade() {
        return () -> SAPUser.create("testUser");
    }
}
