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
import java.util.function.Function;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRequest {
    //user
    public String email;
    public String username;
    public String firstname;
    public String lastname;
    public String password;

    // user data
    private String aboutYou;
    private String birthday; // as "dd/MM/yyyy"
    private UserData.Gender gender;
    private UserData.Interest interest;
    private UserData.Marital marital;

    private String web;
    private String religion;
    private String politics;
    private String work;
    private String studies;
    private String interests;
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

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getStudies() {
        return studies;
    }

    public void setStudies(String studies) {
        this.studies = studies;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public User createUser() {
        LocalDate birthday = LocalDate.parse(this.birthday, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        return User.of(this.email, this.username, this.firstname, this.lastname, this.password)
                .setUserData(UserData.of(null, birthday, this.gender, this.marital, this.interest,
                        this.aboutYou, this.religion, this.politics, this.work, this.studies, this.interests,
                        this.web, Address.of(this.street, this.number, this.optional, this.areacode,
                                this.city, this.country)
                ));
    }

    public User updateUser(User user) {

        UserData userData = user.getUserData();

        if(this.firstname != null) user.setFirstname(this.firstname);
        if(this.lastname != null) user.setLastname(this.lastname);
        if(this.email != null) user.setEmail(this.email);

        userData.setAboutYou(this.aboutYou);
        userData.setPolitics(this.politics);
        userData.setReligion(this.religion);
        userData.setWork(this.work);
        userData.setStudies(this.studies);
        userData.setInterests(this.interests);
        userData.setWeb(this.web);

        userData.setAddress(updateAddress(userData.getAddress()));
        if(this.gender != null) userData.setGender(this.gender);
        if(this.interest != null) userData.setInterest(this.interest);
        if(this.marital != null) userData.setMarital(this.marital);
        if(this.birthday != null) userData.setBirthday(asLocalDate.apply(this.birthday));

        return user;
    }

    private Address updateAddress(Address address) {
        if(address == null) {
            return Address.of(this.street, this.number, this.optional, this.areacode,
                    this.city, this.country);
        }
        if(this.street != null) address.setStreet(this.street);
        if(this.number != null) address.setStreet(this.number);
        if(this.optional != null) address.setStreet(this.optional);
        if(this.areacode != null) address.setStreet(this.areacode);
        if(this.city != null) address.setStreet(this.city);
        if(this.country != null) address.setStreet(this.country);
        return  address;
    }

    private Function<String, LocalDate> asLocalDate = birthday ->  LocalDate.parse(birthday, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
}
