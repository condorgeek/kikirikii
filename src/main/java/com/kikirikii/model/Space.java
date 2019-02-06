/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [Space.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 27.07.18 14:04
 */

package com.kikirikii.model;

import com.kikirikii.model.enums.State;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * The quality of spaces does not come by design: it can only emerge during the process of making. We experience
 * beauty in space when we see that everything around has arisen by careful choice and restless consideration of
 * both the place and our own self. We are interested in the process of fine-tuning that creates a place:
 * in a short-term "project" scenario, and in the longer-term, and truly "evolutionary."
 *
 * public access - anonymous, automatic joining
 * restricted access - by invitation, needs confirmation
 *      JOIN REQUEST -> PENDING -> CONFIRMED
 */

@Entity
@Table(name = "spaces")
public class Space {

    public enum Type {GLOBAL, HOME, GENERIC, EVENT, SHOP, DATING}
    public enum Access {PUBLIC, RESTRICTED, PRIVATE}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

//    @JsonProperty("owner")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    private String name;

    private String cover;

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "state in ('ACTIVE')")
    @OrderBy("position ASC")
    private List<SpaceMedia> media = new ArrayList<>();

    private String icon;

    @Column(columnDefinition = "text", length = 10485760)
    private String description;

    @OneToOne(mappedBy = "space", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private SpaceData spacedata;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Type type;

    @NotNull
    @Enumerated(EnumType.STRING)
    private State state;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Access access;

    private int ranking;

    @NotNull
    private Date created;

    protected Space() {
    }

    public static Space of(User user, String name, String description, Type type) {
        return of(user, name, null, null, null, description, type, Access.PUBLIC);
    }

    public static Space of(User user, String name, String description, Type type, Access access) {
        return of(user, name, null, null, null, description, type, access);
    }

    public static Space of(User user, String name, String cover, List<SpaceMedia> media, String icon, String description, Type type,
                           Access access) {
        Space space = new Space();
        space.user = user;
        space.name = name;
        space.cover = cover;
        if(media != null) {
            space.media = media;
            space.media.forEach(m -> m.setSpace(space));
        } else {
            space.media = new ArrayList<>();
        }
        space.description = description;
        space.type = type;
        space.icon = icon;
        space.access = access;
        space.ranking = 0;
        space.state = State.ACTIVE;
        space.created = new Date();

        return space;
    }

    public Space addMedia(SpaceMedia media) {
        this.media.add(media);
        media.setSpace(this);
        return this;
    }

    public Space removeMedia(SpaceMedia media) {
        this.media.remove(media);
        media.setState(State.DELETED);
        return this;
    }

    public List<SpaceMedia> getMedia() {
        return media;
    }

    /* clear instead of just overwriting to avoid hibernate engine errors */
    public void setMedia(List<SpaceMedia> media) {
        this.media.clear();
        this.media.addAll(media);
    }

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public SpaceData getSpacedata() {
        return spacedata;
    }

    public void setSpacedata(SpaceData spacedata) {
        spacedata.setSpace(this);
        this.spacedata = spacedata;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }
}
