package com.sap.csc.timebackend.service;

import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.workforcetimesheet.TimeSheetEntry;
import com.sap.csc.timebackend.model.Day;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface TimeSheetService {


    /**
     * @param from Start date.
     * @param to   End date.
     * @return Filtered days using start and end dates.
     * @throws ODataException
     */
    List<Day> getBetween(LocalDate from, LocalDate to) throws ODataException;

    /**
     * @param days Create, update or delete days in S4.
     * @return Resulted entries from S4 after performing CUD operations.
     */
    List<TimeSheetEntry> createOrUpdate(List<Day> days);

}
