package com.acme.extorderapp.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "d")
public class Products {

	private List<Product> results;

	public Products() {
	}

	public List<Product> getResults() {
		return results;
	}

	@JsonProperty(value = "results") // override since the property is in lowercase already.
	public void setResults(List<Product> results) {
		this.results = results;
	}
	
}