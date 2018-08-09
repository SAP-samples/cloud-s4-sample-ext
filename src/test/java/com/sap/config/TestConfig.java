package com.sap.config;

import com.sap.repository.FilterRepository;
import com.sap.repository.LocalWorkforceRepository;
import com.sap.repository.WorkforceRepository;
import com.sap.security.AuthFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.mock;

@Profile("test")
@Configuration
public class TestConfig {

    @Bean
    @Primary
    public FilterRepository filterRepository() {
        return mock(FilterRepository.class);
    }

    @Bean
    @Primary
    public WorkforceRepository workforceRepository() {
        return new LocalWorkforceRepository();
    }

    @Bean
    @Primary
    public AuthFacade authFacade() {
        return mock(AuthFacade.class);
    }
}
