package com.db.awmd.challenge.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.db.awmd.challenge.domain.ErrorMessage;


@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	/*
	 * Exception thrown in format of http status and message whenever
	 * AccountNotException is thrown
	 */
	@ExceptionHandler(AccountNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ErrorMessage> acctNotFoundException(AccountNotFoundException accException) {

		ErrorMessage errMessage = new ErrorMessage(HttpStatus.NOT_FOUND.value(), accException.getMessage());

		// return new ResponseEntity.status(HttpStatus.NOT_FOUND).body(errMessage);

		return new ResponseEntity<ErrorMessage>(errMessage, HttpStatus.NOT_FOUND);
	}
}
