package com.sap.csc.timebackend.service;


import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.workforcetimesheet.TimeSheetEntry;
import com.sap.csc.timebackend.repository.TimeSheetRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ActiveProfiles("test")

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TimeSheetRepositoryTests {

    @Autowired
    private TimeSheetRepository timeSheetRepository;


    @Test(expected = NullPointerException.class)
    public void nullUserTest() throws ODataException {

        timeSheetRepository.getByDateAndUser(LocalDate.now(), LocalDate.now(), null);
    }

    @Test(expected = NullPointerException.class)
    public void nullDateFromTest() throws ODataException {

        timeSheetRepository.getByDateAndUser(null, LocalDate.now(), "User");
    }


    @Test(expected = NullPointerException.class)
    public void nullDateTo() throws ODataException {

        timeSheetRepository.getByDateAndUser(LocalDate.now(), null, "User");
    }

    @Test(expected = NullPointerException.class)
    public void nullEntries() {
        timeSheetRepository.saveAll(null);
    }

    @Test(expected = NullPointerException.class)
    public void listOfNullEntries() {
        List<TimeSheetEntry> entries = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            entries.add(null);
        }
        timeSheetRepository.saveAll(entries);
    }


}
