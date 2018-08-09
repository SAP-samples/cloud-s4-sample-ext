package com.sap.model;

import lombok.Data;

@Data
public class WorkPackage {
    private final String workPackage;
    private final String workPackageName;

    private WorkPackage() {
        this.workPackage = "";
        this.workPackageName = "";
    }

    private WorkPackage(String workPackage, String workPackageName) {
        this.workPackage = workPackage;
        this.workPackageName = workPackageName;
    }

    public static WorkPackage create(String workPackage, String workPackageName) {
        return new WorkPackage(workPackage, workPackageName);
    }
}
