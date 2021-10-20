package com.db.awmd.challenge.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.AccountDoesNotExistException;
import com.db.awmd.challenge.exception.InvalidAmountException;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.repository.AccountsRepositoryInMemory;

import io.vertx.core.json.JsonObject;

@Service
public class AccountTransferService {

	@Autowired
	private AccountsRepository accountsRepository;

	@Autowired
	private EmailNotificationService emailNotificationService;

	@Autowired
	private AccountsRepositoryInMemory accountsInmemoryObj;

	private static Logger logger = LoggerFactory.getLogger(AccountTransferService.class);

	/*
	 * @Description: Transferring the amount between source and transaction
	 * account.It involves withdrawal from source account and deposit in target
	 * account "acctNumber":"345443543","email":"a@gmail.com","balance":"400"
	 */

	public JsonObject transferAmountBetweenAccounts(String sourceTxnId, String targetTxnId, BigDecimal transferAmount) {

		JsonObject resultSet = new JsonObject();
		resultSet.put("isSuccess", false);
		/*
		 * Synchronized block so that only one thread can execute the operation of
		 * withdrawing the amount from source account and depositing in destination
		 * account is transferring process
		 * 
		 */

		Account sourceAccount = accountsRepository.getAccount(sourceTxnId);
		if (sourceAccount == null) {

			throw new AccountDoesNotExistException("Source Account Not found");
		}
		Account destinationAccount = accountsRepository.getAccount(targetTxnId);
		if (destinationAccount == null) {
			throw new AccountDoesNotExistException("Destination Account Not found");

		}

		synchronized (this) {

			logger.info("Inside synchronized block");
			try {

				logger.info("Before transfer "
						+ accountsInmemoryObj.getAccounts().get(targetTxnId).getBalance().doubleValue());
				withdraw(transferAmount, sourceAccount);
				accountsRepository.updateAccount(sourceAccount);

				deposit(transferAmount, destinationAccount);

				accountsRepository.updateAccount(destinationAccount);

				logger.info("After transfer "
						+ accountsInmemoryObj.getAccounts().get(targetTxnId).getBalance().doubleValue());

				emailNotificationService.notifyAboutTransfer(destinationAccount,
						"Amount : " + transferAmount + " credited into account " + destinationAccount.getAccountId());
				logger.info("email sent to destination account");
				emailNotificationService.notifyAboutTransfer(sourceAccount,
						"Amount :" + transferAmount + " debited from account " + sourceAccount.getAccountId());
				logger.info("email sent to source account");

				resultSet.put("isSuccess", true);

			}

			catch (InvalidAmountException e) {
				logger.error("Exception e {} ", e.getMessage());
				throw new InvalidAmountException(e.getMessage());
			}
		}
		return resultSet;

	}

	/* @Parameter:amount= Withdrawing the amount from account */
	public synchronized BigDecimal withdraw(BigDecimal amount, Account acct) {
		if (amount.compareTo(BigDecimal.ZERO) < 0) // withdraw value is negative
		{
			throw new InvalidAmountException("Error: Withdraw amount is invalid.");

		} else if (amount.compareTo(acct.getBalance()) > 0) {
			throw new InvalidAmountException("Error: Insufficient funds.");
		} else {
			logger.info("Before withdraw " + acct.getBalance().doubleValue());
			acct.setBalance(acct.getBalance().subtract(amount));

		}
		logger.info("After withdraw " + acct.getBalance().doubleValue());
		return acct.getBalance();
	}

	/*-----------------------------------------------------------------
	Validates the transaction, then deposits the specified amount
	 into the account. Returns the new balance.
	----------------------------------------------------------------
	@Parameter:amount= depositing the amount in account*/
	public synchronized BigDecimal deposit(BigDecimal amount, Account acct) {

		if (amount.compareTo(BigDecimal.ZERO) < 0) // deposit value is negative
		{
			throw new InvalidAmountException("Error: Deposit amount is invalid.");

		} else {
			logger.info("Before deposit " + acct.getBalance().doubleValue());
			acct.setBalance(acct.getBalance().add(amount));
		}
		logger.info("After deposit balance " + acct.getBalance().doubleValue());
		return acct.getBalance();
	}

}
