package com.kikirikii.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "followers")
public class Follower {

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

    @NotNull
    @Enumerated(EnumType.STRING)
    private State state;

    @NotNull
    private Date created;

    private Follower(){}

    public static Follower of(User user, User surrogate) {
        Follower follower = new Follower();
        follower.user = user;
        follower.surrogate = surrogate;
        follower.state = State.ACTIVE;
        follower.created = new Date();
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

    public Date getCreated() {
        return created;
    }

}
