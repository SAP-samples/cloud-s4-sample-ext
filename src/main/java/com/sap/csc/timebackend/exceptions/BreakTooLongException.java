package com.sap.csc.timebackend.exceptions;

public class BreakTooLongException extends SAPBadRequestException {
    private static final long serialVersionUID = -8157628382436520674L;

    private BreakTooLongException(String exception) {
        super(exception);
    }

    public static BreakTooLongException create(String exception) {
        return new BreakTooLongException(exception);
    }

}
