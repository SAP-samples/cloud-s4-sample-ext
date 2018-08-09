package com.sap.repository;

import com.sap.model.SapUser;
import com.sap.model.Workforce;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkforceRepository {
    List<Workforce> getAll();

    List<Workforce> filterByPersonWorkAgreementExternalId(String id);

    List<Workforce> filterByProjectAndCustomerInRange(String project, String customer, LocalDate from, LocalDate to, SapUser user);

    List<Workforce> filterByCustomerInRange(String customer, LocalDate from, LocalDate to, SapUser user);

    List<Workforce> filterByProjectInRange(String project, LocalDate from, LocalDate to, SapUser user);

    List<Workforce> filterInRange(LocalDate from, LocalDate to, SapUser user);

    Workforce findById(String id);

    List<Workforce> filterByProjectAndCustomerAndWPInRange(String project, String customer, String workPackage, LocalDate from, LocalDate to, SapUser user);

    List<Workforce> filterCustomerAndWPInRange(String customer, String workPackage, LocalDate from, LocalDate to, SapUser user);

    List<Workforce> filterByProjectAndWPInRange(String project, String workPackage, LocalDate from, LocalDate to, SapUser user);

    List<Workforce> filterByWPInRange(String workPackage, LocalDate from, LocalDate to, SapUser user);
}

