package com.kikirikii.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "post_likes")
public class Like {

    public enum BasicType {HAPPY, ANGRY, SAD, SCARED, SURPRISED, DISGUSTED}

    public enum Type {LIKE, LOVE, HAHA, WOW, SAD, ANGRY};

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
    private Type type;

    @NotNull
    private Date created;

    private Like() {}

    public static Like of(User user, Type type) {
        return of(null, user, type);
    }

    public static Like of(Post post, User user, Type type) {
        Like like = new Like();
        like.post = post;
        like.user = user;
        like.type = type;
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
