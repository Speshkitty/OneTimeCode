package com.mctoybox.onetimecode.exceptions;

public class NotOTCBookException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public NotOTCBookException() {
		super();
	}
	
	public NotOTCBookException(String message) {
		super(message);
	}
	
	public NotOTCBookException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public NotOTCBookException(Throwable cause) {
		super(cause);
	}
	
}
