package com.sap.query;

import com.sap.config.Status;
import com.sap.model.SapUser;

import java.time.LocalDate;

import static com.sap.config.Constants.*;

public class QueryBuilder {
    private String query = "";

    public static QueryBuilder query() {
        return new QueryBuilder();
    }

    public QueryBuilder filterBy() {
        query = query.concat("$filter=");
        return this;
    }

    //TODO:: Calling this method should return an object that has only methods like project(), customer() and so on;
    public QueryBuilder and() {
        query = query.concat(AND);
        return this;
    }

    //TODO:: calling this method should return an object that has only predicate methods such as and(), or().
    public QueryBuilder project(String project) {
        query = query.concat(buildProjectExpression(project));
        return this;
    }

    public QueryBuilder workPackage(String workPackage) {
        query = query.concat(buildWPExpression(workPackage));
        return this;
    }

    public QueryBuilder dateRange(LocalDate from, LocalDate to) {
        query = query.concat(buildDateTimeExpression(from, to));
        return this;
    }

    public QueryBuilder personWorkAgreementExtID(SapUser user) {
        query = query.concat(buildPersAgrExpression(user));
        return this;
    }

    public QueryBuilder recorderHoursGt(Integer value) {
        query = query.concat(recHoursGt(value));
        return this;
    }

    public QueryBuilder status(Status status) {
        query = query.concat(buildStatusExpression(status));
        return this;
    }

    public String build() {
        final String temp = query;
        query = "";
        return temp;
    }

    public QueryBuilder customer(String customer) {
        query = query.concat(buildCustomerExpression(customer));
        return this;
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

    private String buildStatusExpression(Status status) {
        return TIME_SHEET_STATUS + EQ + "'" + status.getCode() + "'";
    }

    private String recHoursGt(int value) {
        return "RecordedHours" + " gt " + value;
    }


}
