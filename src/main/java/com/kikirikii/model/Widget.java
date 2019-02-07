/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [Widget.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 05.02.19 19:51
 */

package com.kikirikii.model;

import com.kikirikii.model.enums.State;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Widgets are system wide ie for the site and not dependant on any user or space.
 * Actually only superuser should create Widgets since they affect the look and feel for all.
 * Widgets will be sorted by ranking and created date
 */
@Entity
@Table(name = "widgets")
public class Widget {

    public enum Position {
        LTOP, LBOTTOM, RTOP, RBOTTOM, MTOP, MBOTTOM
    }

    public enum Type {TEXT, SPACE, USER, CUSTOM, EXTERNAL}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String cover;

    private String title;

    @Column(columnDefinition = "text", length = 10485760)
    private String text;

    private String url;

    @Enumerated(EnumType.STRING)
    private State state;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Position pos;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Type type;

    private int ranking;

    @NotNull
    private Date created;

    private Widget() {
    }

    public static Widget of(User user, Position pos, int ranking) {
        String url = user.getUsername() + "/home";

        return of(url, user.getAvatar(), user.getFullname(), user.getUserData().getAboutYou(),
                pos, Type.USER, ranking);
    }

    public static Widget of(Space space, Position pos, int ranking) {
        String url = space.getUser().getUsername() + "/space/" + space.getId();
        return of(url, space.getCover(), space.getName(), space.getDescription(),
                pos, Type.SPACE, ranking);
    }

    public static Widget of(String url, String cover, String title, String text, Position pos, int ranking) {
        return of(url, cover, title, text, pos, Type.TEXT, ranking);
    }

    public static Widget of(String url, String cover, String title, String text, Position pos, Type type, int ranking) {
        Widget widget = new Widget();
        widget.url = url;
        widget.cover = cover;
        widget.type = type;
        widget.title = title;
        widget.text = text;
        widget.pos = pos;
        widget.ranking = ranking;
        widget.state = State.ACTIVE;
        widget.created = new Date();
        return widget;
    }

    public long getId() {
        return id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }


    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Position getPos() {
        return pos;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }
}
