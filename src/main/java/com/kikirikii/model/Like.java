package com.kikirikii.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "post_likes")
public class Like {

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

    public Date getCreated() {
        return created;
    }

}
