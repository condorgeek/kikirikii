/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [Like.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 14.05.18 12:03
 */

package com.kikirikii.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "post_likes")
public class Like {

    public enum State {ACTIVE, DELETED}

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    private LikeReaction reaction;

    @Enumerated(EnumType.STRING)
    private State state;

    @NotNull
    private Date created;

    private Like() {}

    public static Like of(User user, LikeReaction type) {
        return of(null, user, type);
    }

    public static Like of(Post post, User user, LikeReaction reaction) {
        Like like = new Like();
        like.post = post;
        like.user = user;
        like.reaction = reaction;
        like.state = State.ACTIVE;
        like.created = new Date();
        return like;
    }

    public long getId() {
        return id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LikeReaction getReaction() {
        return reaction;
    }

    public void setReaction(LikeReaction reaction) {
        this.reaction = reaction;
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

}
