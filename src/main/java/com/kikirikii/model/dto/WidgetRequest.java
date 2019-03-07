/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [WidgetRequest.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 06.02.19 11:56
 */

package com.kikirikii.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kikirikii.model.Widget;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WidgetRequest {

    private String cover;
    private String title;
    private String text;
    private String url;
    private Widget.Type type;
    private Widget.Position pos;
    private int ranking;

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

    public Widget.Position getPos() {
        return pos;
    }

    public void setPos(Widget.Position position) {
        this.pos = position;
    }

    public Widget.Type getType() {
        return type;
    }

    public void setType(Widget.Type type) {
        this.type = type;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public Widget update(Widget widget) {

        if (title != null) widget.setTitle(title);
        if (text != null) widget.setText(text);
        if (type != null) widget.setType(type);
        if (pos != null) widget.setPos(pos);

        widget.setRanking(ranking);
        widget.setUrl(url);
        widget.setCover(cover);

        return widget;
    }
}
