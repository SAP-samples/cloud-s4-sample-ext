package com.sap.csc.timebackend.exceptions;

import com.sap.csc.timebackend.helper.Helper;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Objects;

public class CustomError implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String status;
    private final String message;
    private final String url;

    CustomError(HttpStatus status, String message, HttpServletRequest request) {
        Helper.nullChecks(status, message, request);
        this.status = status.value() + " " + status.name();
        this.message = message;
        this.url = getUrl(request);
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getUrl() {
        return url;
    }

    private String getUrl(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();

        if (Objects.isNull(queryString)) {
            return requestURL.append(requestURL).toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }

    }

}
