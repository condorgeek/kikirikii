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

    @NotNull
    @Enumerated(EnumType.STRING)
    private LikeReaction reaction;

    @NotNull
    private Date created;

    private CommentLike() {
    }

    public static CommentLike of(User user, LikeReaction reaction) {
        return of(null, user, reaction);
    }

    public static CommentLike of(Comment comment, User user, LikeReaction reaction) {
        CommentLike like = new CommentLike();
        like.comment = comment;
        like.user = user;
        like.reaction = reaction;
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

    public LikeReaction getReaction() {
        return reaction;
    }

    public void setReaction(LikeReaction reaction) {
        this.reaction = reaction;
    }

    public Date getCreated() {
        return created;
    }

}
