package com.kikirikii.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts")
public class Post {
    public enum State {ACTIVE, BLOCKED, HIDDEN}

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

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Media> media = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Like> likes = new HashSet<>();

    @NotNull
    @Enumerated(EnumType.STRING)
    private State state;

    private String title;

    @Column(columnDefinition = "text", length=10485760)
    private String text;

    @NotNull
    private Date created;

    private Post() {
    }

    public static Post of(Space space, User user, String title, String text) {
        Post post = new Post();
        post.space = space;
        post.user = user;
        post.title = title;
        post.text = text;
        post.state = State.ACTIVE;
        post.created = new Date();
        return post;
    }

    public Post addMedia(Media media) {
        this.media.add(media);
        media.setPost(this);
        return this;
    }

    public Post removeMedia(Media media) {
        this.media.remove(media);
        media.setPost(null);
        return this;
    }

    public Post addLike(Like like) {
        this.likes.add(like);
        like.setPost(this);
        return this;
    }

    public Post removeLike(Like like) {
        this.likes.remove(like);
        like.setPost(null);
        return this;
    }

    public Set<Like> getLikes() {
        return likes;
    }

    public Set<Media> getMedia() {
        return media;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
