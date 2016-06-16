package com.gvan.exception;
public class InvalidVersionException extends VersionInformationException {
	String message;
	public InvalidVersionException(String message ) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
}
