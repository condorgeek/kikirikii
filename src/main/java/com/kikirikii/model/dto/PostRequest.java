/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [PostRequest.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 11.12.18 18:29
 */

package com.kikirikii.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kikirikii.model.Media;
import com.kikirikii.model.Post;
import com.kikirikii.model.enums.State;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PostRequest {
    private String title;
    private String text;
    private Media[] media;

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

    public Media[] getMedia() {
        return media;
    }

    public void setMedia(Media[] media) {
        this.media = media;
    }

    @SuppressWarnings("Duplicates")
    public List<Media> getMediaAsList() {
//        int[] position = {0};

        return media != null ? Arrays.stream(media).peek(m -> {
            if(m.getState() == null) m.setState(State.ACTIVE);
            if(m.getCreated() == null) m.setCreated(new Date());
//            m.setPosition(position[0]++);
        }).collect(Collectors.toList()) : null;

    }

    private  Function<Media[], Set<Media>> asSet = media ->
            (media != null && media.length > 0) ? Arrays.stream(media).collect(Collectors.toSet()) : null;
}

