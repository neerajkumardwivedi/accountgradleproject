package com.db.awmd.challenge.repository;

import java.util.Map;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;

public interface AccountsRepository {

  void createAccount(Account account) throws DuplicateAccountIdException;

  Account getAccount(String accountId);

  void clearAccounts();

  Map<String, Account> setDemoAccount();

  void updateAccount(Account acct);

  
}
