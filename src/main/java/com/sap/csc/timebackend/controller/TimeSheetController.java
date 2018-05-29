package com.sap.csc.timebackend.controller;

import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.workforcetimesheet.TimeSheetEntry;
import com.sap.csc.timebackend.config.Constants;
import com.sap.csc.timebackend.exceptions.InvalidTimeException;
import com.sap.csc.timebackend.exceptions.SAPBadRequestException;
import com.sap.csc.timebackend.helper.Helper;
import com.sap.csc.timebackend.model.Day;
import com.sap.csc.timebackend.model.TaskType;
import com.sap.csc.timebackend.service.TimeSheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/timesheet")
public class TimeSheetController {


    private final TimeSheetService timeSheetService;

    @Autowired
    public TimeSheetController(TimeSheetService timeSheetService) {
        this.timeSheetService = timeSheetService;
    }


    @GetMapping
    public ResponseEntity<List<Day>> getDays(
            @RequestParam @DateTimeFormat(pattern = Constants.DD_MM_YYYY) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(pattern = Constants.DD_MM_YYYY) LocalDate dateTo) throws ODataException {

        validateDate(dateFrom, dateTo);
        return ResponseEntity.ok(timeSheetService.getBetween(dateFrom, dateTo));


    }


    @PostMapping
    public ResponseEntity<List<TimeSheetEntry>> createOrUpdate(@RequestBody List<Day> days) {

        Helper.nullChecks(days);
        days.forEach(d -> {
            Helper.nullChecks(d);
            validateTaskType(d);
            validateTaskTime(d);
        });

        return ResponseEntity.accepted().body(timeSheetService.createOrUpdate(days));
    }

    private void validateTaskTime(Day d) {

        if (d.getTaskTime().getFirst().getStartTime().isBefore(LocalTime.parse("00:30"))) {
            if (!d.getTaskTime().getFirst().getStartTime().equals(LocalTime.parse("00:00"))) {
                throw InvalidTimeException.create("Start time must be greater than 00:30");
            }
        }

        if (d.getTaskTime().getSecond().getEndTime().isAfter(LocalTime.parse("23:30"))) {
            if (!d.getTaskTime().getSecond().getEndTime().equals(LocalTime.parse("00:00"))) {
                throw InvalidTimeException.create("End time must lower than 23:30");
            }
        }

    }


    private void validateTaskType(Day day) {

        if (day.getTaskTime().getFirst().getTaskType().equals(TaskType.DFLT)
                && day.getBreakTime().getDuration() != 0
                && day.getTotalTravelTime() != 0) {
            throw new SAPBadRequestException("Missing task type!");
        }

        if (day.getTaskTime().getSecond().getTaskType().equals(TaskType.DFLT)
                && day.getBreakTime().getDuration() != 0
                && day.getTotalTravelTime() != 0) {
            throw new SAPBadRequestException("Missing task type!");
        }
    }


    private void validateDate(LocalDate dateFrom, LocalDate dateTo) {

        if (dateFrom.isAfter(dateTo)) {
            throw new SAPBadRequestException("Invalid date!");
        }
    }

}
