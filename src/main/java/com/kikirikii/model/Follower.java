/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [Follower.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 30.08.18 16:48
 */

package com.kikirikii.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Action              | action user    | action surrogate | status user | status surrogate
 * --------------------+----------------+------------------+-------------+-------------------
 * follow user         |(*)FOLLOWING   -->  FOLLOWED       | ACTIVE      | ACTIVE
 * delete followee     |(*)DELETING    -->  DELETED        | DELETED     | DELETED
 * block follower      |   BLOCKED    <--  BLOCKING(*)     | BLOCKED     | BLOCKED
 * unblock follower    |   UNBLOCKED  <--  UNBLOCKING(*)   | ACTIVE      | ACTIVE
 * -------------------------------------------------------------------------------------------
 * see UserService for state logic implementation
 * FOLLOWER - person that follows you, FOLLOWEE (surrogate) - person being followed by you
 */
@Entity
@Table(name = "followers")
public class Follower {

    public enum State {ACTIVE, BLOCKED, DELETED, NONE}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonProperty("follower")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonProperty("followee")
    @OneToOne
    @JoinColumn(name = "surrogate_id")
    private User surrogate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private State state;

    @NotNull
    private Date created;

    @JsonIgnore
    @NotNull
    private Date transitioned;

    private Follower(){}

    public static Follower of(User user, User surrogate) {
        return of(user, surrogate, State.NONE);
    }

    public static Follower of(User user, User surrogate, State state) {
        Follower follower = new Follower();
        follower.user = user;
        follower.surrogate = surrogate;
        follower.state = state;
        follower.created = new Date();
        follower.transitioned = new Date();
        return follower;
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

    public User getSurrogate() {
        return surrogate;
    }

    public void setSurrogate(User surrogate) {
        this.surrogate = surrogate;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        this.transitioned = new Date();
    }

    public Date getTransitioned() {
        return transitioned;
    }

    public void setTransitioned(Date transitioned) {
        this.transitioned = transitioned;
    }

    public Date getCreated() {
        return created;
    }
}
