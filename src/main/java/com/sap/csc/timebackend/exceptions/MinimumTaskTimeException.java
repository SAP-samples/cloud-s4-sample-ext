package com.sap.csc.timebackend.exceptions;

public class MinimumTaskTimeException extends SAPBadRequestException {
    private static final long serialVersionUID = -4701648036834245364L;

    private MinimumTaskTimeException(String exception) {
        super(exception);
    }

    public static MinimumTaskTimeException create(String exception) {
        return new MinimumTaskTimeException(exception);
    }
}
