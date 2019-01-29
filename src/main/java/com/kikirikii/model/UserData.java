/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [UserData.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 22.07.18 16:52
 */

package com.kikirikii.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kikirikii.model.enums.State;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "user_data")
public class UserData {

    public enum Gender {MALE, FEMALE, NONE}
    public enum Marital {SINGLE, ENGAGED, MARRIED, DIVORCED, COMPLICATED, NONE}
    public enum Interest {MEN, WOMEN, BOTH, NONE}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Address address;

    @JsonIgnore
    private String telNumber;

    private String web;

    @Column(columnDefinition = "text", length = 10485760)
    private String aboutYou;

    @Column(columnDefinition = "text", length = 10485760)
    private String religion;

    @Column(columnDefinition = "text", length = 10485760)
    private String politics;

    @Column(columnDefinition = "text", length = 10485760)
    private String studies;

    @Column(columnDefinition = "text", length = 10485760)
    private String work;

    @Column(columnDefinition = "text", length = 10485760)
    private String interests;

    @Enumerated(EnumType.STRING)
    private Marital marital;

    @Enumerated(EnumType.STRING)
    private Interest interest;

    @NotNull
    @Enumerated(EnumType.STRING)
    private State state;

    private UserData() {
    }

    public static UserData of(LocalDate birthday, Gender gender, Marital marital, Interest interest, String aboutYou,
                              Address address) {
        return of(null, birthday, gender, marital, interest, aboutYou,
                null, null, null, null, null, null, address);
    }

    public static UserData of(User user, LocalDate birthday, Gender gender, Marital marital, Interest interest,
                              String aboutYou, String religion, String politics, String work, String studies,
                              String interests, String web, Address address) {
        UserData userData = new UserData();
        userData.user = user;
        userData.birthday = birthday;
        userData.gender = gender;
        userData.address = address;
        userData.aboutYou = aboutYou;
        userData.religion = religion;
        userData.politics = politics;
        userData.marital = marital;
        userData.interest = interest;
        userData.work = work;
        userData.studies = studies;
        userData.interests = interests;
        userData.web = web;
        userData.state = State.ACTIVE;
        return userData;
    }

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public String getAboutYou() {
        return aboutYou;
    }

    public void setAboutYou(String aboutYou) {
        this.aboutYou = aboutYou;
    }

    public Marital getMarital() {
        return marital;
    }

    public void setMarital(Marital marital) {
        this.marital = marital;
    }

    public Interest getInterest() {
        return interest;
    }

    public void setInterest(Interest interest) {
        this.interest = interest;
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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getStudies() {
        return studies;
    }

    public void setStudies(String studies) {
        this.studies = studies;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
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

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
