package com.sap.csc.timebackend.service;

import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.workforcetimesheet.TimeSheetDataFields;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.workforcetimesheet.TimeSheetEntry;
import com.sap.csc.timebackend.config.Constants;
import com.sap.csc.timebackend.exceptions.*;
import com.sap.csc.timebackend.helper.Helper;
import com.sap.csc.timebackend.helper.Tuple;
import com.sap.csc.timebackend.model.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.sap.csc.timebackend.config.Constants.*;
import static java.time.temporal.ChronoUnit.MINUTES;

@Component
public class DayServiceImpl implements DayService {

    @Override
    public List<TimeSheetEntry> delete(Day day) {
        validateDeletion(day);


        final List<TimeSheetEntry> deletedEntries = new ArrayList<>(5);
        final String user = day.getUser();
        deletedEntries.add(createDeletedTask(day.getTaskTime().getFirst(), user));
        deletedEntries.add(createDeletedTask(day.getTaskTime().getSecond(), user));
        deletedEntries.add(createDeletedBreak(day.getBreakTime(), user));
        deletedEntries.add(createDeletedTravel(day.getTravelTime().getFirst(), user));
        deletedEntries.add(createDeletedTravel(day.getTravelTime().getSecond(), user));
        return deletedEntries;
    }

    @Override
    public List<TimeSheetEntry> create(Day day, String user) {
        validateCreation(day);
        final List<TimeSheetEntry> entries = new ArrayList<>(5);
        final Tuple<TimeSheetEntry, TimeSheetEntry> tasks = createTaskEntries(day, user);
        final Tuple<TimeSheetEntry, TimeSheetEntry> travels = createTravelEntries(day, user);
        final TimeSheetEntry breakEntry = createBreakEntry(day, user);

        entries.add(travels.getFirst());
        entries.add(tasks.getFirst());
        entries.add(breakEntry);
        entries.add(tasks.getSecond());
        entries.add(travels.getSecond());

        return entries;
    }

    private void validateCreation(Day day) {

        hasMinimumTravelTime(day);
        hasMinimumBreakTime(day);


        breakTimeIsValid(day);

        breakDurationIsValid(day);
        taskTimeIsValid(day);
    }

    private void hasMinimumBreakTime(Day day) {
        if (day.getBreakTime().getDuration() < 30)
            throw MinimumBreakTimeException.create("Break duration is too short!\nIt must not be lower than 30 minutes!");

    }

    private void breakDurationIsValid(Day day) {
        if (day.getBreakTime().getStartTime().plusMinutes(day.getBreakTime().getDuration()).isAfter(day.getTaskTime().getSecond().getEndTime()))
            throw BreakTooLongException.create("Break is too long!\n It must be between task start time and task end time!");

    }

    private void breakTimeIsValid(Day day) {
        if (day.getTaskTime().getFirst().getStartTime().isAfter(day.getBreakTime().getStartTime()))
            throw InvalidTimeException.create("Task start time is after break start time!");

    }

    private void taskTimeIsValid(Day day) {
        if (day.getTaskTime().getFirst().getStartTime().isAfter(day.getTaskTime().getSecond().getEndTime()))
            throw InvalidTimeException.create("Task start time is after end time!");

        if (day.getTaskTime().getFirst().getStartTime().until(day.getBreakTime().getStartTime(), MINUTES) < 30)
            throw MinimumTaskTimeException.create("First task must not be lower than 30 minutes!");


        if (day.getBreakTime().getStartTime().plusMinutes(day.getBreakTime().getDuration())
                .until(day.getTaskTime().getSecond().getEndTime(), MINUTES) < 30)
            throw MinimumTaskTimeException.create("Second task must not be lower than 30 minutes!");

    }

    @Override
    public Day convert(List<TimeSheetEntry> entries) {

        entries.sort(compareByStartTime());

        return Day.date(createDate(entries))
                .taskTime(createTask(entries))
                .breakTime(createBreak(entries))
                .travelTime(createTravel(entries))
                .user(createUser(entries))
                .build();
    }

    @Override
    public List<TimeSheetEntry> split(List<Day> days, String user) {

        final List<TimeSheetEntry> deletedEntries = splitDeletedDays(days);

        final List<TimeSheetEntry> newEntries = splitNewDays(days, user);

        final List<TimeSheetEntry> updatedEntries = splitUpdatedDays(days, user);


        return Helper.union(newEntries, updatedEntries, deletedEntries);

    }

