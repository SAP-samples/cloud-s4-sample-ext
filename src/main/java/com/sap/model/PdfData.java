package com.sap.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@XmlRootElement(name = "TimesheetData")
public class PdfData implements Serializable {

    private static final long serialVersionUID = 3832117415259920854L;

    @XmlElement(name = "Employee")
    private final TimesheetHeader header;

    @XmlElement(name = "Timesheets")
    private final TimesheetBody body;

    @XmlElement(name = "Signature")
    private final TimesheetFooter footer;

    @XmlElement(name = "SignedOn")
    private final SignedOn signedOn;

    private PdfData() {
        this.footer = new TimesheetFooter();
        this.header = new TimesheetHeader();
        this.body = new TimesheetBody();
        this.signedOn = new SignedOn();
    }

    private PdfData(TimesheetHeader header, TimesheetBody body, TimesheetFooter footer) {
        this.header = header;
        this.body = body;
        this.footer = footer;
        this.signedOn = new SignedOn();
    }


    public static PdfData create(List<Workforce> workforces, String signature) {
        final BigDecimal totalHours = workforces.stream()
                .map(Workforce::getRecordedHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final TimesheetHeader header = new TimesheetHeader();
        header.employeeName = workforces.stream().findAny().orElse(new Workforce()).getBusinessPartnerFullName();
        header.totalHours = totalHours;
        header.employeeId = workforces.stream().findAny().orElse(new Workforce()).getPersonWorkAgreement();

        final TimesheetBody body = new TimesheetBody();

        body.table = new BodyTable();

        body.table.timesheets = workforces.stream()
                .map(PdfData::createRow)
                .collect(Collectors.toList());

        final TimesheetFooter footer = new TimesheetFooter(signature);


        return new PdfData(header, body, footer);
    }

    private static Row createRow(Workforce workforce) {
        final Row row = new Row();
        row.customer = workforce.getCustomerFullName();
        row.date = workforce.getCalendarDate().toString().replaceAll("[^\\d]", "");
        row.project = workforce.getEngagementProjectName();
        row.hours = workforce.getRecordedHours();
        row.wbsDescription = workforce.getWbsDescription();
        row.workItemName = workforce.getWorkItemName();
        row.workPackageId = workforce.getWorkPackage();
        row.workPackageName = workforce.getWorkPackageName();
        row.timesheetNote = workforce.getTimeSheetNote();
        return row;
    }

    private static class TimesheetHeader {

        @XmlElement(name = "TotalRecordedHours")
        private BigDecimal totalHours;
        @XmlElement(name = "BusinessPartnerFullName")
        private String employeeName;
        @XmlElement(name = "PersonWorkAgreement")
        private String employeeId;


    }

    private static class TimesheetBody {
        @XmlElement(name = "TimesheetTable")
        private BodyTable table;


    }

    private static class BodyTable {
        @XmlElement(name = "Timesheet")
        private List<Row> timesheets;

        private BodyTable() {
            this.timesheets = Collections.emptyList();
        }


    }


    private static class Row {
        @XmlElement(name = "CustomerFullName")
        private String customer;

        @XmlElement(name = "EngagementProjectName")
        private String project;

        @XmlElement(name = "TimeSheetDate")
        private String date;

        @XmlElement(name = "TimesheetNote")
        private String timesheetNote;

        @XmlElement(name = "RecordedHours")
        private BigDecimal hours;

        @XmlElement(name = "WBSDescription")
        private String wbsDescription;

        @XmlElement(name = "WorkItemName")
        private String workItemName;

        @XmlElement(name = "WorkPackageID")
        private String workPackageId;


        @XmlElement(name = "WorkPackageName")
        private String workPackageName;
    }

    private static class TimesheetFooter {
        @XmlElement(name = "Signature")
        private final String signature;

        private TimesheetFooter() {
            this.signature = "";
        }

        private TimesheetFooter(String signature) {
            this.signature = signature;
        }
    }

    private static class SignedOn {
        @XmlElement(name = "Date")
        private String date;

        private SignedOn() {
            this.date = LocalDate.now().format(DateTimeFormatter.ofPattern("YYYYMMdd"));
        }

    }
}
