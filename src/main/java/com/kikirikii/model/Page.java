/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [Page.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 08.02.19 19:52
 */

package com.kikirikii.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kikirikii.model.enums.State;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Pages are not bound to any space nor user, but site global elements. Standard pages are
 * Contact, Imprint, Terms of Use, Privacy Policy
 */

@Entity
@Table(name = "pages")
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    private String name;

    private String headline;

    @Column(columnDefinition = "text", length = 10485760)
    private String lead;

    @Column(columnDefinition = "text", length = 10485760)
    private String content;

    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "state in ('ACTIVE')")
    @OrderBy("position ASC")
    private List<PageMedia> media = new ArrayList<>();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private Page parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Where(clause = "state in ('ACTIVE')")
    @OrderBy("ranking DESC")
    private List<Page> children = new ArrayList<>();

    private int ranking;

    @Enumerated(EnumType.STRING)
    private State state;

    @NotNull
    private Date created;

    private Page() {}

    public static Page of() {
        Page page = new Page();
        return page;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getLead() {
        return lead;
    }

    public void setLead(String lead) {
        this.lead = lead;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<PageMedia> getMedia() {
        return media;
    }

    public void setMedia(List<PageMedia> media) {
        this.media.forEach(m -> m.setState(State.DELETED));
        this.media.clear();
        this.media.addAll(media);
    }

    public Page addMedia(PageMedia media) {
        this.media.add(media);
        media.setPage(this);
        return this;
    }

    public Page removeMedia(PageMedia media) {
        this.media.remove(media);
        media.setState(State.DELETED);
        return this;
    }

    public Page getParent() {
        return parent;
    }

    public void setParent(Page parent) {
        this.parent = parent;
    }

    public List<Page> getChildren() {
        return children;
    }


    /* clear instead of just overwriting to avoid hibernate engine errors */
    public void setChildren(List<Page> children) {
        this.children.forEach(child -> child.setParent(null));
        this.children.clear();
        this.children.addAll(children);
    }

    public Page addChild(Page child) {
        this.children.add(child);
        child.setParent(this);
        return this;
    }

    /* not setting child page to deleted */
    public Page removeChild(Page child) {
        this.children.remove(child);
        child.setParent(null);
        return this;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
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
}
