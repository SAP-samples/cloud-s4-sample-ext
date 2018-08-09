package com.sap.services;

import com.sap.model.SapUser;
import com.sap.model.Workforce;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface WorkforceService {

    Workforce getById(String id);

    List<Workforce> getWorkforceData();

    List<Workforce> getByPersonWorkAgreementExternalId(String id);

    List<Workforce> getByProjectAndCustomer(String project, String customer, LocalDate from, LocalDate to, SapUser user);

    List<Workforce> getByCustomer(String customer, LocalDate from, LocalDate to, SapUser user);

    List<Workforce> getByProject(String project, LocalDate from, LocalDate to, SapUser user);

    List<Workforce> getWorkforceData(LocalDate from, LocalDate to, SapUser user);

    List<Workforce> getByProjectAndCustomerAndWP(String project, String customer, String workPackage, LocalDate from, LocalDate to, SapUser user);

    List<Workforce> getByCustomerAndWorkPackage(String customer, String workPackage, LocalDate from, LocalDate to, SapUser user);

    List<Workforce> getByProjectAndWorkPackage(String project, String workPackage, LocalDate from, LocalDate to, SapUser user);

    List<Workforce> getByWorkPackage(String workPackage, LocalDate from, LocalDate to, SapUser user);
}
