package com.sap.repository;

import com.sap.model.Customer;
import com.sap.model.Project;
import com.sap.model.WorkPackage;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface FilterRepository {

    Set<Project> getProjectSet(String personWorkAgreement);

    Set<Customer> getCustomerSet(String personWorkAgreement);

    Set<WorkPackage> getWorkPackageSet(String personWorkAgreement);


}
