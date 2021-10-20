package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.AccountDoesNotExistException;
import com.db.awmd.challenge.exception.InvalidAmountException;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.repository.AccountsRepositoryInMemory;
import com.db.awmd.challenge.service.AccountTransferService;

import io.vertx.core.json.JsonObject;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountTransferServiceTest {

	@Autowired
	private AccountTransferService accountsTransferService;

	@Autowired
	private AccountsRepositoryInMemory accountsRepositoryInMemoryObj;

	@Mock
	private AccountsRepository acctReposObj;

	private static Logger loggerTestObj = LoggerFactory.getLogger(AccountsServiceTest.class);

	@Before
	public void setUp() throws Exception {

		String sourceAccountId = "id-123";
		String destAccountId = "id-456";
		Account srcAccount = accountsRepositoryInMemoryObj.getAccount(sourceAccountId);
	    Account destinationAccount = accountsRepositoryInMemoryObj.getAccount(destAccountId);
	    
	    if(Objects.isNull(srcAccount)) {
	    	accountsRepositoryInMemoryObj.createAccount(new Account(sourceAccountId, new BigDecimal("1000.00")));
	    }
	    if(Objects.isNull(destinationAccount)) {	    	
	    	accountsRepositoryInMemoryObj.createAccount(new Account(destAccountId, new BigDecimal("2000.00")));
	    }

	}

	@Test
	public void transfer_between_AccountsConcurrentTest() {
		Map<String, Account> threadOneConcurrentMap = new ConcurrentHashMap<>();
		Map<String, Account> threadTwoConcurrentMap = new ConcurrentHashMap<>();
		
		String sourceAccountId = "id-123";
		String destAccountId = "id-456";

		when(acctReposObj.getAccount(sourceAccountId)).thenReturn(new Account(sourceAccountId, new BigDecimal("1000.00")));
		when(acctReposObj.getAccount(destAccountId)).thenReturn(new Account(destAccountId, new BigDecimal("2000.00")));

		/* Multiple thread executing transfer function */
		Thread t1 = new Thread(new Runnable() {
			public void run() {
				for (int i = 1; i <= 100; i++) {

					JsonObject resultObj = accountsTransferService.transferAmountBetweenAccounts(sourceAccountId,
							destAccountId, new BigDecimal("1.00"));
					if (resultObj.containsKey("isSuccess") && resultObj.getBoolean("isSuccess") != null) {
						Account modifiedDestAccObj=accountsRepositoryInMemoryObj.getAccount(destAccountId);

						if (threadOneConcurrentMap.containsKey(destAccountId)) {
							
							threadOneConcurrentMap.replace(destAccountId,
									new Account(destAccountId,modifiedDestAccObj.getBalance()));
							
						} 
						else {
							threadOneConcurrentMap.putIfAbsent(destAccountId,
									new Account(destAccountId,modifiedDestAccObj.getBalance()));
							
							//threadOneConcurrentMap.putIfAbsent(destAccountId,modifiedDestAccObj);
						}
					}

				}
				loggerTestObj.info("Actual calulated Map value " + threadOneConcurrentMap.get(destAccountId).getBalance());
				assertThat(threadOneConcurrentMap.get(destAccountId).getBalance()).isEqualTo(new BigDecimal("2100.00"));
			
			}
		});

		Thread t2 = new Thread(new Runnable() {
			public void run() {
				for (int i = 1; i <= 100; i++) {

					JsonObject resultObjThread2 = accountsTransferService.transferAmountBetweenAccounts(sourceAccountId,
							destAccountId, new BigDecimal("2.00"));
					if (resultObjThread2.containsKey("isSuccess") && resultObjThread2.getBoolean("isSuccess") != null) {
						
						Account destAccObj=accountsRepositoryInMemoryObj.getAccount(destAccountId);
						
						if (threadTwoConcurrentMap.containsKey(destAccountId)) {
							loggerTestObj.info(destAccObj.getBalance().toString());
							threadTwoConcurrentMap.replace(destAccountId,new Account(destAccountId, destAccObj.getBalance()));
						} 
						else {
							threadTwoConcurrentMap.putIfAbsent(destAccountId,new Account(destAccountId, destAccObj.getBalance()));
						}

					}

				}
				loggerTestObj.info("Actual calulated Map value " + threadTwoConcurrentMap.get(destAccountId).getBalance());
				assertThat(threadTwoConcurrentMap.get(destAccountId).getBalance()).isEqualTo(new BigDecimal("2300.00"));
			}
		});
		t1.start();
		t2.start();

		try {
			/* if threads are executing same time then interrupted exception is thrown */
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void transfer_between_AccountsSingleTest() {

		String sourceAccountId = "id-789";
		String destAccountId = "id-001";
		
	    accountsRepositoryInMemoryObj.createAccount(new Account(sourceAccountId, new BigDecimal("1000.00")));
	    accountsRepositoryInMemoryObj.createAccount(new Account(destAccountId, new BigDecimal("2000.00")));
	   

		loggerTestObj.info("Repository Map value " + accountsRepositoryInMemoryObj.getAccounts().toString());
		loggerTestObj.info("Repository Map value " + accountsRepositoryInMemoryObj.getAccounts().get(destAccountId));

		when(acctReposObj.getAccount(sourceAccountId)).thenReturn(new Account(sourceAccountId, new BigDecimal("1000.00")));
		when(acctReposObj.getAccount(destAccountId)).thenReturn(new Account(destAccountId, new BigDecimal("2000.00")));

		accountsTransferService.transferAmountBetweenAccounts(sourceAccountId, destAccountId, new BigDecimal("100.00"));

		// assert statement
		loggerTestObj.info(
				"Repository Map value " + accountsRepositoryInMemoryObj.getAccounts().get(destAccountId).getBalance());

		assertThat(accountsRepositoryInMemoryObj.getAccounts().get(destAccountId).getBalance())
				.isEqualTo((new BigDecimal("2100.00")));

	}

	@Test
	public void createAccountDoesNotExistTest() {
		
		boolean isSuccess=false;
		String sourceAccountId = "id-4589";
		String destAccountId = "id-001";

		when(acctReposObj.getAccount(sourceAccountId)).thenReturn(new Account(sourceAccountId, new BigDecimal("1000.00")));
		when(acctReposObj.getAccount(destAccountId)).thenReturn(new Account(destAccountId, new BigDecimal("2000.00")));

		try {
			accountsTransferService.transferAmountBetweenAccounts(sourceAccountId, destAccountId, new BigDecimal("100.00"));
			isSuccess=true;
		}catch (AccountDoesNotExistException e) {
			isSuccess=false;
		}
		

		// assert statement
		assertFalse(isSuccess);

	}
	
	@Test
	public void createInsufficientFundsTest() {
		
		boolean isSuccess=false;
		String sourceAccountId = "id-123";
		String destAccountId = "id-456";

		when(acctReposObj.getAccount(sourceAccountId)).thenReturn(new Account(sourceAccountId, new BigDecimal("1000.00")));
		when(acctReposObj.getAccount(destAccountId)).thenReturn(new Account(destAccountId, new BigDecimal("2000.00")));

		try {
			accountsTransferService.transferAmountBetweenAccounts(sourceAccountId, destAccountId, new BigDecimal("5500.00"));
			isSuccess=true;
		}catch (InvalidAmountException e) {
			isSuccess=false;
		}
		

		// assert statement
		assertFalse(isSuccess);

	}
	
	
}
