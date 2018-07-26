package com.kikirikii.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kikirikii.security.util.PasswordCrypt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    public enum State {ACTIVE, BLOCKED, DELETED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
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

    @JsonIgnore
    @NotNull
    @Enumerated(EnumType.STRING)
    private State state;

    private User(){}

    public static User of(String email, String userid, String firstname, String lastname, String password) {
        return of(email, userid, firstname, lastname, password, null);
    }

    public static User of(String email, String username, String firstname, String lastname, String password, String avatar) {
        User user = new User();
        user.email = email;
        user.username = username.toLowerCase();
        user.firstname = firstname;
        user.lastname = lastname;
        user.state = State.ACTIVE;
        user.avatar = avatar;
        user.addRole(Role.of(Role.Type.USER));
        user.salt = PasswordCrypt.getSalt(64);
        user.password = PasswordCrypt.encrypt(password, user.salt);
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


}
