package com.sap.services;

import com.sap.model.SapUser;
import com.sap.model.Workforce;
import com.sap.repository.WorkforceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class WorkforceServiceImpl implements WorkforceService {
    private final WorkforceRepository workforceRepository;

    @Autowired
    public WorkforceServiceImpl(WorkforceRepository workforceRepository) {
        this.workforceRepository = workforceRepository;
    }

    @Override
    public List<Workforce> getWorkforceData() {
        return workforceRepository.getAll();
    }

    @Override
    public List<Workforce> getByPersonWorkAgreementExternalId(String id) {
        return workforceRepository.filterByPersonWorkAgreementExternalId(id);
    }

    @Override
    public List<Workforce> getByProjectAndCustomer(String project, String customer, LocalDate from, LocalDate to, SapUser user) {
        return workforceRepository.filterByProjectAndCustomerInRange(project, customer, from, to, user);
    }

    @Override
    public List<Workforce> getByCustomer(String customer, LocalDate from, LocalDate to, SapUser user) {
        return workforceRepository.filterByCustomerInRange(customer, from, to, user);
    }

    @Override
    public List<Workforce> getByProject(String project, LocalDate from, LocalDate to, SapUser user) {
        return workforceRepository.filterByProjectInRange(project, from, to, user);
    }

    @Override
    public List<Workforce> getWorkforceData(LocalDate from, LocalDate to, SapUser user) {
        return workforceRepository.filterInRange(from, to, user);
    }

    @Override
    public Workforce getById(String id) {
        return workforceRepository.findById(id);
    }

    @Override
    public List<Workforce> getByProjectAndCustomerAndWP(String project, String customer, String workPackage, LocalDate from, LocalDate to, SapUser user) {
        return workforceRepository.filterByProjectAndCustomerAndWPInRange(project, customer, workPackage, from, to, user);
    }

    @Override
    public List<Workforce> getByCustomerAndWorkPackage(String customer, String workPackage, LocalDate from, LocalDate to, SapUser user) {
        return workforceRepository.filterCustomerAndWPInRange(customer, workPackage, from, to, user);
    }

    @Override
    public List<Workforce> getByProjectAndWorkPackage(String project, String workPackage, LocalDate from, LocalDate to, SapUser user) {
        return workforceRepository.filterByProjectAndWPInRange(project, workPackage, from, to, user);
    }

    @Override
    public List<Workforce> getByWorkPackage(String workPackage, LocalDate from, LocalDate to, SapUser user) {
        return workforceRepository.filterByWPInRange(workPackage, from, to, user);
    }
}
