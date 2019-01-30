/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [SpaceMediaRequest.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 30.01.19 17:05
 */

package com.kikirikii.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kikirikii.model.Space;
import com.kikirikii.model.SpaceMedia;
import com.kikirikii.model.enums.State;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SpaceMediaRequest {
    private SpaceMedia[] media;

    public SpaceMediaRequest() {}

    public SpaceMedia[] getMedia() {
        return media;
    }

    public void setMedia(SpaceMedia[] media) {
        this.media = media;
    }

    @SuppressWarnings("Duplicates")
    public List<SpaceMedia> getMediaAsList(Space space) {
        return media != null ? Arrays.stream(media).peek(m -> {
            if(m.getState() == null) m.setState(State.ACTIVE);
            if(m.getCreated() == null) m.setCreated(new Date());
            m.setSpace(space);
            }).sorted((o1, o2) -> o1.getPosition() > o2.getPosition() ? 1 : -1)
                .collect(Collectors.toList()) : null;
    }
}
