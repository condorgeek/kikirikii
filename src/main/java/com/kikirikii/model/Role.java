/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [Role.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 08.06.18 18:13
 */

package com.kikirikii.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "roles")
public class Role {

    public enum State {ACTIVE, DELETED}

    public enum Type {
        USER, SUPERUSER, ADMIN;

        public String authority() {
            return "ROLE_" + this.name();
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Type type;

    @NotNull
    @Enumerated(EnumType.STRING)
    private State state;

    private Role() {
    }

    public static Role of(Type type) {
        return of(null, type);
    }

    public static Role[] asArray(Type type) {
        return new Role[]{Role.of(type)};
    }
    public static Role[] of(Type... types) {
        return (Role[]) Arrays.stream(types).map(Role::of).toArray();
    }

    public static Role of(User user, Type type) {
        Role role = new Role();
        role.type = type;
        role.user = user;
        role.state = State.ACTIVE;
        return role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getAuthority() {
        return type.authority();
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public long getId() {
        return id;
    }
}
