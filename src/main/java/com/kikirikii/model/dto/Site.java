/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [Site.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 09.01.19 16:14
 */

package com.kikirikii.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Site {
    private String name;
    private String logo;
    @Deprecated
    private String style;
    private String theme;
    @JsonIgnore
    private String publicpage;
    @JsonIgnore
    private String superuser;

    @JsonProperty("public")
    private Public publicmode;

    private Page cover;
    private Page login;
    private Page register;

    public static Site of(String name, String logo, String style, String theme, String publicpage, String superuser) {
        return of(name, logo, style, theme, publicpage, superuser, null, null, null, null);
    }

    public static Site of(String name, String logo, String style, String theme, String publicpage, String superuser,
                          Public publicmode, Page cover, Page login, Page register) {
        Site site = new Site();
        site.name = name;
        site.logo = logo;
        site.style = style;
        site.theme = theme;
        site.publicpage = publicpage;
        site.superuser = superuser;
        site.publicmode = publicmode;
        site.cover = cover;
        site.login = login;
        site.register = register;

        return site;
    }


    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
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

    public String getPublicpage() {
        return publicpage;
    }

    public void setPublicpage(String publicpage) {
        this.publicpage = publicpage;
    }

    public String getSuperuser() {
        return superuser;
    }

    public void setSuperuser(String superuser) {
        this.superuser = superuser;
    }

    public Page getCover() {
        return cover;
    }

    public void setCover(Page cover) {
        this.cover = cover;
    }

    public Page getLogin() {
        return login;
    }

    public void setLogin(Page login) {
        this.login = login;
    }

    public Page getRegister() {
        return register;
    }

    public void setRegister(Page register) {
        this.register = register;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public static class Page {
        private String background;
        private String title;
        private String subtitle;
        private List<String> text;
        private List<String> footer;
        @Deprecated
        private String style;

        public static Page of(String background, String title, String subtitle, List<String> text, String style,
                              List<String> footer) {
           Page page = new Page();
           page.background = background;
           page.title = title;
           page.subtitle = subtitle;
           page.text = text;
           page.style = style;
           page.footer = footer;
           return page;
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

        public List<String> getFooter() {
            return footer;
        }

        public void setFooter(List<String> footer) {
            this.footer = footer;
        }

        public List<String> getText() {
            return text;
        }

        public void setText(List<String> text) {
            this.text = text;
        }
    }

    public static class Public {
        @Deprecated
        private String homepage;
        private boolean landingpage;
        private boolean likes;
        private boolean comments;
        private boolean registration;

        public static Public of(String homepage, String landingpage, String likes, String comments, String registration) {
            Public publicmode = new Public();
            publicmode.homepage = homepage;
            publicmode.landingpage = Boolean.valueOf(landingpage);
            publicmode.likes = Boolean.valueOf(likes);
            publicmode.comments = Boolean.valueOf(comments);
            publicmode.registration = Boolean.valueOf(registration);

            return publicmode;
        }

        public String getHomepage() {
            return homepage;
        }

        public void setHomepage(String homepage) {
            this.homepage = homepage;
        }

        public Boolean isLandingpage() {
            return landingpage;
        }

        public void setLandingpage(Boolean landingpage) {
            this.landingpage = landingpage;
        }

        public boolean isLikes() {
            return likes;
        }

        public void setLikes(boolean likes) {
            this.likes = likes;
        }

        public boolean isComments() {
            return comments;
        }

        public void setComments(boolean comments) {
            this.comments = comments;
        }

        public boolean isRegistration() {
            return registration;
        }

        public void setRegistration(boolean registration) {
            this.registration = registration;
        }
    }
}
