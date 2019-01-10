/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [Client.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 09.01.19 16:14
 */

package com.kikirikii.model.dto;

import java.util.List;

public class Client {
    private String name;
    private String logo;
    private String style;
    private String homepage;
    private String superuser;
    private Cover cover;

    public static Client of(String name, String logo, String style, String homepage, String superuser, Cover cover) {
        Client client = new Client();
        client.name = name;
        client.logo = logo;
        client.style = style;
        client.homepage = homepage;
        client.superuser = superuser;
        client.cover = cover;

        return client;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getSuperuser() {
        return superuser;
    }

    public void setSuperuser(String superuser) {
        this.superuser = superuser;
    }

    public Cover getCover() {
        return cover;
    }

    public void setCover(Cover cover) {
        this.cover = cover;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public static class Cover {
        private String background;
        private String title;
        private String subtitle;
        private List<String> text;
        private String footer;
        private String style;

        public static Cover of(String background, String title, String subtitle, List<String> text, String style, String footer) {
           Cover cover = new Cover();
           cover.background = background;
           cover.title = title;
           cover.subtitle = subtitle;
           cover.text = text;
           cover.style = style;
           cover.footer = footer;
           return cover;
        }

        public String getBackground() {
            return background;
        }

        public void setBackground(String background) {
            this.background = background;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public String getStyle() {
            return style;
        }

        public void setStyle(String style) {
            this.style = style;
        }

        public String getFooter() {
            return footer;
        }

        public void setFooter(String footer) {
            this.footer = footer;
        }

        public List<String> getText() {
            return text;
        }

        public void setText(List<String> text) {
            this.text = text;
        }
    }
}
