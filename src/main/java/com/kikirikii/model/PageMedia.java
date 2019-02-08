/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [PageMedia.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 28.01.19 20:06
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
@Table(name = "page_media")
public class PageMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id")
    private Page page;

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

    protected PageMedia() {}

    public static PageMedia of(String url, MediaType type) {
        return PageMedia.of(null, url, type, 0);
    }

    public static PageMedia of(String url, MediaType type, int position) {
        return PageMedia.of(null, url, type, position);
    }

    public static PageMedia of(Page page, String url, MediaType type, int position) {
        PageMedia media = new PageMedia();
        media.page = page;
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

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
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
