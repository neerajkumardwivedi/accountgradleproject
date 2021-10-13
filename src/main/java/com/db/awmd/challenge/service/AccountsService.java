package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.AccountNotFoundException;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.repository.AccountsRepositoryInMemory;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountsService {

	@Autowired
	private final AccountsRepository accountsRepository;

//@Autowired
//private EmailNotificationService emailNotificationService;

	@Autowired
	private AccountsRepositoryInMemory accountsInmemoryObj;

	private static Logger logger = LoggerFactory.getLogger(AccountsService.class);

	public AccountsRepository getAccountsRepository() {
		return accountsRepository;
	}

	@Autowired
	public AccountsService(AccountsRepository accountsRepository) {
		this.accountsRepository = accountsRepository;
	}

	public void createAccount(Account account) {
		this.accountsRepository.createAccount(account);
	}

	public Account getAccount(String accountId) {
		return this.accountsRepository.getAccount(accountId);
	}

	/*
	 * @Description: Transferring the amount between source and transaction
	 * account.It involves withdrawal from source account and deposit in target
	 * account "acctNumber":"345443543","email":"a@gmail.com","balance":"400"
	 */

	public Account transferAmountBetweenAccounts(String sourceTxnId, String destTxnId, BigDecimal transferAmount) {

		/*
		 * Synchronized block so that only one thread can execute the operation of
		 * withdrawing the amount from source account and depositing in destination
		 * account is transferring process
		 */
		// accountsRepository.setAccount();
		Account sourceAccount = accountsRepository.getAccount(sourceTxnId);
		if (sourceAccount == null) {
			throw new AccountNotFoundException("Source Account Not found");
		}
		Account destinationAccount = accountsRepository.getAccount(destTxnId);
		if (destinationAccount == null) {
			throw new AccountNotFoundException("Destination Account Not found");
		}

		synchronized (this) {

			logger.info("Inside synchronized block");
			try {

				logger.info("Before transfer "
						+ accountsInmemoryObj.getAccounts().get(destTxnId).getBalance().doubleValue());
				sourceAccount.withdraw(transferAmount, sourceAccount);
				accountsRepository.updateAccount(sourceAccount);

				destinationAccount.deposit(transferAmount, destinationAccount);
				;
				accountsRepository.updateAccount(destinationAccount);

				logger.info("After transfer "
						+ accountsInmemoryObj.getAccounts().get(destTxnId).getBalance().doubleValue());

				// emailNotificationService.notifyAboutTransfer(destinationAccount, "Transferred
				// amount is" + transferAmount);
				logger.info("email sent to destination account");
			}

			catch (Exception e) {
				logger.error("Exception e {} ", e.getMessage());
			}
		}
		return destinationAccount;

	}
}
