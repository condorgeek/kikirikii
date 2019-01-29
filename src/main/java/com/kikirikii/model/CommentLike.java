/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [CommentLike.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 14.05.18 11:56
 */

package com.kikirikii.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kikirikii.model.enums.Reaction;
import com.kikirikii.model.enums.State;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "comment_likes")
public class CommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private State state;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Reaction reaction;

    @NotNull
    private Date created;

    private CommentLike() {
    }

    public static CommentLike of(User user, Reaction reaction) {
        return of(null, user, reaction);
    }

    public static CommentLike of(Comment comment, User user, Reaction reaction) {
        CommentLike like = new CommentLike();
        like.comment = comment;
        like.user = user;
        like.reaction = reaction;
        like.state = State.ACTIVE;
        like.created = new Date();
        return like;
    }

    public long getId() {
        return id;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Reaction getReaction() {
        return reaction;
    }

    public void setReaction(Reaction reaction) {
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