    private List<TimeSheetEntry> splitDeletedDays(List<Day> days) {
        return days.stream()
                .filter(d -> !hasTask(d) && hasRecordNumber(d) && !d.getUser().isEmpty())
                .map(this::delete)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<TimeSheetEntry> splitUpdatedDays(List<Day> days, String user) {
        final List<TimeSheetEntry> deletedDays = days.stream()
                .filter(d -> hasUser(d) && hasTask(d) && !d.getTaskTime().getFirst().getRecordNumber().isEmpty())
                .map(this::delete)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        final List<TimeSheetEntry> createdDays = days.stream()
                .filter(d -> hasUser(d) && hasTask(d) && !d.getTaskTime().getFirst().getRecordNumber().isEmpty())
                .map(day -> create(day, user))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        return Helper.union(deletedDays, createdDays);
    }


    private List<TimeSheetEntry> splitNewDays(List<Day> days, String user) {
        return days.stream()
                .filter(d -> !hasUser(d) && hasTask(d) && d.getTaskTime().getFirst().getRecordNumber().isEmpty())
                .map(day -> create(day, user))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private boolean hasTask(Day d) {
        return !d.getTaskTime().getFirst().getTaskType().equals(TaskType.DFLT);
    }

    private boolean hasUser(Day d) {
        return !d.getUser().isEmpty();
    }


    private boolean hasRecordNumber(Day day) {
        return !day.getTaskTime().getFirst().getRecordNumber().isEmpty();
    }

    private Tuple<TimeSheetEntry, TimeSheetEntry> createTaskEntries(Day day, String user) {
        final LocalTime firstTaskStartTime = day.getTaskTime().getFirst().getStartTime();
        final LocalTime firstTaskEndTime = day.getBreakTime().getStartTime();

        final LocalTime secondTaskStartTime = firstTaskEndTime
                .plusMinutes(day.getBreakTime().getDuration());

        final LocalTime secondTaskEndTime = day.getTaskTime().getSecond().getEndTime();

        final TaskType taskType = day.getTaskTime().getFirst().getTaskType();

        final Calendar c = getCalendarUsingLocaleDate(day.getDate());

        final TimeSheetEntry e1 = createNewEntry(firstTaskStartTime, firstTaskEndTime, taskType, c, user);
        final TimeSheetEntry e2 = createNewEntry(secondTaskStartTime, secondTaskEndTime, taskType, c, user);

        return Tuple.create(e1, e2);

    }

    private Tuple<TimeSheetEntry, TimeSheetEntry> createTravelEntries(Day day, String user) {
        final LocalTime firstTravelStartTime = day.getTaskTime().getFirst().getStartTime()
                .minusMinutes(day.getTotalTravelTime() / 2);
        final LocalTime firstTravelEndTime = day.getTaskTime().getFirst().getStartTime();

        final LocalTime secondTravelStartTime = day.getTaskTime().getSecond().getEndTime();
        final LocalTime secondTravelEndTime = secondTravelStartTime.plusMinutes(day.getTotalTravelTime() / 2);

        final Calendar c = getCalendarUsingLocaleDate(day.getDate());

        final TimeSheetEntry e1 = createNewEntry(firstTravelStartTime, firstTravelEndTime, TaskType.TRAV, c, user);
        final TimeSheetEntry e2 = createNewEntry(secondTravelStartTime, secondTravelEndTime, TaskType.TRAV, c, user);

        return Tuple.create(e1, e2);

    }

    private TimeSheetEntry createBreakEntry(Day day, String user) {
        final LocalTime breakStartTime = day.getBreakTime().getStartTime();
        final LocalTime breakEndTime = breakStartTime.plusMinutes(day.getBreakTime().getDuration());

        final Calendar c = getCalendarUsingLocaleDate(day.getDate());

        return createNewEntry(breakStartTime, breakEndTime, TaskType.MISC, c, user);

    }

    private TimeSheetEntry createNewEntry(LocalTime startTime, LocalTime endTime, TaskType taskType, Calendar c, String user) {
        final BigDecimal duration = durationInHours(startTime, endTime);
        if (duration.compareTo(BigDecimal.ZERO) < 0) {
            throw new TotalTravelTimeTooLongException("Travel time is too big!");
        }
        final TimeSheetEntry tse = TimeSheetEntry.builder()
                .personWorkAgreementExternalID(user)
                .companyCode(Constants.COMPANY_CODE)
                .timeSheetOperation(Constants.CREATE)
                .timeSheetDate(c)
                .timeSheetIsReleasedOnSave(true)
                .timeSheetIsExecutedInTestRun(false)
                .timeSheetDataFields(TimeSheetDataFields.builder()
                        .timeSheetTaskType(taskType.getAbbr())
                        .timeSheetTaskLevel(Constants.TASK_LEVEL_NONE)
                        .timeSheetTaskComponent(WORK)
                        .hoursUnitOfMeasure(Constants.HOURS)
                        .recordedHours(duration)
                        .recordedQuantity(duration)
                        .controllingArea(CONTROLLING_AREA)
                        .build()
                ).build();

        tse.setCustomField(Constants.CUSTOM_FIELD_START_TIME,
                Date.from(startTime.atDate(LocalDate.of(1970, 1, 1))
                        .atZone(ZoneId.systemDefault()).toInstant()));

        tse.setCustomField(Constants.CUSTOM_FIELD_END_TIME,
                Date.from(endTime.atDate(LocalDate.of(1970, 1, 1))
                        .atZone(ZoneId.systemDefault()).toInstant()));
        return tse;

    }

    private BigDecimal durationInHours(LocalTime startTime, LocalTime endTime) {
        final long durationInMinutes = startTime.until(endTime, MINUTES);
        final double durationInHours = (double) durationInMinutes / 60;

        return BigDecimal.valueOf(durationInHours).setScale(2, RoundingMode.HALF_UP);
    }

    private Calendar getCalendarUsingLocaleDate(LocalDate date) {
        final Calendar c = Calendar.getInstance();
        c.setTime(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        return c;
    }

    private LocalDate createDate(List<TimeSheetEntry> entries) {
        return entries.stream()
                .findFirst()
                .map(TimeSheetEntry::getTimeSheetDate)
                .orElseThrow(() -> SAPException.create("Cannot find date for time entry!"))
                .getTime()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private String createUser(List<TimeSheetEntry> entries) {
        return entries.stream()
                .findFirst()
                .map(TimeSheetEntry::getPersonWorkAgreementExternalID)
                .orElseThrow(() -> SAPException.create("Cannot find PersonWorkAgreementExternalID"));
    }

    private Break createBreak(List<TimeSheetEntry> entries) {
        final TimeSheetEntry breakEntry = entries
                .stream()
                .filter(this::isBreak).findFirst()
                .orElseThrow(() -> SAPException.create("Cannot find break"));
        return createBreakUsingEntry(breakEntry);
    }

    private Tuple<Travel, Travel> createTravel(List<TimeSheetEntry> entries) {
        final List<TimeSheetEntry> travelEntries = entries
                .stream()
                .filter(this::isTravel)
                .collect(Collectors.toList());
        return createTravelForDay(travelEntries);
    }

    private Comparator<TimeSheetEntry> compareByStartTime() {
        return Comparator.comparing(e ->
                Optional.ofNullable(e.getCustomField(Constants.CUSTOM_FIELD_START_TIME))
                        .map(Object::toString)
                        .orElseThrow(() -> SAPException.create("Start time is null!")));
    }

    private Tuple<Task, Task> createTask(List<TimeSheetEntry> entries) {
        final List<TimeSheetEntry> taskEntries = entries
                .stream()
                .filter(isTask())
                .collect(Collectors.toList());

        return createTasksForDay(taskEntries);
    }

    private Tuple<Task, Task> createTasksForDay(List<TimeSheetEntry> taskEntries) {
        if (taskEntries.size() == 2) {
            taskEntries = taskEntries.stream()
                    .sorted(compareByStartTime())
                    .collect(Collectors.toList());

            final TimeSheetEntry firstEntry = taskEntries.get(0);

            final String firstEntryStartTime = Optional
                    .ofNullable(firstEntry.getCustomField(Constants.CUSTOM_FIELD_START_TIME))
                    .map(Object::toString)
                    .orElseThrow(() -> SAPException.create(Constants.START_TIME_IS_NULL));
            final String firstEntryEndTime = Optional
                    .ofNullable(firstEntry.getCustomField(Constants.CUSTOM_FIELD_END_TIME))
                    .map(Object::toString)
                    .orElseThrow(() -> SAPException.create(Constants.END_TIME_IS_NULL));

            final String firstTaskType = Optional.ofNullable(firstEntry.getTimeSheetDataFields())
                    .map(TimeSheetDataFields::getTimeSheetTaskType)
                    .orElseThrow(() -> SAPException.create(Constants.TASK_TYPE_IS_NULL));

            final Task firstTask = Task
                    .startTime(customFormatTime(firstEntryStartTime))
                    .endTime(customFormatTime(firstEntryEndTime))
                    .taskType(TaskType.valueOf(firstTaskType))
                    .companyCode(firstEntry.getCompanyCode())
                    .recordNumber(firstEntry.getTimeSheetRecord())
                    .build();

            final TimeSheetEntry secondEntry = taskEntries.get(1);

            final String secondTaskType = Optional.ofNullable(secondEntry.getTimeSheetDataFields())
                    .map(TimeSheetDataFields::getTimeSheetTaskType)
                    .orElseThrow(() -> SAPException.create(TASK_TYPE_IS_NULL));

            final String secondEntryStartTime = Optional
                    .ofNullable(secondEntry.getCustomField(Constants.CUSTOM_FIELD_START_TIME))
                    .map(Object::toString)
                    .orElseThrow(() -> SAPException.create(Constants.START_TIME_IS_NULL));
            final String secondEntryEndTime = Optional
                    .ofNullable(secondEntry.getCustomField(Constants.CUSTOM_FIELD_END_TIME))
                    .map(Object::toString)
                    .orElseThrow(() -> SAPException.create(Constants.END_TIME_IS_NULL));
            final Task secondTask = Task
                    .startTime(customFormatTime(secondEntryStartTime))
                    .endTime(customFormatTime(secondEntryEndTime))
                    .taskType(TaskType.valueOf(secondTaskType))
                    .companyCode(secondEntry.getCompanyCode())
                    .recordNumber(secondEntry.getTimeSheetRecord())
                    .build();
            return Tuple.create(firstTask, secondTask);
        } else {
            throw SAPException.create("Could not find two tasks for a day!");
        }
    }

    private Tuple<Travel, Travel> createTravelForDay(List<TimeSheetEntry> travelEntries) {
        if (travelEntries.size() == 2) {
            TimeSheetEntry firstEntry = travelEntries.get(0);
            final Travel firstTravel = Travel.startTime(customFormatTime(firstEntry.getCustomField(Constants.CUSTOM_FIELD_START_TIME)))
                    .endTime(customFormatTime(firstEntry.getCustomField(Constants.CUSTOM_FIELD_END_TIME)))
                    .companyCode(firstEntry.getCompanyCode())
                    .recordNumber(firstEntry.getTimeSheetRecord())
                    .build();


            TimeSheetEntry secondEntry = travelEntries.get(1);
            final Travel secondTravel = Travel.startTime(customFormatTime(secondEntry.getCustomField(Constants.CUSTOM_FIELD_START_TIME)))
                    .endTime(customFormatTime(secondEntry.getCustomField(Constants.CUSTOM_FIELD_END_TIME)))
                    .companyCode(secondEntry.getCompanyCode())
                    .recordNumber(secondEntry.getTimeSheetRecord())
                    .build();
            return Tuple.create(firstTravel, secondTravel);
        } else {
            throw SAPException.create("Could not find two travels for a day!");
        }

    }

    private LocalTime customFormatTime(String dateTime) {
        return LocalTime.from((LocalTime.ofSecondOfDay(Duration.parse(dateTime).getSeconds())));
    }

    private TimeSheetEntry createDeletedTask(Task task, String user) {
        return TimeSheetEntry.builder()
                .timeSheetRecord(task.getRecordNumber())
                .companyCode(task.getCompanyCode())
                .personWorkAgreementExternalID(user)
                .timeSheetOperation(DELETE)
                .build();
    }

    private TimeSheetEntry createDeletedTravel(Travel travel, String user) {
        return TimeSheetEntry.builder()
                .timeSheetRecord(travel.getRecordNumber())
                .companyCode(travel.getCompanyCode())
                .personWorkAgreementExternalID(user)
                .timeSheetOperation(Constants.DELETE)
                .build();
    }

    private TimeSheetEntry createDeletedBreak(Break breakTime, String user) {
        return TimeSheetEntry.builder()
                .timeSheetRecord(breakTime.getRecordNumber())
                .companyCode(breakTime.getCompanyCode())
                .personWorkAgreementExternalID(user)
                .timeSheetOperation(Constants.DELETE)
                .build();
    }

    private Predicate<TimeSheetEntry> isTask() {
        return e -> !isBreak(e)
                && !isTravel(e);
    }

    private boolean isBreak(TimeSheetEntry value) {
        final Optional<String> taskType = getTaskTypeAsOptional(value);
        return taskType
                .orElseThrow(() -> SAPException.create("Task Type is null!"))
                .equals(TaskType.MISC.getAbbr());
    }

    private boolean isTravel(TimeSheetEntry value) {
        final Optional<String> taskType = getTaskTypeAsOptional(value);
        return taskType
                .orElseThrow(() -> SAPException.create("Task Type is null!"))
                .equals(TaskType.TRAV.getAbbr());
    }

    private Optional<String> getTaskTypeAsOptional(TimeSheetEntry value) {
        final Optional<TimeSheetDataFields> fields = Optional
                .ofNullable(value.getTimeSheetDataFields());
        return Optional
                .ofNullable(fields
                        .orElseThrow(() -> SAPException.create("Time Sheet Data fields are null!"))
                        .getTimeSheetTaskType());
    }

    private Break createBreakUsingEntry(TimeSheetEntry breakEntry) {
        final String breakStartTime = Optional
                .ofNullable(breakEntry.getCustomField(Constants.CUSTOM_FIELD_START_TIME))
                .map(Object::toString)
                .orElseThrow(() -> SAPException.create(Constants.CUSTOM_FIELD_START_TIME));

        final String breakEndTime = Optional
                .ofNullable(breakEntry.getCustomField(Constants.CUSTOM_FIELD_END_TIME))
                .map(Object::toString)
                .orElseThrow(() -> SAPException.create(Constants.CUSTOM_FIELD_END_TIME));

        return Break.startTime(customFormatTime(breakStartTime))
                .endTime(customFormatTime(breakEndTime))
                .companyCode(breakEntry.getCompanyCode())
                .recordNumber(breakEntry.getTimeSheetRecord())
                .build();
    }

    private void validateDeletion(Day day) {
        Helper.nullChecks(day);
        taskHasRecordNumber(day);

        travelHasRecordNumber(day);

        breakHasRecordNumber(day);

        if (!hasUser(day))
            throw MissingUserException.create("User is empty!");

        if (day.getBreakTime().getCompanyCode().isEmpty())
            throw MissingCompanyCodeException.create("Company code for break is empty!");

        if (day.getTaskTime().getFirst().getCompanyCode().isEmpty() || day.getTaskTime().getSecond().getCompanyCode().isEmpty())
            throw MissingCompanyCodeException.create("Company code for task time is empty!");


        if (day.getTravelTime().getFirst().getCompanyCode().isEmpty() || day.getTravelTime().getSecond().getCompanyCode().isEmpty())
            throw MissingCompanyCodeException.create("Company code for travel time is empty!");

    }

    private void breakHasRecordNumber(Day day) {
        if (day.getBreakTime().getRecordNumber().isEmpty())
            throw MissingRecordNumberException.create("Record Number for break time is empty!");
    }

    private void travelHasRecordNumber(Day day) {
        if (day.getTravelTime().getFirst().getRecordNumber().isEmpty() || day.getTravelTime().getSecond().getRecordNumber().isEmpty())
            throw MissingRecordNumberException.create("Record Number for travel time is empty!");
    }

    private void taskHasRecordNumber(Day day) {
        if (day.getTaskTime().getFirst().getRecordNumber().isEmpty() || day.getTaskTime().getSecond().getRecordNumber().isEmpty())
            throw MissingRecordNumberException.create("Record number for task time is empty!");
    }

    private void hasMinimumTravelTime(Day day) {
        if (day.getTotalTravelTime() < 60L) {
            throw MinimumTravelTimeException.create("Travel time must not be lower than 60 minutes!");
        }
    }
}
