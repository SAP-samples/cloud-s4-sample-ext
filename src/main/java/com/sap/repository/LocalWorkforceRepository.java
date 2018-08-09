package com.sap.repository;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.sap.json.CustomerDeserializer;
import com.sap.json.ProjectDeserializer;
import com.sap.json.WorkPackageDeserializer;
import com.sap.json.WorkforceDeserializer;
import com.sap.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Profile({"dev", "test"})
@Component
public class LocalWorkforceRepository implements WorkforceRepository {

    private static final String MOCK_DATA_JSON = "mockData.json";
    private static final Logger log = LoggerFactory.getLogger(LocalWorkforceRepository.class);

    @Override
    public List<Workforce> getAll() {
        return readMockData();
    }

    @Override
    public List<Workforce> filterByPersonWorkAgreementExternalId(String id) {
        final Predicate<Workforce> byId = wf -> wf.getPersonWorkAgreementExternalID().equalsIgnoreCase(id);

        return readMockData().stream()
                .filter(byId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Workforce> filterByProjectAndCustomerInRange(String project, String customer, LocalDate from, LocalDate to, SapUser user) {
        return readMockData().stream()
                .filter(matchProject(project).and(matchCustomer(customer)).and(matchRange(from, to)))
                .collect(Collectors.toList());

    }

    @Override
    public List<Workforce> filterByCustomerInRange(String customer, LocalDate from, LocalDate to, SapUser user) {
        return readMockData().stream()
                .filter(matchCustomer(customer).and(matchRange(from, to)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Workforce> filterByProjectInRange(String project, LocalDate from, LocalDate to, SapUser user) {
        return readMockData().stream()
                .filter(matchProject(project).and(matchRange(from, to)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Workforce> filterInRange(LocalDate from, LocalDate to, SapUser user) {
        return readMockData().stream()
                .filter(matchRange(from, to))
                .collect(Collectors.toList());
    }

    @Override
    public Workforce findById(String id) {
        return readMockData().stream().filter(matchId(id)).findFirst().orElse(new Workforce());
    }

    @Override
    public List<Workforce> filterByProjectAndCustomerAndWPInRange(String project, String customer, String workPackage, LocalDate from, LocalDate to, SapUser user) {
        return readMockData().stream()
                .filter(matchRange(from, to).and(matchCustomer(customer))
                        .and(matchWorkPackage(workPackage))
                        .and(matchProject(project)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Workforce> filterCustomerAndWPInRange(String customer, String workPackage, LocalDate from, LocalDate to, SapUser user) {
        return readMockData().stream()
                .filter(matchRange(from, to).and(matchCustomer(customer))
                        .and(matchWorkPackage(workPackage)))
                .collect(Collectors.toList());

    }

    @Override
    public List<Workforce> filterByProjectAndWPInRange(String project, String workPackage, LocalDate from, LocalDate to, SapUser user) {
        return readMockData().stream()
                .filter(matchRange(from, to)
                        .and(matchWorkPackage(workPackage))
                        .and(matchProject(project)))
                .collect(Collectors.toList());

    }

    @Override
    public List<Workforce> filterByWPInRange(String workPackage, LocalDate from, LocalDate to, SapUser user) {
        return readMockData().stream()
                .filter(matchRange(from, to)
                        .and(matchWorkPackage(workPackage)))
                .collect(Collectors.toList());
    }


    private Predicate<Workforce> matchId(String id) {
        return wf -> wf.getId().equalsIgnoreCase(id);
    }

    private List<Workforce> readMockData() {
        try {
            return Arrays.asList(getMapper().readValue(new File(Objects.requireNonNull(getClass().getClassLoader().getResource(MOCK_DATA_JSON)).getFile()), Workforce[].class));
        } catch (IOException e) {
            log.error("Error reading from file " + MOCK_DATA_JSON);
            return Collections.emptyList();
        }
    }


    private Predicate<Workforce> matchWorkPackage(String workPackage) {
        return wf -> wf.getWorkPackage().equalsIgnoreCase(workPackage);
    }

    private Predicate<Workforce> matchCustomer(String customer) {
        return wf -> wf.getCustomer().equalsIgnoreCase(customer);
    }

    private Predicate<Workforce> matchProject(String project) {
        return wf -> wf.getEngagementProjectName().equalsIgnoreCase(project);
    }

    private Predicate<Workforce> matchRange(LocalDate from, LocalDate to) {
        return wf -> (wf.getCalendarDate().isAfter(from) || wf.getCalendarDate().isEqual(from))
                && (wf.getCalendarDate().isBefore(to) || wf.getCalendarDate().isEqual(to));
    }


    private ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("CustomModule", new Version(1, 0, 0, "", "", ""));
        module.addDeserializer(Workforce.class, new WorkforceDeserializer());
        module.addDeserializer(Project.class, new ProjectDeserializer());
        module.addDeserializer(Customer.class, new CustomerDeserializer());
        module.addDeserializer(WorkPackage.class, new WorkPackageDeserializer());
        mapper.registerModule(module);
        return mapper;
    }


}
