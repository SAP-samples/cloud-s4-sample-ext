package com.acme.extorderapp.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "d")
public class ProductDescriptions {

	private List<ProductDescription> results;		

	public ProductDescriptions() {
	}

	public List<ProductDescription> getResults() {
		if (results == null) {
			results = new ArrayList<ProductDescription>();
		}		
		return results;
	}

	@JsonProperty(value = "results") // override since the property is in lowercase already. 
	public void setResults(List<ProductDescription> results) {
		this.results = results;
	}
	
}