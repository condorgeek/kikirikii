package com.kikirikii.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "comment_likes")
public class CommentLike {

    public enum Type {HAPPY, ANGRY, SAD, SCARED, SURPRISED, DISGUSTED}

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
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
    private Type type;

    @NotNull
    private Date created;

    private CommentLike() {}

    public static CommentLike of(Comment comment, User user, Type type) {
        CommentLike like = new CommentLike();
        like.comment = comment;
        like.user = user;
        like.type = type;
        like.created = new Date();
        return like;
    }

    public long getId() {
        return id;
    }

    public Comment getComment() {
        return comment;
    }

    public void setPost(Comment comment) {
        this.comment = comment;
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

    public Date getCreated() {
        return created;
    }

}
