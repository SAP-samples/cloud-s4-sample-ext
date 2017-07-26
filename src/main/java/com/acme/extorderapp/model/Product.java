package com.acme.extorderapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "d")
public class Product {

	private String product;
	private ProductDescriptions descriptions;

	public Product() {
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	@JsonProperty(value = "to_Description") // override since the backend does not use UpperCamelCase for associatons
	public void setTo_Description(ProductDescriptions results) {
		this.descriptions = results;
	}		
	
	public String getDescription() { // helper method to retrieve the english description
		if (this.descriptions != null && this.descriptions.getResults() != null) {
			for (ProductDescription d : this.descriptions.getResults()) {
				if ("EN".equalsIgnoreCase(d.getLanguage())) {
					return d.getProductDescription();
				}
			}
		}
		return "n/a";
	}

}