package com.acme.intreportapp.exception;

/**
 * This is the custom exception class to catch all the service layer exceptions
 * 
 * @author SAP
 * 
 */
public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}

}
