package com.sap.csc.timebackend.repository;

import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.workforcetimesheet.TimeSheetEntry;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Used to access TimeSheetEntries in S4/HANA system.
 */
@Repository
public interface TimeSheetRepository {


    /**
     * @param dateFrom Start date.
     * @param dateTo   End date.
     * @param user     User who's entries are assigned to.
     * @return Existing entries in S4/HANA filtered by dateFrom, dateTo and user.
     * @throws ODataException
     */
    List<TimeSheetEntry> getByDateAndUser(LocalDate dateFrom, LocalDate dateTo, String user) throws ODataException;

    /**
     * @param entries Entries that will be saved to S4/HANA.
     * @return Resulted entries after saving operation to S4/HANA. These entries might have changed completely.
     */
    List<TimeSheetEntry> saveAll(List<TimeSheetEntry> entries);

}
