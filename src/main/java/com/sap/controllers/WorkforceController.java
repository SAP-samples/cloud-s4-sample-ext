package com.sap.controllers;

import com.sap.exceptions.SAPBadRequestException;
import com.sap.model.Customer;
import com.sap.model.Project;
import com.sap.model.WorkPackage;
import com.sap.model.Workforce;
import com.sap.repository.FilterRepository;
import com.sap.security.AuthFacade;
import com.sap.services.WorkforceService;
import com.sap.utils.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/workforces")
public class WorkforceController {

    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static final Logger log = LoggerFactory.getLogger(WorkforceController.class);
    private final WorkforceService workforceService;
    private final FilterRepository filterRepository;
    private final AuthFacade authFacade;

    @Autowired
    public WorkforceController(WorkforceService workforceService, FilterRepository filterRepository, AuthFacade authFacade) {
        this.workforceService = workforceService;
        this.filterRepository = filterRepository;
        this.authFacade = authFacade;
    }


    @GetMapping
    public List<Workforce> getWorkforceData(@RequestParam(required = false) String customer,
                                            @RequestParam(required = false) String project,
                                            @RequestParam @DateTimeFormat(pattern = YYYY_MM_DD) LocalDate from,
                                            @RequestParam @DateTimeFormat(pattern = YYYY_MM_DD) LocalDate to,
                                            @RequestParam(required = false) String workPackage) {

        validateDates(from, to);

        log.info("Parameters -> " + "customer: " + customer + ", project: " + project
                + ", workPackage -> " + workPackage);
        if (isPresent(project) && isPresent(customer) && isPresent(workPackage))
            return sortByCalendarDate(workforceService
                    .getByProjectAndCustomerAndWP(project,
                            customer,
                            workPackage,
                            from,
                            to,
                            authFacade.getLoggedUser()));

        if (isPresent(project) && isPresent(customer)) {
            return sortByCalendarDate(workforceService.getByProjectAndCustomer(project, customer, from, to,
                    authFacade.getLoggedUser()));
        }

        if (isPresent(customer) && isPresent(workPackage)) {
            return sortByCalendarDate(workforceService.getByCustomerAndWorkPackage(customer, workPackage, from, to,
                    authFacade.getLoggedUser()));
        }

        if (isPresent(project) && isPresent(workPackage)) {
            return sortByCalendarDate(workforceService.getByProjectAndWorkPackage(project, workPackage, from, to,
                    authFacade.getLoggedUser()));
        }

        if (isPresent(customer))
            return sortByCalendarDate(workforceService.getByCustomer(customer, from, to,
                    authFacade.getLoggedUser()));


        if (isPresent(project))
            return sortByCalendarDate(workforceService.getByProject(project, from, to,
                    authFacade.getLoggedUser()));

        if (isPresent(workPackage)) {
            return sortByCalendarDate(workforceService.getByWorkPackage(workPackage, from, to,
                    authFacade.getLoggedUser()));
        }


        return sortByCalendarDate(workforceService.getWorkforceData(from, to,
                authFacade.getLoggedUser()));

    }

    @GetMapping("/all")
    public List<Workforce> getAllWorkforceData() {
        return workforceService.getWorkforceData();
    }

    @GetMapping("filters/projects")
    public Set<String> getProjects() {
        return filterRepository.getProjectSet(authFacade.getLoggedUser().getUserId())
                .stream()
                .map(Project::getEngagementProjectName)
                .collect(Collectors.toSet());
    }

    @GetMapping("filters/customers")
    public Set<Customer> getCustomers() {
        return filterRepository.getCustomerSet(authFacade.getLoggedUser().getUserId());
    }

    @GetMapping("filters/workPackages")
    public Set<WorkPackage> getWorkPackages() {
        return filterRepository.getWorkPackageSet(authFacade.getLoggedUser().getUserId());
    }

    @GetMapping("/{id}")
    public Workforce getById(@PathVariable String id) {
        return workforceService.getById(id);
    }

    private void validateDates(LocalDate dateFrom, LocalDate dateTo) {

        ExceptionUtils.nullCheck(dateFrom, "Date from must not be null!");
        ExceptionUtils.nullCheck(dateTo, "Date to must not be null!");
        if (dateFrom.isAfter(dateTo)) {
            throw new SAPBadRequestException("Invalid Date! Date from must be lower than date to");
        }
    }

    private boolean isPresent(String string) {
        return Objects.nonNull(string) && (!string.isEmpty());
    }

    private List<Workforce> sortByCalendarDate(List<Workforce> workforces) {
        workforces.sort(Comparator.comparing(Workforce::getCalendarDate));
        return workforces;
    }

}

