package com.acme.intreportapp.model;

import com.sap.cloud.sdk.result.ElementName;

public class OneTimeCustomerRecord {

	@ElementName(value = "SAP_UUID")
	private String id;

	@ElementName(value = "OrderID")
	private String orderId;

	@ElementName(value = "LastName")
	private String lastName;

	@ElementName(value = "FirstName")
	private String firstName;

	@ElementName(value = "PhoneNumber")
	private String phoneNumber;

	@ElementName(value = "ShippingAddress")
	private String shippingAddress;

	@ElementName(value = "BankAccount")
	private String bankAccount;

	public OneTimeCustomerRecord() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrderId() {
		return this.orderId;
	}

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