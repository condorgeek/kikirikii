package com.kikirikii.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "users")
public class User {

    public enum State {ACTIVE, BLOCKED, DELETED}
    @Id
    private String id;

    @NotNull
    private String name;

    @JsonIgnore
    @NotNull
    private String password;

    private String thumbnail;

    @NotNull
    @Enumerated(EnumType.STRING)
    private State state;

    private User(){}

    public static User of(String email, String name, String password) {
        return of(email, name, password, null);
    }

    public static User of(String email, String name, String password, String thumbnail) {
        User user = new User();
        user.id = email;
        user.name = name;
        user.state = State.ACTIVE;
        user.password = password;
        user.thumbnail = thumbnail;
        return user;
    }

    public String getId() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
