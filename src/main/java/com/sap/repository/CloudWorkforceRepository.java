package com.sap.repository;

import com.sap.model.SapUser;
import com.sap.model.Workforce;
import com.sap.utils.DestinationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.util.List;

import static com.sap.config.Constants.*;

@Component
public class CloudWorkforceRepository implements WorkforceRepository {

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
    public CloudWorkforceRepository(DestinationUtils destinationUtils) {
        this.destinationUtils = destinationUtils;
    }

    @Override
    public List<Workforce> getAll() {
        final String serviceURL = composeUrl(cdsName, entityName,
                "?" + FILTER + "RecordedHours gt 0&$top=10&" + JSON_FORMAT);
        log.info("URL --> " + serviceURL);
        return getWorkforces(serviceURL);
    }

    @Override
    public List<Workforce> filterByPersonWorkAgreementExternalId(String id) {
        final String serviceURL = composeUrl(cdsName, entityName,
                "?" + FILTER + "PersonWorkAgreementExternalID eq '"
                        + id
                        + "'"
                        + AMPERSAND
                        + JSON_FORMAT);
        return getWorkforces(serviceURL);
    }

    //TODO:: Refactor all ODATA query builders and place them into a separate class
    //TODO:: Usable class should be something like QueryBuilder.filterBy().project("p1").customer("c1").dateRange("02-12-2018","03-12-2018").user("admin").build();
    //TODO:: Maybe use Project.class, Customer.class and SapUser.class for method parameters

    @Override
    public List<Workforce> filterByProjectAndCustomerInRange(String project, String customer, LocalDate from, LocalDate to, SapUser user) {
        final String serviceURL = composeUrl(cdsName, entityName,
                "?" + FILTER + buildDateTimeExpression(from, to)
                        + AND
                        + buildCustomerExpression(customer)
                        + AND
                        + buildProjectExpression(project)
                        + AND
                        + buildPersAgrExpression(user)
                        + AND
                        + filterApprovedStatus()
                        + AND
                        + recHoursGtZero()
                        + AMPERSAND
                        + JSON_FORMAT);
        log.info("URL --> " + serviceURL);
        return getWorkforces(serviceURL);
    }

    @Override
    public List<Workforce> filterByCustomerInRange(String customer, LocalDate from, LocalDate to, SapUser user) {
        final String serviceURL = composeUrl(cdsName, entityName,
                "?"
                        + FILTER
                        + buildDateTimeExpression(from, to)
                        + AND
                        + buildCustomerExpression(customer)
                        + AND
                        + buildPersAgrExpression(user)
                        + AND
                        + filterApprovedStatus()
                        + AND
                        + recHoursGtZero()
                        + AMPERSAND
                        + JSON_FORMAT);
        log.info("URL --> " + serviceURL);
        return getWorkforces(serviceURL);
    }

    @Override
    public List<Workforce> filterByProjectInRange(String project, LocalDate from, LocalDate to, SapUser user) {
        final String serviceURL = composeUrl(cdsName, entityName,
                "?" + FILTER
                        + buildDateTimeExpression(from, to)
                        + AND
                        + buildProjectExpression(project)
                        + AND
                        + buildPersAgrExpression(user)
                        + AND
                        + filterApprovedStatus()
                        + AND
                        + recHoursGtZero()
                        + AMPERSAND
                        + JSON_FORMAT);
        log.info("URL --> " + serviceURL);
        return getWorkforces(serviceURL);
    }

    @Override
    public List<Workforce> filterInRange(LocalDate from, LocalDate to, SapUser user) {
        final String serviceURL = composeUrl(cdsName, entityName,
                "?" + FILTER
                        + buildDateTimeExpression(from, to)
                        + AND
                        + buildPersAgrExpression(user)
                        + AND
                        + filterApprovedStatus()
                        + AND
                        + recHoursGtZero()
                        + AMPERSAND
                        + JSON_FORMAT);
        log.info("URL --> " + serviceURL);
        return getWorkforces(serviceURL);
    }


    @Override
    public Workforce findById(String id) {
        final String serviceURL = composeUrl(cdsName, entityName,
                "('"
                        + id
                        + "')?"
                        + JSON_FORMAT);
        log.info("URL --> " + serviceURL);
        final MultiValueMap<String, String> newHeaders = destinationUtils.getNewHeaders(destinationName, serviceURL);
        final ResponseEntity<String> result = destinationUtils.getDataFromServiceAsString(destinationName, serviceURL, HttpMethod.GET,
                newHeaders);

        return destinationUtils
                .convertODataJsonToObject(result.getBody(), Workforce.class);
    }

