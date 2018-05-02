package com.kikirikii.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "comments")
public class Comment {

    public enum State {ACTIVE, BLOCKED, HIDDEN}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommentLike> likes = new HashSet<>();

    @NotNull
    @Enumerated(EnumType.STRING)
    private State state;

    @Column(columnDefinition = "text", length=10485760)
    private String text;

    @NotNull
    private Date created;

    private Comment(){}

    public static Comment of(Post post, User user, String text) {
        Comment comment = new Comment();
        comment.post = post;
        comment.user = user;
        comment.text = text;
        comment.state = State.ACTIVE;
        comment.created = new Date();
        return comment;
    }

    public Comment addLike(CommentLike like) {
        this.likes.add(like);
        like.setPost(this);
        return this;
    }

    public Comment removeLike(CommentLike like) {
        this.likes.remove(like);
        like.setPost(null);
        return this;
    }

    public Set<CommentLike> getLikes() {
        return likes;
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
