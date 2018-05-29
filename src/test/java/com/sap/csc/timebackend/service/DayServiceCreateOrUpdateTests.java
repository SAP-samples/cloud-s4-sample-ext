package com.sap.csc.timebackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.workforcetimesheet.TimeSheetEntry;
import com.sap.csc.timebackend.config.Constants;
import com.sap.csc.timebackend.exceptions.MinimumBreakTimeException;
import com.sap.csc.timebackend.exceptions.MinimumTaskTimeException;
import com.sap.csc.timebackend.exceptions.MinimumTravelTimeException;
import com.sap.csc.timebackend.model.Day;
import com.sap.csc.timebackend.security.AuthenticationFacade;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
@ActiveProfiles("test")

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class DayServiceCreateOrUpdateTests {

    private static final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private DayService dayService;
    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Test
    public void createThreeNewDaysResultsFifteenEntries() throws IOException {
        final List<Day> threeNewDays = Arrays.asList(mapper.readValue(getFile("threeNewDays.json"), Day[].class));

        final List<TimeSheetEntry> entries = dayService.split(threeNewDays, authenticationFacade.getLoggedUser().getId());

        assertEquals(15, entries.size());

    }

    @Test
    public void createThreeNewDaysResultsAllEntriesHaveCreateOperation() throws IOException {
        final List<Day> threeNewDays = Arrays.asList(mapper.readValue(getFile("threeNewDays.json"), Day[].class));

        final List<TimeSheetEntry> entries = dayService.split(threeNewDays, authenticationFacade.getLoggedUser().getId());

        entries.forEach(e -> assertTrue(Objects.equals(e.getTimeSheetOperation(), Constants.CREATE)));

    }

    @Test
    public void deleteThreeExistingDaysResultsFifteenEntries() throws IOException {
        final List<Day> threeDeletedDays = Arrays.asList(mapper.readValue(getFile("deleteThreeExistingDays.json"), Day[].class));

        final List<TimeSheetEntry> entries = dayService.split(threeDeletedDays, authenticationFacade.getLoggedUser().getId());

        assertEquals(15, entries.size());
    }

    @Test
    public void deleteThreeExistingDaysResultsAllEntriesHaveDeleteOperation() throws IOException {
        final List<Day> threeDeletedDays = Arrays.asList(mapper.readValue(getFile("deleteThreeExistingDays.json"), Day[].class));

        final List<TimeSheetEntry> entries = dayService.split(threeDeletedDays, authenticationFacade.getLoggedUser().getId());

        entries.forEach(e -> assertTrue(Objects.equals(e.getTimeSheetOperation(), Constants.DELETE)));
    }

    @Test
    public void deleteOneOfThreeDaysResultsFifteenEntriesWithDeleteOperationAndTenWithCreate() throws IOException {

        final List<Day> threeDaysOneDeleted = Arrays.asList(mapper.readValue(getFile("deleteOneOfThreeExistingDays.json"), Day[].class));

        final List<TimeSheetEntry> entries = dayService.split(threeDaysOneDeleted, authenticationFacade.getLoggedUser().getId());

        final long deletedEntries = entries.stream().filter(e -> Objects.equals(e.getTimeSheetOperation(), Constants.DELETE)).count();
        final long createdEntries = entries.stream().filter(e -> Objects.equals(e.getTimeSheetOperation(), Constants.CREATE)).count();

        assertEquals(15, deletedEntries);
        assertEquals(10, createdEntries);
    }

    @Test
    public void resultedDaysNotContainDateOfDeletedDay() throws IOException {

        final List<Day> threeDaysOneDeleted = Arrays.asList(mapper.readValue(getFile("deleteOneOfThreeExistingDays.json"), Day[].class));

        final List<TimeSheetEntry> entries = dayService.split(threeDaysOneDeleted, authenticationFacade.getLoggedUser().getId());

        final List<LocalDate> remainDaysDate = entries.stream()
                .filter(e -> Objects.equals(e.getTimeSheetOperation(), Constants.CREATE))
                .collect(Collectors.groupingBy(TimeSheetEntry::getTimeSheetDate))
                .entrySet().stream().map(Map.Entry::getKey)
                .map(Calendar::getTime)
                .map(Date::toInstant)
                .map(i -> i.atZone(ZoneId.systemDefault()))
                .map(ZonedDateTime::toLocalDate)
                .collect(Collectors.toList());
        final LocalDate deletedDayDate = LocalDate.of(2018, 2, 20);
        assertFalse(remainDaysDate.contains(deletedDayDate));
    }

    @Test
    public void createOneDayDeleteAnother() throws IOException {
        final List<Day> oneDayCreatedAnotherDeleted = Arrays.asList(mapper.readValue(getFile("deleteOneDayCreateAnother.json"), Day[].class));

        final List<TimeSheetEntry> entries = dayService.split(oneDayCreatedAnotherDeleted, authenticationFacade.getLoggedUser().getId());

        long createdEntries = entries.stream()
                .filter(e -> Objects.equals(e.getTimeSheetOperation(), Constants.CREATE))
                .count();
        long deletedEntries = entries.stream()
                .filter(e -> Objects.equals(e.getTimeSheetOperation(), Constants.DELETE))
                .count();
        assertEquals(5, createdEntries);
        assertEquals(5, deletedEntries);
    }

    @Test
    public void createOneDayDeleteAnotherDateIsValid() throws IOException {
        final List<Day> deleteOneDayCreateAnother = Arrays.asList(mapper.readValue(getFile("deleteOneDayCreateAnother.json"), Day[].class));

        final List<TimeSheetEntry> entries = dayService.split(deleteOneDayCreateAnother, authenticationFacade.getLoggedUser().getId());

        final LocalDate dateOfCreatedEntry = entries.stream()
                .filter(e -> Objects.equals(e.getTimeSheetOperation(), Constants.CREATE))
                .findAny()
                .map(TimeSheetEntry::getTimeSheetDate)
                .map(Calendar::getTime)
                .map(Date::toInstant)
                .map(i -> i.atZone(ZoneId.systemDefault()))
                .map(ZonedDateTime::toLocalDate)
                .orElse(LocalDate.now());

        entries.stream()
                .filter(e -> Objects.equals(e.getTimeSheetOperation(), Constants.DELETE))
                .filter(e -> Objects.isNull(e.getTimeSheetDate()))
                .map(TimeSheetEntry::getTimeSheetDate)
                .forEach(Assert::assertNull);

        assertEquals(deleteOneDayCreateAnother.get(1).getDate(), dateOfCreatedEntry);
    }

    @Test
    public void createOneDeleteOneUpdateOneResultsTenCreatedEntriesAndTenDeleted() throws IOException {
        final List<Day> deleteOneDayCreateAnother = Arrays.asList(mapper.readValue(getFile("deleteOneCreateOneUpdateOne.json"), Day[].class));

        final List<TimeSheetEntry> entries = dayService.split(deleteOneDayCreateAnother, authenticationFacade.getLoggedUser().getId());

        long createdDays = entries.stream()
                .filter(e -> Objects.equals(e.getTimeSheetOperation(), Constants.CREATE))
                .count();

        long deletedDays = entries.stream()
                .filter(e -> Objects.equals(e.getTimeSheetOperation(), Constants.CREATE))
                .count();

        assertEquals(10, createdDays);
        assertEquals(10, deletedDays);
    }

    @Test
    public void createOneDeleteOneUpdateOneOnlyCreatedAndDeletedHaveDate() throws IOException {
        final List<Day> deleteOneDayCreateAnother = Arrays.asList(mapper.readValue(getFile("deleteOneCreateOneUpdateOne.json"), Day[].class));

        final List<TimeSheetEntry> entries = dayService.split(deleteOneDayCreateAnother, authenticationFacade.getLoggedUser().getId());

        List<LocalDate> createdDates = entries.stream()
                .filter(e -> Objects.equals(e.getTimeSheetOperation(), Constants.CREATE))
                .collect(Collectors.groupingBy(TimeSheetEntry::getTimeSheetDate))
                .keySet()
                .stream()
                .map(Calendar::getTime)
                .map(Date::toInstant)
                .map(i -> i.atZone(ZoneId.systemDefault()))
                .map(ZonedDateTime::toLocalDate)
                .collect(Collectors.toList());
        assertEquals(2, createdDates.size());
        assertTrue(createdDates.contains(LocalDate.parse("2018-03-27")));
        assertTrue(createdDates.contains(LocalDate.parse("2018-03-28")));

        long deletedDays = entries.stream()
                .filter(e -> Objects.equals(e.getTimeSheetOperation(), Constants.CREATE))
                .count();

        assertEquals(10, deletedDays);
    }

    @Test(expected = MinimumTravelTimeException.class)
    public void lowerTotalTravelTimeThanAnHourThrowsException() throws IOException {
        final Day day = mapper.readValue(getFile("dayWithTravelTimeLowerThanAnHour.json"), Day.class);
        dayService.split(Collections.singletonList(day), authenticationFacade.getLoggedUser().getId());
    }

    @Test(expected = MinimumBreakTimeException.class)
    public void lowerBreakTimeThanThirtyMinutesThrowsException() throws IOException {
        final Day day = mapper.readValue(getFile("dayWithBreakTimeLowerThanThirtyMinutes.json"), Day.class);
        dayService.split(Collections.singletonList(day), authenticationFacade.getLoggedUser().getId());
    }

    @Test(expected = MinimumTaskTimeException.class)
    public void firstTaskTimeLowerThanThirtyMinutesThrowsException() throws IOException {
        final Day day = mapper.readValue(getFile("dayWithFirstTaskTimeLowerThanThirtyMinutes.json"), Day.class);
        dayService.split(Collections.singletonList(day), authenticationFacade.getLoggedUser().getId());
    }

    private File getFile(String fileName) {
        return new File(Objects.requireNonNull(getClass().getClassLoader().getResource(fileName)).getFile());
    }
}
