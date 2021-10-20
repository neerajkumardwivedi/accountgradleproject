package com.db.awmd.challenge;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.math.BigDecimal;
import java.util.Objects;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.service.AccountsService;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TransferControllerTest {

	  private MockMvc mockMvc;
	  
	  @Autowired
	  private  AccountsRepository accountsRepositoryObj;
	  
	  @Autowired
	  private AccountsService accountsService;

	  @Autowired
	  private WebApplicationContext webApplicationContext;

	  @Before
	  public void prepareMockMvc() {
	    this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

	    // Reset the existing accounts before each test.
	    accountsService.getAccountsRepository().clearAccounts();
	    String sourceAccountId = "id-123";
		String destAccountId = "id-456";
		
		Account srcAccount = accountsRepositoryObj.getAccount(sourceAccountId);
	    Account destinationAccount = accountsRepositoryObj.getAccount(destAccountId);
	    
	    if(Objects.isNull(srcAccount)) {
	    	accountsRepositoryObj.createAccount(new Account(sourceAccountId, new BigDecimal("1000.00")));
	    }
	    if(Objects.isNull(destinationAccount)) {	    	
	    	accountsRepositoryObj.createAccount(new Account(destAccountId, new BigDecimal("2000.00")));
	    }
	    
	  }

	  @Test
	  public void transfer_Amount_BetweenAccounts() throws Exception  {

		    MockHttpServletRequestBuilder builder =
		              MockMvcRequestBuilders.put("/v1/amount/transfer")
		                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
		                                    .content("{\"srcAcctId\":\"id-123\",\"amount\":50.5,\"destAcctId\":\"id-456\"}")
		                                    .accept(MediaType.APPLICATION_JSON)
		                                    .characterEncoding("UTF-8");
		    
		    this.mockMvc.perform(builder)
		      .andExpect(status().isOk());
		      
		  }
	  
	  @Test
	  public void createAccountNotFound() throws Exception {

		  MockHttpServletRequestBuilder builder =
	              MockMvcRequestBuilders.put("/v1/amount/transfer")
	                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
	                                    .content("{\"srcAcctId\":\"id-019\",\"amount\":50.5,\"destAcctId\":\"id-456\"}")
	                                    .accept(MediaType.APPLICATION_JSON)
	                                    .characterEncoding("UTF-8");
	    
	    this.mockMvc.perform(builder)
	      .andExpect(status().isNotFound());  
	  } 
  
	  @Test
	  public void createInvalidTransferAmount() throws Exception {

		  MockHttpServletRequestBuilder builder =
	              MockMvcRequestBuilders.put("/v1/amount/transfer")
	                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
	                                    .content("{\"srcAcctId\":\"id-123\",\"amount\":-50.5,\"destAcctId\":\"id-456\"}")
	                                    .accept(MediaType.APPLICATION_JSON)
	                                    .characterEncoding("UTF-8");
	    
	    this.mockMvc.perform(builder)
	      .andExpect(status().isBadRequest());  
	  }  
	  
	  @Test
	  public void createInsufficientAmount() throws Exception {

		  MockHttpServletRequestBuilder builder =
	              MockMvcRequestBuilders.put("/v1/amount/transfer")
	                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
	                                    .content("{\"srcAcctId\":\"id-123\",\"amount\":5000,\"destAcctId\":\"id-456\"}")
	                                    .accept(MediaType.APPLICATION_JSON)
	                                    .characterEncoding("UTF-8");
	    
	    this.mockMvc.perform(builder)
	      .andExpect(status().isBadRequest());  
	  }  

}
