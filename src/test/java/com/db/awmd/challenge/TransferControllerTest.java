package com.db.awmd.challenge;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.repository.AccountsRepositoryInMemory;
import com.db.awmd.challenge.service.AccountsService;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TransferControllerTest {

	  private MockMvc mockMvc;
	  private static Logger logger = LoggerFactory.getLogger(TransferControllerTest.class);
	  
	  @Autowired
	  private  AccountsRepository accountsRepositoryObj;
	  
	  @Autowired
	  private AccountsService accountsService;

	  @Autowired
	  private WebApplicationContext webApplicationContext;
	  
		@Autowired
		private AccountsRepositoryInMemory accountsInmemoryObj;

	  @Before
	  public void prepareMockMvc() {
	    this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

	    // Reset the existing accounts before each test.
	    accountsService.getAccountsRepository().clearAccounts();
	  }

	  @Test
	  public void transfer_Amount_BetweenAccounts() throws Exception  {

		   //setting account values
		   accountsRepositoryObj.setDemoAccount();
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

		   //setting account values
		   accountsRepositoryObj.setDemoAccount(); 
		   
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
	                                    .content("{\"srcAcctId\":\"id-019\",\"amount\":-50.5,\"destAcctId\":\"id-456\"}")
	                                    .accept(MediaType.APPLICATION_JSON)
	                                    .characterEncoding("UTF-8");
	    
	    this.mockMvc.perform(builder)
	      .andExpect(status().isBadRequest());  
	  }  
	  
	  @Test
	  public void createInsufficientAmount() throws Exception {

		 //setting account values
		   accountsRepositoryObj.setDemoAccount();
          
		  MockHttpServletRequestBuilder builder =
	              MockMvcRequestBuilders.put("/v1/amount/transfer")
	                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
	                                    .content("{\"srcAcctId\":\"id-123\",\"amount\":1000,\"destAcctId\":\"id-456\"}")
	                                    .accept(MediaType.APPLICATION_JSON)
	                                    .characterEncoding("UTF-8");
	    
	    this.mockMvc.perform(builder)
	      .andExpect(status().isBadRequest());  
	  }  

}
