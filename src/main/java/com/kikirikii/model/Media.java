/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [Media.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 31.05.18 12:08
 */

package com.kikirikii.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kikirikii.model.enums.MediaType;
import com.kikirikii.model.enums.State;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Post media
 */

@Entity
@Table(name = "media")
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @NotNull
    private String url;

    private String thumbnail;

    private int position;

    @JsonIgnore
    private String username;

    @Enumerated(EnumType.STRING)
    private State state;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MediaType type;

    @NotNull
    private Date created;

    protected Media() {}

    public static Media of(String url, MediaType type) {
        return Media.of(null, url, type, 0);
    }

    public static Media of(String url, MediaType type, int position) {
        return Media.of(null, url, type, position);
    }

    public static Media of(Post post, String url, MediaType type, int position) {
        Media media = new Media();
        media.post = post;
        media.url = url;
        media.position = position;
        media.type = type;
        media.state = State.ACTIVE;
        media.created = new Date();

        return media;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public MediaType getType() {
        return type;
    }

    public void setType(MediaType type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
