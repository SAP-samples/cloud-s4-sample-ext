package com.sap.csc.timebackend.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.workforcetimesheet.TimeSheetEntry;
import com.sap.csc.timebackend.config.Constants;
import com.sap.csc.timebackend.exceptions.*;
import com.sap.csc.timebackend.helper.Tuple;
import com.sap.csc.timebackend.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ActiveProfiles("test")

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class DayServiceTests {

    private final static String testUser = "testUser";
    private final Day day = createTestDay();
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private DayService dayService;


    @Test
    public void allEntriesAreMarkedWithDelete() {

        final List<TimeSheetEntry> deletedEntries = dayService.delete(day);
        deletedEntries.forEach(e ->
                assertTrue(Optional.ofNullable(e.getTimeSheetOperation())
                        .orElseThrow(NullPointerException::new)
                        .equals(Constants.DELETE))
        );
    }

    @Test
    public void fiveEntriesResultedInDelete() {
        assertTrue(dayService.delete(day).size() == 5);
    }

    @Test
    public void personWorkAgreementExtIdIsValid() {
        dayService.delete(day).forEach(e ->
                assertTrue(Optional
                        .ofNullable(e.getPersonWorkAgreementExternalID())
                        .orElseThrow(NullPointerException::new)
                        .equals(testUser)));
    }

    @Test
    public void recordNumberIsValid() {
        int i = 1;
        for (TimeSheetEntry e : dayService.delete(day)) {
            assertTrue(Optional
                    .ofNullable(e.getTimeSheetRecord())
                    .orElseThrow(NullPointerException::new)
                    .equals(String.valueOf(i++)));

        }
    }

    @Test(expected = MissingRecordNumberException.class)
    public void missingRecordNumberThrowsException() {
        dayService.delete(createDayWithMissingRecNumber());
    }

    @Test(expected = MissingUserException.class)
    public void missingUserThrowsException() {
        dayService.delete(createDayWithMissingUser());
    }

    @Test(expected = MissingCompanyCodeException.class)
    public void missingCompanyCodeThrowsException() {
        dayService.delete(createDayWithMissingCompCode());
    }

    @Test(expected = InvalidTimeException.class)
    public void startTimeShouldBeLtEndTime() {
        dayService.create(createDayWithInvalidTime(), testUser);

    }

    @Test(expected = BreakTooLongException.class)
    public void breakDurationShouldNotExceedSecondTaskEndTime() throws IOException {
        final Day day = mapper.readValue(getFile("dayWithLongerBreakThanExpected.json"), Day.class);
        dayService.create(day, testUser);
    }


    @Test
    public void fiveEntriesAreResultedInCreate() throws IOException {
        final Day day = readDayFromJson();
        final List<TimeSheetEntry> resultedEntries = dayService.create(day, testUser);
        assertEquals(5, resultedEntries.size());
    }


    @Test
    public void entriesStartTimeIsValid() throws IOException {
        final Day day = readDayFromJson();
        final List<TimeSheetEntry> resultedEntries = dayService.create(day, testUser);

        final Date firstTravelStart = (Date) Optional.ofNullable(resultedEntries.get(0).getCustomField(Constants.CUSTOM_FIELD_START_TIME))
                .orElseThrow(() -> InvalidTimeException.create("First travel start time is missing!"));
        final LocalTime firstTravelStartTime = firstTravelStart.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

        final Date firstTaskStart = (Date) Optional.ofNullable(resultedEntries.get(1).getCustomField(Constants.CUSTOM_FIELD_START_TIME))
                .orElseThrow(() -> InvalidTimeException.create("First task start time is missing!"));
        final LocalTime firstTaskStartTime = firstTaskStart.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

        final Date breakStart = (Date) Optional.ofNullable(resultedEntries.get(2).getCustomField(Constants.CUSTOM_FIELD_START_TIME))
                .orElseThrow(() -> InvalidTimeException.create("Break time start is missing!"));
        final LocalTime breakStartTime = breakStart.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

        final Date secondTaskStart = (Date) Optional.ofNullable(resultedEntries.get(3).getCustomField(Constants.CUSTOM_FIELD_START_TIME))
                .orElseThrow(() -> InvalidTimeException.create("Second task start time is missing!"));
        final LocalTime secondTaskStartTime = secondTaskStart.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

        final Date secondTravelStart = (Date) Optional.ofNullable(resultedEntries.get(4).getCustomField(Constants.CUSTOM_FIELD_START_TIME))
                .orElseThrow(() -> InvalidTimeException.create("Second travel start time is missing!"));
        final LocalTime secondTravelStartTime = secondTravelStart.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        System.out.println(secondTaskStartTime);
        System.out.println(secondTaskStart);
        assertEquals(LocalTime.parse("06:30"), firstTravelStartTime);
        assertEquals(LocalTime.parse("07:00"), firstTaskStartTime);
        assertEquals(LocalTime.parse("12:00"), breakStartTime);
        assertEquals(LocalTime.parse("13:00"), secondTaskStartTime);
        assertEquals(LocalTime.parse("17:00"), secondTravelStartTime);

    }

    @Test
    public void entriesEndTimeIsValid() throws IOException {
        final Day day = readDayFromJson();
        final List<TimeSheetEntry> resultedEntries = dayService.create(day, testUser);
        final TimeSheetEntry firstTravel = resultedEntries.get(0);
        assertEquals(firstTravel.getTimeSheetOperation(), Constants.CREATE);

        final Date firstTravelEnd = (Date) Optional.ofNullable(firstTravel.getCustomField(Constants.CUSTOM_FIELD_END_TIME))
                .orElseThrow(() -> InvalidTimeException.create("First travel end time is missing!"));
        final LocalTime firstTravelEndTime = firstTravelEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

        final Date firstTaskEnd = (Date) Optional.ofNullable(resultedEntries.get(1).getCustomField(Constants.CUSTOM_FIELD_END_TIME))
                .orElseThrow(() -> InvalidTimeException.create("First task end time is missing!"));
        final LocalTime firstTaskEndTime = firstTaskEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

        final Date breakEnd = (Date) Optional.ofNullable(resultedEntries.get(2).getCustomField(Constants.CUSTOM_FIELD_END_TIME))
                .orElseThrow(() -> InvalidTimeException.create("Break end time is missing!"));
        final LocalTime breakEndTime = breakEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

        final Date secondTaskEnd = (Date) Optional.ofNullable(resultedEntries.get(3).getCustomField(Constants.CUSTOM_FIELD_END_TIME))
                .orElseThrow(() -> InvalidTimeException.create("Second task end time is missing!"));
        LocalTime secondTaskEndTime = secondTaskEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

        final Date secondTravelEnd = (Date) Optional.ofNullable(resultedEntries.get(4).getCustomField(Constants.CUSTOM_FIELD_END_TIME))
                .orElseThrow(() -> InvalidTimeException.create("Second travel end time is missing!"));
        final LocalTime secondTravelEndTime = secondTravelEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();


        assertEquals(LocalTime.parse("07:00"), firstTravelEndTime);
        assertEquals(LocalTime.parse("12:00"), firstTaskEndTime);
        assertEquals(LocalTime.parse("13:00"), breakEndTime);
        assertEquals(LocalTime.parse("17:00"), secondTaskEndTime);
        assertEquals(LocalTime.parse("17:30"), secondTravelEndTime);
    }


    @Test
    public void twoEntriesMustBeTravel() throws IOException {
        final Day day = readDayFromJson();
        final List<TimeSheetEntry> resultedEntries = dayService.create(day, testUser);

        long travelCounter = resultedEntries.stream()
                .filter(e -> Objects.equals(Objects.requireNonNull(e.getTimeSheetDataFields()).getTimeSheetTaskType(), TaskType.TRAV.getAbbr()))
                .count();
        assertEquals(2, travelCounter);

    }

    @Test
    public void twoEntriesMustBeTask() throws IOException {
        final Day day = readDayFromJson();
        final List<TimeSheetEntry> resultedEntries = dayService.create(day, testUser);

        long taskCounter = resultedEntries.stream()
                .filter(e -> !Objects.equals(Objects.requireNonNull(e.getTimeSheetDataFields()).getTimeSheetTaskType(), TaskType.TRAV.getAbbr())
                        && !Objects.equals(Objects.requireNonNull(e.getTimeSheetDataFields()).getTimeSheetTaskType(), TaskType.MISC.getAbbr()))
                .count();
        assertEquals(2, taskCounter);
    }

    @Test
    public void oneEntryMustBeBreak() throws IOException {
        final Day day = readDayFromJson();
        final List<TimeSheetEntry> resultedEntries = dayService.create(day, testUser);

        long breakCounter = resultedEntries.stream()
                .filter(e -> Objects.equals(Objects.requireNonNull(e.getTimeSheetDataFields()).getTimeSheetTaskType(), TaskType.MISC.getAbbr()))
                .count();
        assertEquals(1, breakCounter);
    }

    @Test
    public void allEntriesHaveTimeSheetOperationCreate() throws IOException {
        final Day day = readDayFromJson();
        final List<TimeSheetEntry> resultedEntries = dayService.create(day, testUser);

        resultedEntries.forEach(e -> assertEquals(Constants.CREATE, e.getTimeSheetOperation()));

    }

    @Test
    public void userIsValidForAllEntries() throws IOException {
        final Day day = readDayFromJson();
        final List<TimeSheetEntry> resultedEntries = dayService.create(day, testUser);
        resultedEntries.forEach(e -> assertEquals(testUser, e.getPersonWorkAgreementExternalID()));

    }


    private Day createDayWithInvalidTime() {
        final Task firstTask = createTestTask("13:00", "12:00", "1", "01");
        final Task secondTask = createTestTask("13:00", "17:00", "2", "01");
        final Break breakTime = createTestBreak();
        final Travel firstTravel = createTestTravel("08:00", "09:00", "4");

        final Travel secondTravel = createTestTravel("17:00", "18:00", "5");

        return Day.date(LocalDate.parse("2018-03-27"))
                .taskTime(Tuple.create(firstTask, secondTask))
                .breakTime(breakTime)
                .travelTime(Tuple.create(firstTravel, secondTravel))
                .user(testUser)
                .build();

    }

    private Day createDayWithMissingCompCode() {
        final Task firstTask = createTestTask("09:00", "12:00", "1", "");
        final Task secondTask = createTestTask("13:00", "17:00", "2", "01");
        final Break breakTime = createTestBreak();
        final Travel firstTravel = createTestTravel("08:00", "09:00", "4");

        final Travel secondTravel = createTestTravel("17:00", "18:00", "5");

        return Day.date(LocalDate.parse("2018-03-27"))
                .taskTime(Tuple.create(firstTask, secondTask))
                .breakTime(breakTime)
                .travelTime(Tuple.create(firstTravel, secondTravel))
                .user(testUser)
                .build();

    }

    private Day createDayWithMissingUser() {
        final Task firstTask = createTestTask("09:00", "12:00", "1", "01");
        final Task secondTask = createTestTask("13:00", "17:00", "2", "01");
        final Break breakTime = createTestBreak();
        final Travel firstTravel = createTestTravel("08:00", "09:00", "4");

        final Travel secondTravel = createTestTravel("17:00", "18:00", "5");

        return Day.date(LocalDate.parse("2018-03-27"))
                .taskTime(Tuple.create(firstTask, secondTask))
                .breakTime(breakTime)
                .travelTime(Tuple.create(firstTravel, secondTravel))
                .user("")
                .build();

    }

    private Day createDayWithMissingRecNumber() {
        final Task firstTask = createTestTask("09:00", "12:00", "", "01");
        final Task secondTask = createTestTask("13:00", "17:00", "2", "01");
        final Break breakTime = createTestBreak();
        final Travel firstTravel = createTestTravel("08:00", "09:00", "4");

        final Travel secondTravel = createTestTravel("17:00", "18:00", "5");

        return Day.date(LocalDate.parse("2018-03-27"))
                .taskTime(Tuple.create(firstTask, secondTask))
                .breakTime(breakTime)
                .travelTime(Tuple.create(firstTravel, secondTravel))
                .user(testUser)
                .build();

    }


    private Day createTestDay() {
        final Task firstTask = createTestTask("09:00", "12:00", "1", "01");

        final Task secondTask = createTestTask("13:00", "17:00", "2", "01");

        final Tuple<Task, Task> tasks = Tuple.create(firstTask, secondTask);

        final Break breakTime = createTestBreak();

        final Travel firstTravel = createTestTravel("08:00", "09:00", "4");

        final Travel secondTravel = createTestTravel("17:00", "18:00", "5");

        final Tuple<Travel, Travel> travels = Tuple.create(firstTravel, secondTravel);


        return Day.date(LocalDate.parse("2018-03-27"))
                .taskTime(tasks)
                .breakTime(breakTime)
                .travelTime(travels)
                .user(testUser)
                .build();
    }

    private Travel createTestTravel(String start, String end, String recordNumber) {
        return Travel.startTime(LocalTime.parse(start))
                .endTime(LocalTime.parse(end))
                .companyCode("01")
                .recordNumber(recordNumber)
                .build();
    }

    private Break createTestBreak() {
        return Break.startTime(LocalTime.parse("12:00"))
                .endTime(LocalTime.parse("13:00"))
                .companyCode("01")
                .recordNumber("3")
                .build();
    }


    private Task createTestTask(String startTime, String endTime, String recNumber, String compCode) {
        return Task.startTime(LocalTime.parse(startTime))
                .endTime(LocalTime.parse(endTime))
                .taskType(TaskType.ADMI)
                .companyCode(compCode)
                .recordNumber(recNumber)
                .build();

    }

    private File getFile(String fileName) {
        return new File(Objects.requireNonNull(getClass().getClassLoader().getResource(fileName)).getFile());
    }

    private Day readDayFromJson() throws IOException {
        return mapper.readValue(getFile("oneDay.json"), Day.class);
    }


}
