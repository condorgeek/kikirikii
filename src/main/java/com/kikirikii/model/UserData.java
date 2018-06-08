package com.kikirikii.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "user_data")
public class UserData {

    public enum Marital {SINGLE, ENGAGED, MARRIED, DIVORCED, COMPLICATED}

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

    private Address address;

    private String telNumber;

    @Column(columnDefinition = "text", length = 10485760)
    private String aboutYou;

    private String religion;

    private String politics;

    private Marital marital;

    private Interest interest;

    private UserData() {
    }

    public static UserData of(LocalDate birthday, String telNumber, Address address) {
        return of(null, birthday, telNumber, address);
    }

    public static UserData of(User user, LocalDate birthday, String telNumber, Address address) {
        UserData userData = new UserData();
        userData.user = user;
        userData.birthday = birthday;
        userData.address = address;
        userData.telNumber = telNumber;
        userData.marital = Marital.SINGLE;
        userData.interest = Interest.NONE;
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
}