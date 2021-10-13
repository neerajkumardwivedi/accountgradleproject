package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

	private final Map<String, Account> accounts = new ConcurrentHashMap<>();

	@Override
	public void createAccount(Account account) throws DuplicateAccountIdException {
		Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
		if (previousAccount != null) {
			throw new DuplicateAccountIdException("Account id " + account.getAccountId() + " already exists!");
		}
	}

	@Override
	public Account getAccount(String accountId) {
		return accounts.get(accountId);
	}

	@Override
	public void clearAccounts() {
		accounts.clear();
	}

	public Map<String, Account> getAccounts() {
		return accounts;
	}

	@Override
	public void updateAccount(Account acct) {
		if (accounts.containsKey(acct.getAccountId())) {
			accounts.replace(acct.getAccountId(), acct);
		}
	}

	@Override
	public Map<String, Account> setAccount() {
		Account acct1 = new Account("id-123");
		acct1.setBalance(new BigDecimal("100.25"));
		Account acct2 = new Account("id-456");
		acct2.setBalance(new BigDecimal("200.234"));

		Account acct3 = new Account("id-789");
		acct3.setBalance(new BigDecimal("1001.0000"));
		Account acct4 = new Account("id-0001");
		acct4.setBalance(new BigDecimal("1001.0000"));

		accounts.put(acct1.getAccountId(), acct1);
		accounts.put(acct2.getAccountId(), acct2);
		accounts.put(acct3.getAccountId(), acct3);
		accounts.put(acct4.getAccountId(), acct4);

		return accounts;
	}

}
