/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [SearchService.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 26.01.19 19:01
 */

package com.kikirikii.services;

import com.kikirikii.model.Space;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class SearchService {

    @Autowired
    private UserService userService;

    @Autowired
    private SpaceService spaceService;


    public List<Map<String, Object>> searchGlobalByTerm(String term, Integer size) {

        Stream<Map<String, Object>> s1 = spaceService.searchByTermAsStream(term, size)
                .filter(space -> space.getType() != Space.Type.HOME && space.getType() != Space.Type.GLOBAL)
                .map(space -> {
            String url = "/" + space.getUser().getUsername() + "/space/" + space.getId();
            return asSearchResult(space.getName(), "", url, space.getCover(), "SPACE");
        });

        Stream<Map<String, Object>> s2 = userService.searchByTermAsStream(term, size).map(user -> {
            String url = "/" + user.getUsername() + "/home";
            return asSearchResult(user.getFullname(), user.getUsername(), url, user.getAvatar(), "USER");
        });

        return Stream.concat(s1, s2).collect(Collectors.toList());
    }

    Map<String, Object> asSearchResult(String name, String username, String url, String avatar, String type) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("username", username);
        data.put("url", url);
        data.put("avatar", avatar);
        data.put("type", type);

        return data;
    }
}
