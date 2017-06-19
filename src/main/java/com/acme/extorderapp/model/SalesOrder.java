package com.acme.extorderapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "d")
public class SalesOrder {
	
	private String salesOrder;
	private String salesOrderType;
	private String distributionChannel;
	private String organizationDivision;
	private String soldToParty = "";
	private String purchaseOrderByCustomer = "";
	private SalesOrderItems items;
	
	public static final String TYPE_STANDARD_SALES_ORDER = "OR";
	public static final String TYPE_CASH_ORDER = "BV";
	public static final String TYPE_RUSH_ORDER = "SO";

	public SalesOrder() {
	}
		
	public String getSalesOrder() {
		return salesOrder;
	}

	public void setSalesOrder(String salesOrder) {
		this.salesOrder = salesOrder;
	}

	public String getSalesOrderType() {
		return this.salesOrderType;
	}

	public void setSalesOrderType(String salesOrderType) {
		this.salesOrderType = salesOrderType;
	}

	public String getDistributionChannel() {
		return this.distributionChannel;
	}

	public void setDistributionChannel(String distributionChannel) {
		this.distributionChannel = distributionChannel;
	}

	public String getOrganizationDivision() {
		return this.organizationDivision;
	}

	public void setOrganizationDivision(String organizationDivision) {
		this.organizationDivision = organizationDivision;
	}

	public String getSoldToParty() {
		return this.soldToParty;
	}

	public void setSoldToParty(String soldToParty) {
		this.soldToParty = soldToParty;
	}
	
	public String getPurchaseOrderByCustomer() {
		return purchaseOrderByCustomer;
	}

	public void setPurchaseOrderByCustomer(String purchaseOrderByCustomer) {
		this.purchaseOrderByCustomer = purchaseOrderByCustomer;
	}

	@JsonProperty(value = "to_Item") // override since the backend does not use UpperCamelCase for associatons
	public SalesOrderItems getItems() {
		return this.items;
	}

	@JsonProperty(value = "to_Item") // override since the backend does not use UpperCamelCase for associatons
	public void setItems(SalesOrderItems item) {
		this.items = item;
	}
	
	public void addItem(SalesOrderItem item) {
		if (this.items == null) {
			this.items = new SalesOrderItems();
		}
		this.items.getResults().add(item);
	}

}