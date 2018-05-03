package com.kikirikii.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "friends")
public class Friend {

    public enum State {ACTIVE, BLOCKED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "surrogate_id")
    private User surrogate;

    @JsonIgnore
    @NotNull
    @Enumerated(EnumType.STRING)
    private State state;

    @NotNull
    private Date created;

    private Friend(){}

    public static Friend of(User user, User surrogate) {
        Friend friend = new Friend();
        friend.user = user;
        friend.surrogate = surrogate;
        friend.state = State.ACTIVE;
        friend.created = new Date();
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

}
