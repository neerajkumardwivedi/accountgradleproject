package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.repository.AccountsRepositoryInMemory;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsRepositoryInMemoryTest {

	@Autowired
	private AccountsRepositoryInMemory acctReposObjInMemory;
	
	@Before
	public void setUp() throws Exception {
		
		acctReposObjInMemory.setDemoAccount();
	}

	@Test
	public void testCreate_Account() {
		//acctReposObjInMemory.setAccount();
		Account srcAccount = new Account("id-459",new BigDecimal("100.25"));

		acctReposObjInMemory.createAccount(srcAccount);
		assertNotNull(acctReposObjInMemory.getAccounts().get(srcAccount.getAccountId()));
	}
	
	@Test
	public void testCreate_failsOnDuplicateAccount() {
		String sourceAccountId = "id-123";
		Account srcAccount = new Account(sourceAccountId,new BigDecimal("100.25"));
	
		try {
			acctReposObjInMemory.createAccount(srcAccount);
			fail("Should have failed when adding duplicate account");
		} catch (DuplicateAccountIdException ex) {
			assertThat(ex.getMessage()).isEqualTo("Account id " + sourceAccountId + " already exists!");
		}
		
	}

	@Test
	public void fetch_AccountId() {
		String sourceAccountId = "id-123";
		
		Account accountObj = acctReposObjInMemory.getAccount(sourceAccountId);;
		assertNotNull(accountObj);
	}

	@Test
	public void update_AccountBalance() {
		String sourceAccountId = "id-123";
		Account srcAccount = new Account(sourceAccountId,new BigDecimal("90.25"));
		acctReposObjInMemory.updateAccount(srcAccount);
	    assertThat(acctReposObjInMemory.getAccounts().get(sourceAccountId).getBalance()).isEqualTo(new BigDecimal("90.25"));
	}

}
