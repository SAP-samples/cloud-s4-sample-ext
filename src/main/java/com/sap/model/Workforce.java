package com.sap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sap.json.LocalDateDeserializer;
import com.sap.json.LocalDateSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Workforce {

    private String id;
    private String personWorkAgreement;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate calendarDate;

    private String customerFullName;
    private String customer;
    private String projectManager;
    private String hoursUnitOfMeasure;
    private BigDecimal recordedHours;
    private String businessPartnerName;
    private String activityType;
    private String engagementProjectCategory;
    private String accountingIndicatorCode;
    private BigDecimal billableHours;
    private BigDecimal nonBillableHours;
    private String unitHour;
    private String dateYear;
    private String dateMonth;
    private String personWorkAgreementExternalID;
    private String engagementProjectName;
    private String businessPartnerFullName;
    private String timeSheetRecord;

    private String wbsDescription;
    private String wbsElementInternalId;
    private String workPackage;
    private String workPackageName;
    private String workItem;
    private String workItemName;
    private String timeSheetNote;
    private String engagementProject;
    private String timesheetStatus;

    public Workforce() {
    }

}
