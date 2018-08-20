package com.kikirikii.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Action              | action user    | action surrogate | status user | status surrogate
 * --------------------+----------------+------------------+-------------+-------------------
 * request friend      |(*)REQUESTING  -->  REQUESTED      | PENDING     | PENDING
 * accept request      |   ACCEPTED    <--  ACCEPTING(*)   | ACTIVE      | ACTIVE
 * cancel request      |(*)CANCELLING  -->  CANCELLED      | CANCELLED   | CANCELLED
 * ignore request      |   IGNORED     <--  IGNORING(*)    | IGNORED     | IGNORED
 * delete friend       |(*)DELETING    -->  DELETED        | DELETED     | DELETED
 *                     |   DELETED     <--  DELETING(*)    |
 * block friend        |(*)BLOCKING    -->  BLOCKED        | BLOCKED     | BLOCKED
 *                     |   BLOCKED     <--  BLOCKING(*)    |
 * unblock friend      |(*)UNBLOCKING  -->  UNBLOCKED      | ACTIVE      | ACTIVE
 * -------------------------------------------------------------------------------------------
 * see UserService for state logic implementation
 */

@Entity
@Table(name = "friends")
public class Friend {

    public enum State {ACTIVE, BLOCKED, DELETED, CANCELLED, IGNORED, PENDING, NONE}

    public enum Action {REQUESTING, REQUESTED, ACCEPTING, ACCEPTED, CANCELLING, CANCELLED,
        IGNORING, IGNORED, DELETING, DELETED, BLOCKING, BLOCKED, UNBLOCKING, UNBLOCKED, NONE}

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

    @JsonIgnore
    @NotNull
    @Enumerated(EnumType.STRING)
    private Action action;

    @NotNull
    private Date created;

    private Date transitioned;

    private Date actioned;

    private Friend(){}

    public static Friend of(User user, User surrogate) {
        return of(user, surrogate, State.NONE, Action.NONE);
    }

    public static Friend of(User user, User surrogate, State state, Action action) {
        Friend friend = new Friend();
        friend.user = user;
        friend.surrogate = surrogate;
        friend.state = state;
        friend.action = action;
        friend.created = new Date();
        friend.transitioned = new Date();
        friend.actioned = new Date();
        return friend;
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

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        this.transitioned = new Date();
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
        this.actioned = new Date();
    }

    public User getSurrogate() {
        return surrogate;
    }

    public void setSurrogate(User surrogate) {
        this.surrogate = surrogate;
    }

    public Date getCreated() {
        return created;
    }

    public Date getTransitioned() {
        return transitioned;
    }

    public void setTransitioned(Date transitioned) {
        this.transitioned = transitioned;
    }

    public Date getActioned() {
        return actioned;
    }

    public void setActioned(Date actioned) {
        this.actioned = actioned;
    }
}
