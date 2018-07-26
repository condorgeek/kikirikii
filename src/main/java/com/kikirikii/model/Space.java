package com.kikirikii.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "spaces")
public class Space {

    public enum Type {GLOBAL, HOME, GENERIC, EVENT, SHOP, DATING, GROUP}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    private String name;

    private String cover;

    @NotNull
    private String description;

    @Enumerated(EnumType.STRING)
    private Type type;

    @NotNull
    private Date created;

    private Space() {
    }

    public static Space of(User user, String name, String description, Type type) {
        return of(user, name, null, description, type);
    }

    public static Space of(User user, String name, String cover, String description, Type type) {
        Space space = new Space();
        space.user = user;
        space.name = name;
        space.cover = cover;
        space.description = description;
        space.type = type;
        space.created = new Date();
        return space;
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
}
