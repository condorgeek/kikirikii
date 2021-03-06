/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [User.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 19.08.18 17:53
 */

package com.kikirikii.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kikirikii.model.enums.State;
import com.kikirikii.security.util.PasswordCrypt;
import org.omg.PortableInterceptor.USER_EXCEPTION;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(name = "email", unique = true)
    private String email;

    @NotNull
    @Column(name = "username", unique = true)
    private String username;

    @NotNull
    private String firstname;

    @NotNull
    private String lastname;

    @JsonIgnore
    private String salt;

    private int ranking;

    @JsonIgnore
    @NotNull
    private String password;

    private String avatar;

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private UserData userData;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

    @NotNull
    @Enumerated(EnumType.STRING)
    private State state;

    @NotNull
    private Date created;

    private User(){}

    public static User of(String email, String username, String firstname, String lastname, String password) {
        return of(email, username, firstname, lastname, password, null, Role.asArray(Role.Type.USER));
    }

    public static User of(String email, String username, String firstname, String lastname, String password,
                          String avatar, Role[] roles) {
        User user = new User();
        user.email = email;
        user.username = username.toLowerCase();
        user.firstname = firstname;
        user.lastname = lastname;
        user.state = State.ACTIVE;
        user.ranking = 0;
        user.avatar = avatar;
        if(roles != null) {
            Arrays.stream(roles).forEach(user::addRole);
        } else {
            user.addRole(Role.of(Role.Type.USER));
        }
        user.salt = PasswordCrypt.getSalt(64);
        user.password = PasswordCrypt.encrypt(password, user.salt);
        user.created = new Date();
        return user;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public State getState() {
        return state;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public String getFullname() {
        return firstname + " " + lastname;
    }

    public UserData getUserData() {
        return userData;
    }

    public User setUserData(UserData userData) {
        userData.setUser(this);
        this.userData = userData;
        return this;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public User addRole(Role role) {
        this.roles.add(role);
        role.setUser(this);
        return this;
    }

    public User removeRole(Role role) {
        this.roles.remove(role);
        role.setUser(null);
        return this;
    }

    public Boolean verifyPassword(String password) {
        return PasswordCrypt.verify(password, this.password, salt);
    }

    public void setPassword(String password) {
        this.password = PasswordCrypt.encrypt(password, salt);
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
