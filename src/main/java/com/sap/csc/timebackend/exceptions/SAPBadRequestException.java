package com.sap.csc.timebackend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serializable;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SAPBadRequestException extends SAPException implements Serializable {

    private static final long serialVersionUID = 722976352128009726L;

    public SAPBadRequestException(String exception) {
        super(exception);
    }

}
