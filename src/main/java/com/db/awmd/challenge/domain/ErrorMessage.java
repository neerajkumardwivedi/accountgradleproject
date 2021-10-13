package com.db.awmd.challenge.domain;

public class ErrorMessage {

	/* Error code for throwing error */
	private int errorCode;
	/* Message of throwing error */
	private String message;

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/* Parameterized constructor with status and message */
	public ErrorMessage(int errorCode, String message) {
		super();
		this.errorCode = errorCode;
		this.message = message;
	}

}
