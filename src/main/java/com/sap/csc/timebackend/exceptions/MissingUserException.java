package com.sap.csc.timebackend.exceptions;

public class MissingUserException extends SAPBadRequestException {
    private static final long serialVersionUID = 424117090423227375L;

    private MissingUserException(String exception) {
        super(exception);
    }

    public static MissingUserException create(String exception) {
        return new MissingUserException(exception);
    }

}
