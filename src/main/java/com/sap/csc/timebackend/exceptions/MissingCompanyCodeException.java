package com.sap.csc.timebackend.exceptions;

public class MissingCompanyCodeException extends SAPBadRequestException {
    private static final long serialVersionUID = 3010832395543178542L;

    private MissingCompanyCodeException(String exception) {
        super(exception);
    }

    public static MissingCompanyCodeException create(String exception) {
        return new MissingCompanyCodeException(exception);
    }

}
