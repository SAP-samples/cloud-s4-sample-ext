package com.sap.config;

public enum Status {

    IN_PROCESS("In process", "10"),
    RELEASED_FOR_APPROVAL("Released for approval", "20"),
    APPROVED("Approved", "30"),
    APPROVAL_REJECTED("Approval rejected", "40"),
    CHANGED_AFTER_APPROVAL("Changed after approval", "50"),
    CANCELLED("Cancelled", "60");


    private final String description;
    private final String code;

    Status(String description, String code) {
        this.description = description;
        this.code = code;
    }

    @Override
    public String toString() {
        return description;
    }

    public String getCode() {
        return code;
    }
}
