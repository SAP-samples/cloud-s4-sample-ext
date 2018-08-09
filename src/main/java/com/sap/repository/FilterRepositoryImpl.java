package com.sap.repository;

import com.sap.model.Customer;
import com.sap.model.Project;
import com.sap.model.WorkPackage;
import com.sap.utils.DestinationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.HashSet;
import java.util.Set;

import static com.sap.config.Constants.*;

@Component
public class FilterRepositoryImpl implements FilterRepository {

    private static final String SELECT = "$select=";
    private static final String COMMA = ",";
    private static final Logger log = LoggerFactory.getLogger(CloudWorkforceRepository.class);
    private final DestinationUtils destinationUtils;

    @Value("${destination.name}")
    private String destinationName;

    @Value("${destination.basePath}")
    private String basePath;

    @Value("${cds.name}")
    private String cdsName;

    @Value("${cds.entity}")
    private String entityName;


    @Autowired
    public FilterRepositoryImpl(DestinationUtils destinationUtils) {
        this.destinationUtils = destinationUtils;
    }

    @Override
    public Set<Project> getProjectSet(String personWorkAgreement) {
        log.info("Getting project set...");

        final String params = "?" + SELECT + ENGAGEMENT_PROJECT + COMMA + ENGAGEMENT_PROJECT_NAME + AMPERSAND + JSON_FORMAT
                + AMPERSAND
                + FILTER
                + ENGAGEMENT_PROJECT
                + NE + "''"
                + AND
                + PERSON_WORK_AGREEMENT_EXTERNAL_ID
                + EQ
                + "'"
                + personWorkAgreement
                + "'"
                + AND
                + filterApprovedStatus()
                + AND
                + recHoursGtZero();

        final String serviceURL = composeUrl(cdsName, entityName, params);

        log.info("URL --> " + serviceURL);
        final MultiValueMap<String, String> newHeaders = destinationUtils.getNewHeaders(destinationName, serviceURL);
        final ResponseEntity<String> result = destinationUtils.getDataFromServiceAsString(destinationName, serviceURL, HttpMethod.GET,
                newHeaders);
        log.info("Retrieved project set...");
        return new HashSet<>(destinationUtils.convertODataJsonToListOfObjects(result.getBody(), Project.class));


    }

    @Override
    public Set<Customer> getCustomerSet(String personWorkAgreement) {

        final String params = "?" + SELECT + CUSTOMER + COMMA + CUSTOMER_FULL_NAME + AMPERSAND + JSON_FORMAT
                + AMPERSAND
                + FILTER
                + CUSTOMER_FULL_NAME
                + NE + "''"
                + AND
                + PERSON_WORK_AGREEMENT_EXTERNAL_ID
                + EQ
                + "'"
                + personWorkAgreement
                + "'"
                + AND
                + filterApprovedStatus()
                + AND
                + recHoursGtZero();

        final String serviceURL = composeUrl(cdsName, entityName, params);
        log.info("URL --> " + serviceURL);
        final MultiValueMap<String, String> newHeaders = destinationUtils.getNewHeaders(destinationName, serviceURL);
        final ResponseEntity<String> result = destinationUtils.getDataFromServiceAsString(destinationName, serviceURL, HttpMethod.GET,
                newHeaders);
        return new HashSet<>(destinationUtils.convertODataJsonToListOfObjects(result.getBody(), Customer.class));

    }

    @Override
    public Set<WorkPackage> getWorkPackageSet(String personWorkAgreement) {
        final String params = "?" + SELECT + WORK_PACKAGE + COMMA + WORK_PACKAGE_NAME + AMPERSAND + JSON_FORMAT
                + AMPERSAND
                + FILTER
                + WORK_PACKAGE_NAME
                + NE + "''"
                + AND
                + PERSON_WORK_AGREEMENT_EXTERNAL_ID
                + EQ
                + "'"
                + personWorkAgreement
                + "'"
                + AND
                + filterApprovedStatus()
                + AND
                + recHoursGtZero();

        final String serviceURL = composeUrl(cdsName, entityName, params);
        final MultiValueMap<String, String> newHeaders = destinationUtils.getNewHeaders(destinationName, serviceURL);
        final ResponseEntity<String> result = destinationUtils.getDataFromServiceAsString(destinationName, serviceURL, HttpMethod.GET,
                newHeaders);
        return new HashSet<>(destinationUtils.convertODataJsonToListOfObjects(result.getBody(), WorkPackage.class));
    }

    private String composeUrl(String service, String entity, String params) {
        final String url = service + "/" + entity + params;

        return destinationUtils.getDestinationServiceUrlForPath(destinationName, basePath + url);
    }

    private String filterApprovedStatus() {
        return TIME_SHEET_STATUS + EQ + "'" + APPROVED + "'";
    }

    private String recHoursGtZero() {
        return RECORDED_HOURS + GT + "0";
    }
}
