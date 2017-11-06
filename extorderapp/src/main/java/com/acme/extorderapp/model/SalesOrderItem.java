package com.acme.extorderapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "d")
public class SalesOrderItem {
	public String requestedQuantity;
	public String material;

	public SalesOrderItem() {	
	}
	
	public String getRequestedQuantity() {
		return requestedQuantity;
	}
	
	public void setRequestedQuantity(String requestedQuantity) {
		this.requestedQuantity = requestedQuantity;
	}
	
	public String getMaterial() {
		return material;
	}
	
	public void setMaterial(String material) {
		this.material = material;
	}
	
}