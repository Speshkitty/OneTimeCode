package com.mctoybox.onetimecode.exceptions;

public class CommandNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public CommandNotFoundException() {
		super();
	}
	
	public CommandNotFoundException(String message) {
		super(message);
	}
	
	public CommandNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CommandNotFoundException(Throwable cause) {
		super(cause);
	}
	
}
