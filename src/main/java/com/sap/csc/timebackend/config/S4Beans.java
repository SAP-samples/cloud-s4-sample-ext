package com.sap.csc.timebackend.config;


import com.sap.cloud.sdk.s4hana.connectivity.ErpConfigContext;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.DefaultWorkforceTimesheetService;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.WorkforceTimesheetService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S4Beans {

    @Bean
    public WorkforceTimesheetService manageWorkforceTimesheetService() {
        return new DefaultWorkforceTimesheetService();
    }

    @Bean
    public ErpConfigContext erpConfigContext() {
        return new ErpConfigContext("S4HANA_CLOUD");
    }




}