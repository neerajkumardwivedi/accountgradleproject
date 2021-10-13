package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.repository.AccountsRepositoryInMemory;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

	@Autowired
	private AccountsService accountsService;

	@Autowired
	private AccountsRepositoryInMemory accountsRepositoryInMemoryObj;

	@Mock
	private AccountsRepository acctReposObj;

	private static Logger loggerTestObj = LoggerFactory.getLogger(AccountsServiceTest.class);

	@Test
	public void addAccount() throws Exception {
		Account account = new Account("Id-123");
		account.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(account);

		assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
	}

	@Test
	public void addAccount_failsOnDuplicateId() throws Exception {
		String uniqueId = "Id-" + System.currentTimeMillis();
		Account account = new Account(uniqueId);
		this.accountsService.createAccount(account);

		try {
			this.accountsService.createAccount(account);
			fail("Should have failed when adding duplicate account");
		} catch (DuplicateAccountIdException ex) {
			assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
		}

	}

	@Test()
	public void transfer_between_Accounts() {
		Map<String, Account> concurrentAccounts = new ConcurrentHashMap<>();

		String sourceAccountId = "id-123";
		String destAccountId = "id-456";
		
		Account srcAccount = new Account(sourceAccountId,new BigDecimal("100.25"));
		Account destinationAccount = new Account(destAccountId,new BigDecimal("200.234"));

		accountsRepositoryInMemoryObj.setAccount();
		loggerTestObj.info("Repository Map value " + accountsRepositoryInMemoryObj.getAccounts().toString());
		loggerTestObj.info("Repository Map value " + accountsRepositoryInMemoryObj.getAccounts().get(destAccountId));

		when(acctReposObj.getAccount(sourceAccountId)).thenReturn(srcAccount);
		when(acctReposObj.getAccount(destAccountId)).thenReturn(destinationAccount);

		/*Multiple thread executing transfer function*/
		Thread t = new Thread(new Runnable() {
			public void run() {
				for (int i = 1; i <= 3; i++) {

					Account tempAcc = accountsService.transferAmountBetweenAccounts(sourceAccountId, destAccountId,
							new BigDecimal("20.53"));
					concurrentAccounts.putIfAbsent(tempAcc.getAccountId(), tempAcc);

				}
			}
		});
		t.start();
		try {
			/*if threads are executing same time then interrupted exception is thrown*/
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//assert statement
		loggerTestObj.info(
				"Repository Map value " + accountsRepositoryInMemoryObj.getAccounts().get(destAccountId).getBalance());
		loggerTestObj.info("Actual calulated Map value " + concurrentAccounts.get(destAccountId).getBalance());

		assertThat(accountsRepositoryInMemoryObj.getAccounts().get(destAccountId).getBalance())
				.isEqualTo((new BigDecimal("261.824")));
		assertThat(accountsRepositoryInMemoryObj.getAccounts().get(destAccountId).getBalance())
				.isEqualTo(concurrentAccounts.get(destAccountId).getBalance());

	}
}
