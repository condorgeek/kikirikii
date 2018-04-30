package com.kikirikii.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "posts")
public class Post {

    public enum MediaType {IMAGE, VIDEO}
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

    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private State state;

    private String title;

    private String text;

    @NotNull
    private Date created;

    private Post() {
    }

    public static Post create(Space space, User user, String mediaUrl, MediaType mediaType, String title, String text) {
        Post post = new Post();
        post.space = space;
        post.user = user;
        post.mediaUrl = mediaUrl;
        post.mediaType = mediaType;
        post.title = title;
        post.text = text;
        post.state = State.ACTIVE;
        post.created = new Date();
        return post;
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

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
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

    public Date getCreated() {
        return created;
    }

}
