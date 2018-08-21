package com.kikirikii.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
 */
@Entity
@Table(name = "followers")
public class Follower {

    public enum State {ACTIVE, BLOCKED, DELETED, NONE}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "surrogate_id")
    private User surrogate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private State state;

    @NotNull
    private Date created;

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
