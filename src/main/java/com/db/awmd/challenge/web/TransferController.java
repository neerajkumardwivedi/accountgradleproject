package com.db.awmd.challenge.web;

import java.math.BigDecimal;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.db.awmd.challenge.domain.RequestDTO;
import com.db.awmd.challenge.exception.InvalidAmountException;
import com.db.awmd.challenge.service.AccountsService;

import io.vertx.core.json.JsonObject;

@RestController
@RequestMapping("/v1/amount")
public class TransferController {

	private static Logger logger = LoggerFactory.getLogger(TransferController.class);

	@Autowired
	private AccountsService accountsService;

	/*
	 * @Description: Transferring amount from one accountid to other
	 * 
	 * @Parameter: Mandatory srcAcctid,destAcctid and amount to be transferred
	 * e.g.localhost:18080/transfer
	 */

	@PutMapping(path = "/transfer", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Object> transferAmountBetweenAccounts(@RequestBody @Valid RequestDTO reqObj) {

		logger.info("Inside Transfer Amount code");

		BigDecimal transferAmount = reqObj.getAmount();
		if (reqObj != null) {

			if (transferAmount.compareTo(BigDecimal.ZERO) < 0) {
				logger.info("deposit value is negative");
				throw new InvalidAmountException("Transfer amount should not be negative" + transferAmount);
			}
			JsonObject resultObj = accountsService.transferAmountBetweenAccounts(reqObj.getSrcAccId(),
					reqObj.getDestAcctId(), reqObj.getAmount());
			if (resultObj.containsKey("isSuccess") && resultObj.getBoolean("isSuccess")) {

				return new ResponseEntity<>("Amount Transferred Successfully", HttpStatus.OK);

			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

			}
		} else {

			return new ResponseEntity<>("Missing Parameters values", HttpStatus.BAD_REQUEST);
		}

	}

}
