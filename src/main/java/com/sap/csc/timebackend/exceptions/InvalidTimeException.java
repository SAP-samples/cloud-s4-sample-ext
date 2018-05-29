package com.sap.csc.timebackend.exceptions;


public class InvalidTimeException extends SAPBadRequestException {
    private static final long serialVersionUID = -2925465480625793152L;

    private InvalidTimeException(String exception) {
        super(exception);
    }

    public static InvalidTimeException create(String exception) {
        return new InvalidTimeException(exception);
    }
}
