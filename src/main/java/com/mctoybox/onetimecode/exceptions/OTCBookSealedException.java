package com.mctoybox.onetimecode.exceptions;

public class OTCBookSealedException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public OTCBookSealedException() {
		super();
	}
	
	public OTCBookSealedException(String message) {
		super(message);
	}
	
	public OTCBookSealedException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public OTCBookSealedException(Throwable cause) {
		super(cause);
	}
	
}
