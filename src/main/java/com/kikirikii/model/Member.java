/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [Member.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 21.10.18 16:43
 */

package com.kikirikii.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * wrapper class for a user belonging to a space
 */

@Entity
@Table(name = "members")
public class Member {

    public enum State {ACTIVE, BLOCKED, DELETED, NONE}

    public enum Role {OWNER, ADMIN, MEMBER}

    public enum Action {JOINING, JOINED,  REJECTING, REJECTED, CANCELLING, CANCELLED,
        DELETING, DELETED, BLOCKING, BLOCKED, UNBLOCKING, UNBLOCKED, NONE}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "space_id")
    private Space space;

    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    /* may be null if owner */
    @OneToOne
    @JoinColumn(name = "referer_id")
    private User referer;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Action action;

    @NotNull
    @Enumerated(EnumType.STRING)
    private State state;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    @NotNull
    private Date created;

    private Member() {}

    public static Member of(Space space, User user, State state, Role role) {
        return of(space, user, user, state, role);
    }

    public static Member of(Space space, User user, User referer, State state, Role role) {
        Member member = new Member();
        member.space = space;
        member.user = user;
        member.referer = referer;
        member.state = state;
        member.role = role;
        member.action = Action.NONE;
        member.created = new Date();
        return member;
    }

    public long getId() {
        return id;
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getReferer() {
        return referer;
    }

    public void setReferer(User referer) {
        this.referer = referer;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Date getCreated() {
        return created;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
