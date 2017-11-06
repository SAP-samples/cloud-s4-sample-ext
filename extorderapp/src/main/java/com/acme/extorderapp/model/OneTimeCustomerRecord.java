package com.acme.extorderapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "d")
public class OneTimeCustomerRecord {

	private String id;
	private String orderId;
	private String lastName;
	private String firstName;
	private String phoneNumber;
	private String shippingAddress;
	private String bankAccount;

	public OneTimeCustomerRecord() {
	}

	@JsonProperty(value = "SAP_UUID") // override since this property is returned by the backend in upper case
	public String getId() {
		return id;
	}

	@JsonProperty(value = "SAP_UUID") // override since this property is returned by the backend in upper case
	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty(value = "OrderID") // In case the custom business object does not follow  standard camelCase naming, we can override the property name.
	public String getOrderId() {
		return this.orderId;
	}

	@JsonProperty(value = "OrderID")
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

}