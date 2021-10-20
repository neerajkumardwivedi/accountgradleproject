package com.db.awmd.challenge.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorMessage {

	/* Error code for throwing error */
	private int errorCode;
	/* Message of throwing error */
	private String message;

	/* Parameterized constructor with status and message */
	public ErrorMessage(int errorCode, String message) {
		super();
		this.errorCode = errorCode;
		this.message = message;
	}

}
