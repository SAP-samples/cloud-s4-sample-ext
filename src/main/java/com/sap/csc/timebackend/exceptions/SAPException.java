package com.sap.csc.timebackend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class SAPException extends RuntimeException {

    private static final long serialVersionUID = -8750863341350036501L;

    private final String exception;
    private CustomError error;

    SAPException(String exception) {
        this.exception = exception;
    }

    public static SAPException create(String exception) {
        return new SAPException(exception);
    }

    public CustomError getError() {
        return error;
    }

    public String getException() {
        return exception;
    }

}