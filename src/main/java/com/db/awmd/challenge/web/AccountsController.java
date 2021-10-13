package com.db.awmd.challenge.web;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.AccountsService;

import java.math.BigDecimal;

import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

	private static Logger logger = LoggerFactory.getLogger(AccountsController.class);
	private final AccountsService accountsService;

	@Autowired
	public AccountsController(AccountsService accountsService) {
		this.accountsService = accountsService;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
		// log.info("Creating account {}", account);

		try {
			this.accountsService.createAccount(account);
		} catch (DuplicateAccountIdException daie) {
			return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping(path = "/{accountId}")
	public Account getAccount(@PathVariable String accountId) {
		// log.info("Retrieving account for id {}", accountId);
		return this.accountsService.getAccount(accountId);
	}

	/*
	 * @Description: Transferring amount from one accountid to other
	 * 
	 * @Parameter: Mandatory sourceaccountid,destinationAccountid and amount to be
	 * transferred
	 * e.g.localhost:8080/transfer?sourceAccountId=3&destAccountId=1&amount=400
	 */
	@PutMapping("/transfer")
	public ResponseEntity<Object> transferAmountBetweenAccounts(@RequestParam("sourceAccountId") String sourceAccountId,
			@RequestParam("destAccountId") String destAccountId, @RequestParam("amount") BigDecimal transferAmount)
			throws DuplicateAccountIdException {

		Account acct = null;
		if (transferAmount.compareTo(BigDecimal.ZERO) < 0) {
			logger.info("deposit value is negative");
			throw new ArithmeticException("Transfer amount should not be negative" + transferAmount);
		}
		acct = accountsService.transferAmountBetweenAccounts(sourceAccountId, destAccountId, transferAmount);
		if (acct == null) {
			logger.info("if account is null then internal server ");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		} else {
			return new ResponseEntity<>(
					"Amount " + transferAmount + " Transferred from " + sourceAccountId + " to " + destAccountId,
					HttpStatus.OK);
		}
	}
}
