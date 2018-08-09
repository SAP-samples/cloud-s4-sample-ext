package com.sap.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.model.*;
import com.sap.repository.FilterRepository;
import com.sap.repository.WorkforceRepository;
import com.sap.security.AuthFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class WorkforceControllerTest {

    private static final String WORKFORCES = "/workforces";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String FILTERS = "filters/";
    private static final String CUSTOMER = "10100001";
    private static final String WORK_PACKAGE = "BMP21.1.1";
    private static final String PROJECT = "1805EC";
    @Autowired
    private FilterRepository filterRepository;
    @Autowired
    private WorkforceRepository workforceRepository;
    @Autowired
    private AuthFacade authFacade;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        when(authFacade.getLoggedUser())
                .thenReturn(SapUser.create("guest", "guest"));

        when(filterRepository.getCustomerSet(anyString()))
                .thenReturn(workforceRepository.getAll()
                        .stream()
                        .map(wf -> Customer.create(wf.getCustomer(), wf.getCustomerFullName()))
                        .filter(c -> !c.getCustomerId().isEmpty())
                        .collect(Collectors.toSet()));

        when(filterRepository.getProjectSet(anyString()))
                .thenReturn(workforceRepository.getAll()
                        .stream()
                        .map(wf -> Project.create(wf.getEngagementProject(), wf.getEngagementProjectName()))
                        .filter(p -> !p.getProjectId().isEmpty())
                        .collect(Collectors.toSet()));

        when(filterRepository.getWorkPackageSet(anyString()))
                .thenReturn(workforceRepository.getAll()
                        .stream()
                        .map(wf -> WorkPackage.create(wf.getWorkPackage(), wf.getWorkPackageName()))
                        .filter(wp -> !wp.getWorkPackage().isEmpty())
                        .collect(Collectors.toSet()));

    }

    @Test
    @DisplayName("AllWorkforces")
    void getAllWorkforceData() throws Exception {

        final String response = mockMvc.perform(get(WORKFORCES + "/all"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final List<Workforce> workforces = Arrays.asList(getResponse(response, Workforce[].class));

        assertTrue(!workforces.isEmpty());

        workforces.stream()
                .map(Workforce::getId)
                .forEach(id -> assertTrue(!id.isEmpty()));


    }

    @Test
    @DisplayName("DateRangeFilter")
    void getFilteredDataByDateRange() throws Exception {
        final String response = mockMvc.perform(get(WORKFORCES + "?from=2018-04-27&to=2018-04-30"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        final List<Workforce> workforces = Arrays.asList(getResponse(response, Workforce[].class));

        assertEquals(6, workforces.size());
        workforces.stream().map(Workforce::getCalendarDate)
                .forEach(date -> assertTrue((date.isAfter(LocalDate.parse("2018-04-27")) || (date.isEqual(LocalDate.parse("2018-04-27"))
                        && (date.isBefore(LocalDate.parse("2018-04-30"))) || date.isEqual(LocalDate.parse("2018-04-30"))))));
    }

    @Test
    @DisplayName("ProjectFilter")
    void getProjects() throws Exception {

        final String response = mockMvc.perform(get(WORKFORCES + "/" + FILTERS + "projects"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        final List<String> projects = Arrays.asList(getResponse(response, String[].class));


        assertAll("Get of projects should return 4 elements.",
                () -> {
                    assertEquals(4, projects.size());

                    assertTrue(projects.contains("Professional_Services_125"));
                    assertTrue(projects.contains("1805EC"));
                    assertTrue(projects.contains("bmp21"));
                    assertTrue(projects.contains("bmp5"));

                });

    }

    @Test
    @DisplayName("CustomersFilter")
    void getCustomers() throws Exception {

        final String response = mockMvc.perform(get(WORKFORCES + "/" + FILTERS + "customers"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        final List<Customer> customers = Arrays.asList(getResponse(response, Customer[].class));
        assertAll("Getting projects should return one value.", () -> {
            assertEquals(1, customers.size());

            final Customer customer = customers.get(0);
            assertEquals("Customer2", customer.getCustomerFullName());
            assertEquals("10100001", customer.getCustomerId());
        });


    }

    @Test
    @DisplayName("WorkPackageFilter")
    void getWorkPackage() throws Exception {

        final String response = mockMvc.perform(get(WORKFORCES + "/" + FILTERS + "workPackages"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        final List<WorkPackage> workPackages = Arrays.asList(getResponse(response, WorkPackage[].class));

        assertEquals(6, workPackages.size());
    }

    @Test
    @DisplayName("GetUniqueWorkforce")
    void getById() throws Exception {
        final String response = mockMvc.perform(get(WORKFORCES + "/"
                + ".1~50000238.2~50000238.3~9980000186.4~BMP5.5~0010100001.6~50000238.7~000000000012.8~9980000186.9~FA163E32DCA21EE88AA2239F8546B18F.10~BMP5&as1&as2.11~"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        final Workforce workforce = mapper.readValue(response, Workforce.class);

        assertAll("Getting by ID returns one value and fields are valid.",
                () ->
                {
                    assertEquals("wp2", workforce.getWorkPackageName());
                    assertEquals("BMP5.1.2", workforce.getWorkPackage());
                    assertEquals("bmp5", workforce.getEngagementProjectName());
                    assertEquals("BMP5", workforce.getEngagementProject());
                    assertEquals("10100001", workforce.getCustomer());
                    assertEquals("Customer2", workforce.getCustomerFullName());
                    assertEquals("PROJ_MANAGE_COMM", workforce.getPersonWorkAgreementExternalID());
                    assertEquals("50000238", workforce.getPersonWorkAgreement());
                });

    }

    @Test
    @DisplayName("FilterForWorkPackage")
    void filterWorkPackage() throws Exception {
        final String response = mockMvc.perform(get(WORKFORCES + "?from=2018-04-27&to=2018-04-30&workPackage=BMP21.1.1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        final List<Workforce> filteredData = Arrays.asList(getResponse(response, Workforce[].class));

        assertAll(() -> {
            assertEquals(6, filteredData.size());
            filteredData.forEach(wf -> assertEquals("BMP21.1.1", wf.getWorkPackage()));
        });
    }

    @Test
    @DisplayName("InvalidDateGivesBadRequest")
    void validateDate() throws Exception {
        mockMvc.perform(get(WORKFORCES + "?from=2018-04-25&to=2018-04-20"))
                .andExpect(status().isBadRequest());
    }

    private <T> T getResponse(String response, Class<T> classz) throws IOException {
        return mapper.readValue(response, classz);
    }

    @Test
    @DisplayName("FilterForCustomer")
    void filterCustomer() throws Exception {
        final String response = mockMvc.perform(get(WORKFORCES + "?from=2018-03-27&to=2018-04-30&customer=10100001"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        final List<Workforce> filteredData = Arrays.asList(getResponse(response, Workforce[].class));

        assertAll(() -> {
            assertEquals(13, filteredData.size());
            filteredData.forEach(wf -> assertEquals("10100001", wf.getCustomer()));
        });

    }

    @Test
    @DisplayName("FilterForCustomerAndWorkPackage")
    void filterCustomerAndWorkPackage() throws Exception {
        final String response = mockMvc.perform(get(WORKFORCES
                + "?from=2018-03-27&to=2018-04-30&customer=" + CUSTOMER + "&workPackage=" + WORK_PACKAGE))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        final List<Workforce> filteredData = Arrays.asList(getResponse(response, Workforce[].class));

        assertAll(() ->
        {
            assertEquals(8, filteredData.size());

            filteredData.forEach(wf -> {
                assertEquals(CUSTOMER, wf.getCustomer());
                assertEquals(WORK_PACKAGE, wf.getWorkPackage());
            });
        });
    }

    @Test
    @DisplayName("FilterForCustomerAndProject")
    void filterCustomerAndProject() throws Exception {
        final String response = mockMvc.perform(get(WORKFORCES
                + "?from=2018-03-27&to=2018-04-30&customer=" + CUSTOMER + "&project=" + PROJECT))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        final List<Workforce> filteredData = Arrays.asList(getResponse(response, Workforce[].class));

        assertAll(() ->
        {
            assertEquals(1, filteredData.size());

            final Workforce wf = filteredData.get(0);
            assertEquals(CUSTOMER, wf.getCustomer());
            assertEquals(PROJECT, wf.getEngagementProject());
        });
    }


    @Test
    @DisplayName("FilterForProjectAndWorkPackage")
    void filterProjectAndWorkPackage() throws Exception {
        final String response = mockMvc.perform(get(WORKFORCES
                + "?from=2018-03-27&to=2018-04-30&project=" + PROJECT + "&workPackage=" + "1805EC.1.1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        final List<Workforce> filteredData = Arrays.asList(getResponse(response, Workforce[].class));

        assertAll(() ->
        {
            assertEquals(1, filteredData.size());

            final Workforce wf = filteredData.get(0);
            assertEquals(PROJECT, wf.getEngagementProject());
            assertEquals("1805EC.1.1", wf.getWorkPackage());
        });
    }

    @Test
    @DisplayName("FilterForProjectAndWorkPackageAndCustomer")
    void filterProjectAndWorkPackageAndCustomer() throws Exception {
        final String response = mockMvc.perform(get(WORKFORCES
                + "?from=2018-03-27&to=2018-04-30&project=" + "BMP21" + "&workPackage=" + WORK_PACKAGE
                + "&customer=" + CUSTOMER))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        final List<Workforce> filteredData = Arrays.asList(getResponse(response, Workforce[].class));

        assertAll(() ->
        {
            assertEquals(8, filteredData.size());

            final Workforce wf = filteredData.get(0);
            assertEquals("BMP21", wf.getEngagementProject());
            assertEquals(WORK_PACKAGE, wf.getWorkPackage());
            assertEquals(CUSTOMER, wf.getCustomer());
        });
    }


}