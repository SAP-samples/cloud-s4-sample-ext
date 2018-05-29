package com.sap.csc.timebackend.exceptions;

public class MinimumTravelTimeException extends SAPBadRequestException {
    private static final long serialVersionUID = -8534165610096731681L;

    private MinimumTravelTimeException(String exception) {
        super(exception);
    }

    public static MinimumTravelTimeException create(String exception) {
        return new MinimumTravelTimeException(exception);
    }
}
