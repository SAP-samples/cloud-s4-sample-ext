package com.acme.extorderapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "d")

public class ProductDescription {
		
	private String productDescription = "";
	private String language = "";
	
	public ProductDescription() {
	}
	
	public String getProductDescription() {
		return this.productDescription;
	}
	
	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}
	
	public String getLanguage() {
		return this.language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}

}