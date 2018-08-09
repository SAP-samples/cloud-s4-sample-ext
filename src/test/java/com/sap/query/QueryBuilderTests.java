package com.sap.query;

import com.sap.model.SapUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.sap.config.Constants.AND;
import static com.sap.config.Status.APPROVED;
import static com.sap.query.QueryBuilder.query;
import static org.junit.jupiter.api.Assertions.assertEquals;

class QueryBuilderTests {
    private static final String PROJECT = "project1";
    private static final String CUSTOMER = "customer1";
    private static final String WORK_PCK = "work1";
    private final String filter = "$filter=";
    private final String projectQuery = "EngagementProjectName eq 'project1'";
    private final String customerQuery = "Customer eq 'customer1'";
    private final String workPackageQuery = "WorkPackage eq 'work1'";
    private final String dateRangeQuery = "TimeSheetDate ge datetime'2018-01-01T00:00:00' and TimeSheetDate le datetime'2018-01-02T00:00:00'";
    private final String persWorkAgrExtIdQuery = "PersonWorkAgreementExternalID eq 'userId'";
    private final String recHoursGtZero = "RecordedHours gt 0";
    private final String statusEqApproved = "TimeSheetStatus eq '30'";


    @Test
    @DisplayName("FilterByProject")
    void filterByProjectQuery() {
        final String result = query().filterBy().project(PROJECT).build();
        final String expected = filter.concat(projectQuery);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("FilterByCustomer")
    void filterByCustomerQuery() {
        final String result = query().filterBy().customer(CUSTOMER).build();
        final String expected = filter.concat(customerQuery);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("FilterByWorkPackage")
    void filterByWorkPackageQuery() {
        final String result = query().filterBy().workPackage(WORK_PCK).build();
        final String expected = filter.concat(workPackageQuery);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("FilterByProjectAndCustomer")
    void filterByProjectAndCustomerQuery() {
        final String result = query().filterBy()
                .project(PROJECT)
                .and()
                .customer(CUSTOMER)
                .build();
        final String expected = filter.concat(projectQuery).concat(AND).concat(customerQuery);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("FilterByProjectAndWorkPackage")
    void filterByProjectAndWorkPackageQuery() {
        final String result = query().filterBy().project(PROJECT).and().workPackage(WORK_PCK).build();
        final String expected = filter.concat(projectQuery).concat(AND).concat(workPackageQuery);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("FilterByCustomerAndWorkPackage")
    void filterByCustomerAndWorkPackageQuery() {
        final String result = query().filterBy().customer(CUSTOMER).and().workPackage(WORK_PCK).build();
        final String expected = filter.concat(customerQuery).concat(AND).concat(workPackageQuery);
        assertEquals(expected, result);
    }


    @Test
    @DisplayName("FilterByProjectAndCustomerAndWorkPackage")
    void filterByProjectAndCustomerAndWorkPackageQuery() {
        final String result = query().filterBy().project(PROJECT).and().customer(CUSTOMER).and().workPackage(WORK_PCK).build();
        final String expected = filter.concat(projectQuery).concat(AND).concat(customerQuery).concat(AND).concat(workPackageQuery);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("FilterByDateRange")
    void filterByDateRangeQuery() {
        final LocalDate from = LocalDate.parse("2018-01-01");
        final LocalDate to = LocalDate.parse("2018-01-02");
        final String result = query().filterBy().dateRange(from, to).build();
        final String expected = filter.concat(dateRangeQuery);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("FilterByPersonWorkAgreementExternalID")
    void filterByPersAgrExtIDQuery() {
        final SapUser user = SapUser.create("userId", "userName");
        final String result = query().filterBy().personWorkAgreementExtID(user).build();
        final String expected = filter.concat(persWorkAgrExtIdQuery);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("FilterByRecorderHours")
    void filterByRecHoursQuery() {
        final String result = query().filterBy().recorderHoursGt(0).build();
        final String expected = filter.concat(recHoursGtZero);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("FilterByStatus")
    void filterByStatusQuery() {
        final String result = query().filterBy().status(APPROVED).build();
        final String expected = filter.concat(statusEqApproved);
        assertEquals(expected, result);
    }


}
