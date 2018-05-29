package com.sap.csc.timebackend.exceptions;

public class TotalTravelTimeTooLongException extends SAPBadRequestException {
    public TotalTravelTimeTooLongException(String exception) {
        super(exception);
    }

    @Override
    public String toString() {
        return "TotalTravelTimeTooLongException{}" + super.getException();
    }
}
