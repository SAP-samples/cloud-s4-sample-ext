package com.sap.csc.timebackend.service;

import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.workforcetimesheet.TimeSheetEntry;
import com.sap.csc.timebackend.model.Day;
import com.sap.csc.timebackend.repository.TimeSheetRepository;
import com.sap.csc.timebackend.security.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class TimeSheetServiceImpl implements TimeSheetService {
    private final TimeSheetRepository timeSheetRepository;
    private final DayService dayService;
    private final AuthenticationFacade authenticationFacade;

    @Autowired
    public TimeSheetServiceImpl(TimeSheetRepository timeSheetRepository, DayService dayService, AuthenticationFacade authenticationFacade) {
        this.timeSheetRepository = timeSheetRepository;
        this.dayService = dayService;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    public List<Day> getBetween(LocalDate from, LocalDate to) throws ODataException {
        List<TimeSheetEntry> retrievedEntries = timeSheetRepository
                .getByDateAndUser(from, to, authenticationFacade.getLoggedUser().getId());

        final List<Day> days = new ArrayList<>();

        if (!retrievedEntries.isEmpty()) {
            days.addAll(createDaysUsingEntries(retrievedEntries));
        }

        addBlankDays(from, to, days);
        return days;
    }

    @Override
    public List<TimeSheetEntry> createOrUpdate(List<Day> days) {
        final List<TimeSheetEntry> entries = dayService.split(days, authenticationFacade.getLoggedUser().getId());
        return timeSheetRepository.saveAll(entries);
    }

    private List<Day> createDaysUsingEntries(List<TimeSheetEntry> timeSheetEntries) {
        return timeSheetEntries.stream()
                .collect(Collectors.groupingBy(TimeSheetEntry::getTimeSheetDate))
                .entrySet()
                .stream()
                .map(e -> dayService.convert(e.getValue()))
                .collect(Collectors.toList());
    }

    private void addBlankDays(LocalDate dateFrom, LocalDate dateTo, List<Day> days) {
        final List<LocalDate> dates = createDates(dateFrom, dateTo);

        days.forEach(day -> dates.removeIf(date -> date.equals(day.getDate())));

        days.addAll(dates.stream().map(Day::create).collect(Collectors.toList()));

        days.sort(Comparator.comparing(Day::getDate));
    }

    private List<LocalDate> createDates(LocalDate dateFrom, LocalDate dateTo) {
        final long numOfDaysBetween = ChronoUnit.DAYS.between(dateFrom, dateTo) + 1;
        return IntStream.iterate(0, i -> i + 1)
                .limit(numOfDaysBetween)
                .mapToObj(dateFrom::plusDays)
                .collect(Collectors.toList());
    }


}
