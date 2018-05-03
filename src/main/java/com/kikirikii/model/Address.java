package com.kikirikii.model;

import javax.persistence.Embeddable;

@Embeddable
public class Address {
    private String street;
    private int number;
    private String optional;
    private int areacode;
    private String city;
    private String country;

    private Address() {}

    public static Address of() {
        Address address = new Address();
        return address;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}
