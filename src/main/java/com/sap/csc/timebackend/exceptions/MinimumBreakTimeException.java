package com.sap.csc.timebackend.exceptions;

public class MinimumBreakTimeException extends SAPBadRequestException {
    private static final long serialVersionUID = -490812626044789745L;

    private MinimumBreakTimeException(String exception) {
        super(exception);
    }

    public static MinimumBreakTimeException create(String exception) {
        return new MinimumBreakTimeException(exception);
    }
}
