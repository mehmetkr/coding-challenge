package com.coding.challenge.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpsertProductRequest {


	@JsonProperty
	private String id;

	@JsonProperty
	private String name;

	@JsonProperty
	private String price;

	@JsonProperty
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
