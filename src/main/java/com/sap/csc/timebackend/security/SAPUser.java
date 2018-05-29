package com.sap.csc.timebackend.security;

import org.springframework.context.annotation.Profile;

import java.io.Serializable;
import java.util.Objects;
public class SAPUser implements Serializable {

    private static final long serialVersionUID = -7710130494737998501L;
    private String id;

    private SAPUser() {

    }

    private SAPUser(String id) {
        this.id = id;
    }

    public static SAPUser create(String id) {
        return new SAPUser(id);
    }

    public String getId() {
        return id;
    }

    public void setUser(String id) {
        Objects.requireNonNull(id, "User cannot be null!");
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SAPUser sapUser = (SAPUser) o;

        return id != null ? id.equals(sapUser.id) : sapUser.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}