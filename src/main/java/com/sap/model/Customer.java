package com.sap.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Customer implements Serializable {

    private static final long serialVersionUID = 9182542553176868553L;
    private final String customerId;
    private final String customerFullName;

    private Customer() {
        this.customerFullName = "";
        this.customerId = "";
    }

    private Customer(String customerId, String customerFullName) {
        this.customerId = customerId;
        this.customerFullName = customerFullName;
    }

    public static Customer create(String customerId, String customerFullName) {
        return new Customer(customerId, customerFullName);
    }

    public static Customer getInstance() {
        return new Customer();
    }

}
