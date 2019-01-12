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

import java.util.List;

public class Site {
    private String name;
    private String logo;
    private String style;
    private String homepage;
    private String superuser;
    private Page cover;
    private Page login;
    private Page register;

    public static Site of(String name, String logo, String style, String homepage, String superuser) {
        return of(name, logo, style, homepage, superuser, null, null, null);
    }

    public static Site of(String name, String logo, String style, String homepage, String superuser,
                          Page cover, Page login, Page register) {
        Site site = new Site();
        site.name = name;
        site.logo = logo;
        site.style = style;
        site.homepage = homepage;
        site.superuser = superuser;
        site.cover = cover;
        site.login = login;
        site.register = register;

        return site;
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
}
