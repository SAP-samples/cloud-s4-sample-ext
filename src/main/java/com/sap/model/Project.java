package com.sap.model;

import lombok.Data;

@Data
public class Project {
    private final String projectId;
    private final String engagementProjectName;

    private Project() {
        this.engagementProjectName = "";
        this.projectId = "";
    }

    private Project(String projectId, String engagementProjectName) {
        this.projectId = projectId;
        this.engagementProjectName = engagementProjectName;
    }


    public static Project create(String projectId, String engagementProjectName) {
        return new Project(projectId, engagementProjectName);
    }
}