    @Override
    public List<Workforce> filterByProjectAndCustomerAndWPInRange(String project, String customer, String workPackage, LocalDate from, LocalDate to, SapUser user) {
        final String serviceURL = composeUrl(cdsName, entityName,
                "?" + FILTER + buildDateTimeExpression(from, to)
                        + AND
                        + buildCustomerExpression(customer)
                        + AND
                        + buildProjectExpression(project)
                        + AND
                        + buildWPExpression(workPackage)
                        + AND
                        + buildPersAgrExpression(user)
                        + AND
                        + filterApprovedStatus()
                        + AND
                        + recHoursGtZero()
                        + AMPERSAND
                        + JSON_FORMAT);
        log.info("URL --> " + serviceURL);
        return getWorkforces(serviceURL);
    }


    @Override
    public List<Workforce> filterCustomerAndWPInRange(String customer, String workPackage, LocalDate from, LocalDate to, SapUser user) {
        final String serviceURL = composeUrl(cdsName, entityName,
                "?" + FILTER + buildDateTimeExpression(from, to)
                        + AND
                        + buildCustomerExpression(customer)
                        + AND
                        + buildWPExpression(workPackage)
                        + AND
                        + buildPersAgrExpression(user)
                        + AND
                        + filterApprovedStatus()
                        + AND
                        + recHoursGtZero()
                        + AMPERSAND
                        + JSON_FORMAT);
        log.info("URL --> " + serviceURL);
        return getWorkforces(serviceURL);
    }

    @Override
    public List<Workforce> filterByProjectAndWPInRange(String project, String workPackage, LocalDate from, LocalDate to, SapUser user) {
        final String serviceURL = composeUrl(cdsName, entityName,
                "?" + FILTER + buildDateTimeExpression(from, to)
                        + AND
                        + buildProjectExpression(project)
                        + AND
                        + buildWPExpression(workPackage)
                        + AND
                        + buildPersAgrExpression(user)
                        + AND
                        + filterApprovedStatus()
                        + AND
                        + recHoursGtZero()
                        + AMPERSAND
                        + JSON_FORMAT);
        log.info("URL --> " + serviceURL);
        return getWorkforces(serviceURL);
    }

    @Override
    public List<Workforce> filterByWPInRange(String workPackage, LocalDate from, LocalDate to, SapUser user) {
        final String serviceURL = composeUrl(cdsName, entityName,
                "?" + FILTER + buildDateTimeExpression(from, to)
                        + AND
                        + buildWPExpression(workPackage)
                        + AND
                        + buildPersAgrExpression(user)
                        + AND
                        + filterApprovedStatus()
                        + AND
                        + recHoursGtZero()
                        + AMPERSAND
                        + JSON_FORMAT);
        log.info("URL --> " + serviceURL);
        return getWorkforces(serviceURL);
    }

    private String composeUrl(String service, String entity, String params) {
        final String url = service + "/" + entity + params;

        return destinationUtils.getDestinationServiceUrlForPath(destinationName, basePath + url);
    }

    private List<Workforce> getWorkforces(String serviceURL) {
        final MultiValueMap<String, String> newHeaders = destinationUtils.getNewHeaders(destinationName, serviceURL);
        final ResponseEntity<String> result = destinationUtils.getDataFromServiceAsString(destinationName, serviceURL, HttpMethod.GET,
                newHeaders);
        return destinationUtils.convertODataJsonToListOfObjects(result.getBody(), Workforce.class);
    }

    private String buildWPExpression(String workPackage) {
        return WORK_PACKAGE
                + EQ
                + "'"
                + workPackage
                + "'";
    }

    private String buildCustomerExpression(String customerId) {
        return CUSTOMER
                + EQ
                + "'"
                + customerId
                + "'";
    }

    private String buildProjectExpression(String engagementProjectName) {
        return ENGAGEMENT_PROJECT_NAME
                + EQ
                + "'"
                + engagementProjectName
                + "'";
    }


    private String buildDateTimeExpression(LocalDate from, LocalDate to) {
        return timeSheetDate
                + GE
                + DATETIME
                + "'"
                + from.toString()
                + stringTime
                + "'"
                + AND
                + timeSheetDate
                + LE
                + DATETIME
                + "'"
                + to.toString()
                + stringTime
                + "'";

    }

    private String buildPersAgrExpression(SapUser user) {
        return PERSON_WORK_AGREEMENT_EXTERNAL_ID
                + EQ
                + "'"
                + user.getUserId()
                + "'";
    }

    private String filterApprovedStatus() {
        return TIME_SHEET_STATUS + EQ + "'" + APPROVED + "'";
    }

    private String recHoursGtZero() {
        return "RecordedHours" + " gt " + "0";
    }

}
