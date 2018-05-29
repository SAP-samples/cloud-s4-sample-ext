package com.sap.csc.timebackend.service;

import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.workforcetimesheet.TimeSheetEntry;
import com.sap.csc.timebackend.model.Day;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DayService {


    /**
     * @param day Day that will be deleted.
     * @return A TimeSheetEntry list with TimeSheetOperation DELETE.
     */
    List<TimeSheetEntry> delete(Day day);

    /**
     * @param day  Day that will be created in S4.
     * @param user The user whose entries will be assigned to.
     * @return Entries resulted from conversion.
     */
    List<TimeSheetEntry> create(Day day, String user);

    /**
     * @param entries Entries which will be converted to a Day object.
     * @return Day resulted from conversion.
     */
    Day convert(List<TimeSheetEntry> entries);


    /**
     * @param days Created, updated or deleted days.
     * @param user The user whose new entries will be assigned to
     * @return Entries with TimeSheetOperation CREATE or DELETE.
     */
    List<TimeSheetEntry> split(List<Day> days, String user);


}
