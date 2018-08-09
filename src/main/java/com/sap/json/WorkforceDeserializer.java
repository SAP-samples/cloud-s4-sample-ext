package com.sap.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.sap.model.Workforce;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static com.sap.config.Constants.*;

public class WorkforceDeserializer extends JsonDeserializer<Workforce> {

    @Override
    public Workforce deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final Workforce workforce = new Workforce();
        ObjectCodec codec = p.getCodec();
        JsonNode node = codec.readTree(p);

        final String id = Optional.ofNullable(node.get("ID")).map(JsonNode::asText).orElse("");
        workforce.setId(id);

        final String personWorkAgreement = Optional.ofNullable(node.get(PERSON_WORK_AGREEMENT)).map(JsonNode::asText).orElse("");
        workforce.setPersonWorkAgreement(personWorkAgreement);

        final String timeSheetDate = Optional.ofNullable(node.get("TimeSheetDate")).map(JsonNode::asText).orElse(new Date().toInstant().toString())
                .replaceAll("[^\\d.]", "");
        workforce.setCalendarDate(Instant
                .ofEpochMilli(Long
                        .parseLong(timeSheetDate))
                .atZone(ZoneId.systemDefault())
                .toLocalDate());

        final String wbs = Optional.ofNullable(node.get(WBS_ELEMENT_INTERNAL_ID)).map(JsonNode::asText).orElse("");
        workforce.setWbsElementInternalId(wbs);

        final String customerFullName = Optional.ofNullable(node.get(CUSTOMER_FULL_NAME)).map(JsonNode::asText).orElse("");
        workforce.setCustomerFullName(customerFullName);

        final String customer = Optional.ofNullable(node.get(CUSTOMER)).map(JsonNode::asText).orElse("");
        workforce.setCustomer(customer);

        final String projectManager = Optional.ofNullable(node.get("ProjectManager")).map(JsonNode::asText).orElse("");
        workforce.setProjectManager(projectManager);

        final String wbsDescription = Optional.ofNullable(node.get(WBS_DESCRIPTION)).map(JsonNode::asText).orElse("");
        workforce.setWbsDescription(wbsDescription);

        final String hoursUnitOfMeasure = Optional.ofNullable(node.get("HoursUnitOfMeasure")).map(JsonNode::asText).orElse("H");
        workforce.setHoursUnitOfMeasure(hoursUnitOfMeasure);

        final double recordedHours = Optional.ofNullable(node.get("RecordedHours")).map(JsonNode::asDouble).orElse((double) 0);
        workforce.setRecordedHours(new BigDecimal(recordedHours));

        final String businessPartnerName = Optional.ofNullable(node.get("BusinessPartnerName")).map(JsonNode::asText).orElse("");
        workforce.setBusinessPartnerName(businessPartnerName);

        final String activityType = Optional.ofNullable(node.get("ActivityType")).map(JsonNode::asText).orElse("");
        workforce.setActivityType(activityType);

        final String engagementProjectCategory = Optional.ofNullable(node.get("EngagementProjectCategory")).map(JsonNode::asText).orElse("");
        workforce.setEngagementProjectCategory(engagementProjectCategory);

        final String accountingIndicatorCode = Optional.ofNullable(node.get("AccountingIndicatorCode")).map(JsonNode::asText).orElse("");
        workforce.setAccountingIndicatorCode(accountingIndicatorCode);

        final double billableHours = Optional.ofNullable(node.get("BillableHours")).map(JsonNode::asDouble).orElse((double) 0);
        workforce.setBillableHours(new BigDecimal(billableHours));

        final double nonBillableHours = Optional.ofNullable(node.get("NonBillableHours")).map(JsonNode::asDouble).orElse((double) 0);
        workforce.setNonBillableHours(new BigDecimal(nonBillableHours));

        final String unitHour = Optional.ofNullable(node.get("UnitHour")).map(JsonNode::asText).orElse("");
        workforce.setUnitHour(unitHour);

        final String dateYear = Optional.ofNullable(node.get("DateYear")).map(JsonNode::asText).orElse("");
        workforce.setDateYear(dateYear);

        final String dateMonth = Optional.ofNullable(node.get("DateMonth")).map(JsonNode::asText).orElse("");
        workforce.setDateMonth(dateMonth);

        final String personWorkAgreementExternalID = Optional.ofNullable(node.get(PERSON_WORK_AGREEMENT_EXTERNAL_ID)).map(JsonNode::asText).orElse("");
        workforce.setPersonWorkAgreementExternalID(personWorkAgreementExternalID);

        final String engagementProjectName = Optional.ofNullable(node.get(ENGAGEMENT_PROJECT_NAME)).map(JsonNode::asText).orElse("");
        workforce.setEngagementProjectName(engagementProjectName);

        final String businessPartnerFullName = Optional.ofNullable(node.get("BusinessPartnerFullName")).map(JsonNode::asText).orElse("");
        workforce.setBusinessPartnerFullName(businessPartnerFullName);


        final String timeSheetRecord = Optional.ofNullable(node.get("TimeSheetRecord")).map(JsonNode::asText).orElse("");
        workforce.setTimeSheetRecord(timeSheetRecord);

        final String workPackage = Optional.ofNullable(node.get(WORK_PACKAGE)).map(JsonNode::asText).orElse("");
        workforce.setWorkPackage(workPackage);

        final String workPackageName = Optional.ofNullable(node.get(WORK_PACKAGE_NAME)).map(JsonNode::asText).orElse("");
        workforce.setWorkPackageName(workPackageName);

        final String workItem = Optional.ofNullable(node.get("WorkItem")).map(JsonNode::asText).orElse("");
        workforce.setWorkItem(workItem);

        final String workItemName = Optional.ofNullable(node.get("WorkItemName")).map(JsonNode::asText).orElse("");
        workforce.setWorkItemName(workItemName);

        final String timeSheetNote = Optional.ofNullable(node.get("TimeSheetNote")).map(JsonNode::asText).orElse("");
        workforce.setTimeSheetNote(timeSheetNote);

        final String engagementProject = Optional.ofNullable(node.get(ENGAGEMENT_PROJECT)).map(JsonNode::asText).orElse("");
        workforce.setEngagementProject(engagementProject);

        final String timesheetStatus = Optional.ofNullable(node.get(TIME_SHEET_STATUS)).map(JsonNode::asText).orElse("");
        workforce.setTimesheetStatus(timesheetStatus);
        return workforce;

    }
}
