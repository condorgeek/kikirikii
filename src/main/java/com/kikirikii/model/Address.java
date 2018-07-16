package com.kikirikii.model;

import javax.persistence.Embeddable;

@Embeddable
public class Address {
    private String street;
    private String number;
    private String optional;
    private String areacode;
    private String city;
    private String country;

    private Address() {}

    public static Address of(String city, String country) {
        return of(null, null, null, null, city, country);
    }

    public static Address of(String street, String number, String optional, String areacode, String city, String country) {
        Address address = new Address();
        address.street = street;
        address.number = number;
        address.optional = optional;
        address.areacode = areacode;
        address.city = city;
        address.country = country;
        return address;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getOptional() {
        return optional;
    }

    public void setOptional(String optional) {
        this.optional = optional;
    }

    public String getAreacode() {
        return areacode;
    }

    public void setAreacode(String areacode) {
        this.areacode = areacode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
