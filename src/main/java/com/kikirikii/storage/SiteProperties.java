/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [ConfigurationProperties.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 09.01.19 14:46
 */

package com.kikirikii.storage;

import com.kikirikii.model.dto.Site;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@PropertySource("classpath:site.properties")
public class SiteProperties {

    @Autowired
    private Environment env;

    public Site getSiteConfiguration() {
        return Site.of(prop.get("name"), prop.get("logo"), prop.get("style"), prop.get("homepage"), prop.get("superuser"),
                Site.Page.of(cover.get("background"), cover.get("title"), cover.get("subtitle"),
                        asList("client.config.cover.text"), cover.get("style"),
                        asList("client.config.cover.footer")),
                Site.Page.of(login.get("background"), login.get("title"), login.get("subtitle"),
                        asList("client.config.cover.text"), login.get("style"),
                        asList("client.config.login.footer")),
                Site.Page.of(register.get("background"), register.get("title"), register.get("subtitle"),
                        asList("client.config.cover.text"), register.get("style"),
                        asList("client.config.register.footer"))
        );
    }

    private List<String> asList(String base) {
        String result;
        List<String> list = new ArrayList<>();
        for(int k = 0; (result = env.getProperty(base + "." + k)) != null; k++) {
            list.add(result);
        }
        return  list;
    }

    @FunctionalInterface
    interface Getter <T> { T get(String key); }

    private Getter<String> prop = name -> env.getProperty("client.config." + name);
    private Getter<String> cover = name -> env.getProperty("client.config.cover." + name);
    private Getter<String> login = name -> env.getProperty("client.config.login." + name);
    private Getter<String> register = name -> env.getProperty("client.config.register." + name);
//    private Getter<List<String>> asList = name -> env.getProperty("client.config.cover." + name, List.class);

}
