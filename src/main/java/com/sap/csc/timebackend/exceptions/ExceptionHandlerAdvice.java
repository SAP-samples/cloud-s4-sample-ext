package com.sap.csc.timebackend.exceptions;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(SAPBadRequestException.class)
    public ResponseEntity<CustomError> handleBadRequest(SAPException ex, HttpServletRequest request) {
        final HttpStatus status = getStatus(ex).code();
        final CustomError error = new CustomError(status, ex.getException(), request);
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(SAPException.class)
    public ResponseEntity<CustomError> handleSapException(SAPException ex, HttpServletRequest request) {
        final HttpStatus status = getStatus(ex).code();
        final CustomError error = new CustomError(status, ex.getException(), request);
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CustomError> handleGeneralException(RuntimeException ex, HttpServletRequest request) {
        final CustomError error = new CustomError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
        ex.printStackTrace();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private ResponseStatus getStatus(Exception ex) {
        return AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
    }

}