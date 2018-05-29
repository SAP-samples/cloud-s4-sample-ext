package com.sap.csc.timebackend.exceptions;

public class MissingRecordNumberException extends SAPBadRequestException {
    private static final long serialVersionUID = 1415700759424917794L;

    private MissingRecordNumberException(String exception) {
        super(exception);
    }

    public static MissingRecordNumberException create(String exception) {
        return new MissingRecordNumberException(exception);
    }

}
