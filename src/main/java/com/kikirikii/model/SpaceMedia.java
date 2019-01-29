/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [SpaceMedia.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 28.01.19 15:34
 */

package com.kikirikii.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kikirikii.model.enums.MediaType;
import com.kikirikii.model.enums.State;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Space media
 */

@Entity
@Table(name = "space_media")
public class SpaceMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id")
    private Space space;

    @NotNull
    private String url;

    private String thumbnail;

    private int position;

    @Enumerated(EnumType.STRING)
    private State state;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MediaType type;

    @NotNull
    private Date created;

    protected SpaceMedia() {}

    public static SpaceMedia of(String url, MediaType type) {
        return SpaceMedia.of(null, url, type, 0);
    }

    public static SpaceMedia of(String url, MediaType type, int position) {
        return SpaceMedia.of(null, url, type, position);
    }

    public static SpaceMedia of(Space space, String url, MediaType type, int position) {
        SpaceMedia media = new SpaceMedia();
        media.space = space;
        media.url = url;
        media.type = type;
        media.position = position;
        media.state = State.ACTIVE;
        media.created = new Date();

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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
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

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public MediaType getType() {
        return type;
    }

    public void setType(MediaType type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
