package com.db.awmd.challenge.domain;

import com.db.awmd.challenge.service.AccountsService;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class Account {

  @NotNull
  @NotEmpty
  private final String accountId;

  @NotNull
  @Min(value = 0, message = "Initial balance must be positive.")
  private BigDecimal balance;

  private static Logger loggerAcct = LoggerFactory.getLogger(AccountsService.class);
  
  public Account(String accountId) {
    this.accountId = accountId;
    this.balance = BigDecimal.ZERO;
  }

  public String getAccountId() {
	return accountId;
}
  

public BigDecimal getBalance() {
	return balance;
}

public void setBalance(BigDecimal balance) {
	this.balance = balance;
}

@JsonCreator
  public Account(@JsonProperty("accountId") String accountId,
    @JsonProperty("balance") BigDecimal balance) {
    this.accountId = accountId;
    this.balance = balance;
  }


/*@Parameter:amount= Withdrawing the amount from account*/
	public synchronized BigDecimal withdraw(BigDecimal amount, Account acct) {
		if (amount.compareTo(BigDecimal.ZERO) <0) // withdraw value is negative
		{
			throw new ArithmeticException("Error: Withdraw amount is invalid.");

		}else if(amount.compareTo(acct.getBalance())>0) {
			throw new ArithmeticException("Error: Insufficient funds.");
		}
		 else {
			 loggerAcct.info("Before withdraw"+acct.getBalance().doubleValue());
			 acct.setBalance(acct.getBalance().subtract(amount));
	
		}
		loggerAcct.info("After withdraw"+acct.getBalance().doubleValue());	
		return acct.getBalance();
	}

	/*-----------------------------------------------------------------
	Validates the transaction, then deposits the specified amount
	 into the account. Returns the new balance.
	----------------------------------------------------------------
	@Parameter:amount= depositing the amount in account*/
	public synchronized BigDecimal deposit(BigDecimal amount, Account acct) {
		
		if (amount.compareTo(BigDecimal.ZERO) <0) // deposit value is negative
		{
			System.out.println("Error: Deposit amount is invalid.");

		} else {
			loggerAcct.info("Before deposit "+acct.getBalance().doubleValue());
			 acct.setBalance(acct.getBalance().add(amount));
		}
		loggerAcct.info("After deposit balance "+acct.getBalance().doubleValue());
		return acct.getBalance();
	}


}
