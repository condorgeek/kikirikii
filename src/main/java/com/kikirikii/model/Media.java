package com.kikirikii.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "media")
public class Media {

    public enum Type {PICTURE, VIDEO}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @NotNull
    private String url;

    private String thumbnail;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Type type;

    private Media() {}

    public static Media of(Post post, String url, Type type) {
        Media media = new Media();
        media.post = post;
        media.url = url;
        media.type = type;
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

}
