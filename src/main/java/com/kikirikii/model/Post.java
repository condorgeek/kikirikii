/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [Post.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 26.05.18 14:31
 */

package com.kikirikii.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts")
public class Post {
    public enum State {ACTIVE, BLOCKED, DELETED, HIDDEN, SHARED}

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
    @Where(clause = "state in ('ACTIVE', 'SHARED')")
    private Set<Media> media = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "state = 'ACTIVE'")
    private Set<Like> likes = new HashSet<>();

    @NotNull
    @Enumerated(EnumType.STRING)
    private State state;

    private String title;

    @Column(columnDefinition = "text", length = 10485760)
    private String text;

    @NotNull
    private Date created;

    private Post() {
    }

    public static Post of(Space space, User user, String title, String text) {
        return of(space, user, title, text, null);
    }

    public static Post of(Space space, User user, String title, String text, Set<Media> media) {
        Post post = new Post();
        post.space = space;
        post.user = user;
        post.title = title;
        post.text = text;
        post.media = (media == null) ? new HashSet<>() : media;
        post.state = State.ACTIVE;
        post.created = new Date();

        post.media.forEach(m->m.setPost(post));
        return post;
    }

    public Post addMedia(Media media) {
        this.media.add(media);
        media.setPost(this);
        return this;
    }

    public Post removeMedia(Media media) {
        this.media.remove(media);
//        media.setPost(null);
        media.setState(Media.State.DELETED);
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

    /* soft delete - requires re-reading like list */
    public Post deleteLike(Like like) {
        like.setState(Like.State.DELETED);
        return this;
    }

    public Set<Like> getLikes() {
        return likes;
    }

    public Set<Media> getMedia() {
        return media;
    }

    public void setMedia(Set<Media> media) {
        this.media = media;
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
