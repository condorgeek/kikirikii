package com.kikirikii.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kikirikii.security.PasswordCrypt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "users")
public class User {

    public enum State {ACTIVE, BLOCKED, DELETED}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(name = "email", unique = true)
    private String email;

    @NotNull
    @Column(name = "name", unique = true)
    private String name;

    @NotNull
    private String firstname;

    @NotNull
    private String lastname;

    @JsonIgnore
    private String salt;

    @JsonIgnore
    @NotNull
    private String password;

    private String thumbnail;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private UserData userData;

    @JsonIgnore
    @NotNull
    @Enumerated(EnumType.STRING)
    private State state;

    private User(){}

    public static User of(String email, String userid, String firstname, String lastname, String password) {
        return of(email, userid, firstname, lastname, password, null);
    }

    public static User of(String email, String userid, String firstname, String lastname, String password, String thumbnail) {
        User user = new User();
        user.email = email;
        user.name = userid.toLowerCase();
        user.firstname = firstname;
        user.lastname = lastname;
        user.state = State.ACTIVE;
        user.thumbnail = thumbnail;
        user.salt = PasswordCrypt.getSalt(64);
        user.password = PasswordCrypt.encrypt(password, user.salt);
        return user;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public State getState() {
        return state;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public UserData getUserData() {
        return userData;
    }

    public User setUserData(UserData userData) {
        userData.setUser(this);
        this.userData = userData;
        return this;
    }

    public Boolean verifyPassword(String password) {
        return PasswordCrypt.verify(password, this.password, salt);
    }

    public void setPassword(String password) {
        this.password = PasswordCrypt.encrypt(password, salt);
    }
}
