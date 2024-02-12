package com.challenge.tax_service.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TaxCalculationRequest {

	@JsonProperty
	private String id;

	@JsonProperty
	private String taxtype;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTaxtype() {
		return taxtype;
	}

	public void setTaxtype(String taxtype) {
		this.taxtype = taxtype;
	}
}
