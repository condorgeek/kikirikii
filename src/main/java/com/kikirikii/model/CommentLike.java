package com.kikirikii.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "comment_likes")
public class CommentLike {

    public enum Type {HAPPY, ANGRY, SAD, SCARED, SURPRISED, DISGUSTED}

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Post post;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Type type;

    @NotNull
    private Date created;

    private CommentLike() {}

    public static CommentLike of(Post post, User user, Type type) {
        CommentLike like = new CommentLike();
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
