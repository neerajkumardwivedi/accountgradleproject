package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
public class RequestDTO {

	@NotNull
	@NotEmpty(message = "Account id cannot be empty")
	private String srcAccId;

	@NotNull
	@NotEmpty(message = "Account id cannot be empty")
	private String destAcctId;

	private BigDecimal amount;

	@JsonCreator
	public RequestDTO(@JsonProperty("srcAcctId") String accountId, @JsonProperty("destAcctId") String destAccId,
			@JsonProperty("amount") BigDecimal amount) {
		this.srcAccId = accountId;
		this.destAcctId = destAccId;
		this.amount = amount;
	}

}
