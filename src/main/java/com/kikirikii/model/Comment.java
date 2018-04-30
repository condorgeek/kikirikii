package com.kikirikii.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "comments")
public class Comment {

    public enum State {ACTIVE, BLOCKED, HIDDEN}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    private State state;

    private String text;

    @NotNull
    private Date created;

    private Comment(){}

    public static Comment create(Post post, User user, String text) {
        Comment comment = new Comment();
        comment.post = post;
        comment.user = user;
        comment.text = text;
        comment.state = State.ACTIVE;
        comment.created = new Date();
        return comment;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreated() {
        return created;
    }

}
