/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [Utils.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 15.01.19 14:04
 */

package com.kikirikii.controllers;

import com.kikirikii.model.Friend;
import com.kikirikii.model.Member;
import com.kikirikii.model.Space;
import com.kikirikii.model.User;
import com.kikirikii.services.SpaceService;
import com.kikirikii.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Utils {

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private UserService userService;

    /* alternative - create a class for this stuff */
    @SuppressWarnings("Duplicates")
    Map<String, Object> genericSpaceDataAsMap(Space space, User user) {
        Member member = spaceService.findMember(space.getId(), user);

        Map<String, Object> data = new HashMap<>();
        data.put("space", space);
        data.put("spacedata", space.getSpacedata());
        data.put("userdata", user.getUserData());
        data.put("members", spaceService.getMembersCount(space.getId()));
        data.put("isMember", member != null);
        data.put("member", member);

        return data;
    }

    /* alternative - create a class for this stuff */
    /* ACHTUNG: principal is the username of the logged in, authenticated user */
    @SuppressWarnings("Duplicates")
    Map<String, Object> homeSpaceDataAsMap(Space space, User user, String principal) {

        Friend friend = null;
        boolean isOwner = false;
        boolean isFriend = false;
        boolean isFollowee = false;

        if (principal != null) {
            friend = userService.getFriend(principal, user);
            isOwner = user.getUsername().equals(principal);
            isFriend = isOwner || friend != null;
            isFollowee = isOwner || userService.isFollowee(principal, user);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("space", space);
        data.put("spacedata", space.getSpacedata());
        data.put("userdata", user.getUserData());
        data.put("friends", userService.getFriendsCount(user.getUsername()));
        data.put("followers", userService.getFollowersCount(user.getUsername()));
        data.put("isFriend", isFriend);
        data.put("friend", friend);
        data.put("isFollowee", isFollowee);
        data.put("isOwner", isOwner);

        return data;

    }

    /* alternative - create a class for this stuff */
    Map<String, List<Space>> spacesAsMap(List<Space> generic, List<Space> events, List<Space> shops) {
        Map<String, List<Space>> data = new HashMap<>();
        data.put("generic", generic);
        data.put("events", events);
        data.put("shops", shops);

        return data;
    }

    /* alternative - create a class for this stuff */
    Map<String, Object> userDataAsMap(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("userdata", user.getUserData());

        return data;
    }

}
