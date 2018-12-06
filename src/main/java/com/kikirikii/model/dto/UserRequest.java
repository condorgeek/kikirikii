/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [UserRequest.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 19.07.18 13:26
 */

package com.kikirikii.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kikirikii.model.Address;
import com.kikirikii.model.User;
import com.kikirikii.model.UserData;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRequest {
    @NotNull
    public String email;
    @NotNull
    public String username;
    @NotNull
    public String firstname;
    @NotNull
    public String lastname;
    @NotNull
    public String password;

    // user data
    private String aboutYou;
    private String birthday; // as "dd/MM/yyyy"
    private UserData.Gender gender;
    private UserData.Interest interest;
    private UserData.Marital marital;

    private String religion;
    private String politics;
    private String telNumber;

    // address
    private String street;
    private String city;
    private String country;
    private String areacode;

    private String number;
    private String optional;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAboutYou() {
        return aboutYou;
    }

    public void setAboutYou(String aboutYou) {
        this.aboutYou = aboutYou;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public UserData.Gender getGender() {
        return gender;
    }

    public void setGender(UserData.Gender gender) {
        this.gender = gender;
    }

    public UserData.Interest getInterest() {
        return interest;
    }

    public void setInterest(UserData.Interest interest) {
        this.interest = interest;
    }

    public UserData.Marital getMarital() {
        return marital;
    }

    public void setMarital(UserData.Marital marital) {
        this.marital = marital;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getPolitics() {
        return politics;
    }

    public void setPolitics(String politics) {
        this.politics = politics;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
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

    public String getAreacode() {
        return areacode;
    }

    public void setAreacode(String areacode) {
        this.areacode = areacode;
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

    public User createUser() {
        LocalDate birthday = LocalDate.parse(this.birthday, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        return User.of(this.email, this.username, this.firstname, this.lastname, this.password)
                .setUserData(UserData.of(null, birthday, this.gender, this.marital, this.interest,
                        this.aboutYou, this.religion, this.politics,
                        Address.of(this.street, this.number, this.optional, this.areacode,
                                this.city, this.country)
                ));
    }
}
