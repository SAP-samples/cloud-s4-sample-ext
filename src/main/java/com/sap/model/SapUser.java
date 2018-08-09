package com.sap.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class SapUser implements Serializable {
    private final String userId;
    private final String userName;

    private SapUser() {
        this.userId = "";
        this.userName = "";
    }


    private SapUser(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }


    public static SapUser create(String userId, String userName) {
        return new SapUser(userId, userName);
    }
}
