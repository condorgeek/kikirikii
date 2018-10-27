/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [SpaceController.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 22.10.18 10:13
 */

package com.kikirikii.controllers;

import com.kikirikii.model.Member;
import com.kikirikii.model.Space;
import com.kikirikii.model.User;
import com.kikirikii.services.SpaceService;
import com.kikirikii.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Space is essentially grounded on our individual and collective self, where functionality, ornament and beauty are
 * just different names for the same thing. We explore our individual and collective self in space as a fundamental
 * way to understanding how to make things.
 *
 * Beauty emerges, in the physical world, as an inner order that is spatial. Good functionality, and the sense
 * of belonging and wellness are by-products of that.
 *
 * The quality of spaces does not come by design: it can only emerge during the process of making. Direct hands-on
 * construction is essential to making. That is where and when everything happens.
 */

@RestController
@RequestMapping("/user/{userName}")
public class SpaceController {

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private UserService userService;

    /*  active, generic and restricted spaces */
    @RequestMapping(value = "/spaces", method = RequestMethod.GET)
    public List<Space> getUserSpaces(@PathVariable String userName) {

        User user = userService.getUser(userName);
        return spaceService.getMemberOfGenericSpaces(user.getId());
    }

    @RequestMapping(value = "/space/{spaceId}/members", method = RequestMethod.GET)
    public List<Member> getSpaceMembers(@PathVariable String userName, @PathVariable Long spaceId) {
        return spaceService.getMembersBySpace(spaceId);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/space/create", method = RequestMethod.POST)
    public Map<String, Object> createSpace(@PathVariable String userName, @RequestBody Map<String, String> values) {

        User user = userService.getUser(userName);
        return spaceService.createSpaceCombined(user, values.get("name"),  values.get("description"),
                values.get("access"));
    }

    @RequestMapping(value = "/space/{spaceId}/member/{memberName}/add", method = RequestMethod.POST)
    public Member addMember(@PathVariable String userName, @PathVariable Long spaceId, @PathVariable String memberName,
                            @RequestBody Map<String, String> values) {

        User referer = userService.getUser(userName);
        User member = userService.getUser(memberName);
        Space space = spaceService.getSpace(spaceId);

        /* current user is reference - role one of 'ADMIN, MEMBER'*/
        return spaceService.addMember(space, member, referer, values.get("role"));
    }

    @RequestMapping(value = "/space/{spaceId}/delete", method = RequestMethod.DELETE)
    public Space deleteSpace(@PathVariable String userName, @PathVariable Long spaceId) {
        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);

        return null;
    }

    @RequestMapping(value = "/member/{memberId}/delete", method = RequestMethod.DELETE)
    public Member deleteMember(@PathVariable String userName, @PathVariable Long memberId) {

        User user = userService.getUser(userName);
        Member member = spaceService.getMember(memberId);

        return null;
    }

    @RequestMapping(value = "/space/cover/home", method = RequestMethod.PUT)
    public Map<String, Object> updateHomeCover(@PathVariable String userName, @RequestBody Map<String, String> values) {
        User user = userService.getUser(userName);
        Space space = userService.getHomeSpace(userName);
        space = spaceService.updateCoverPath(space, values.get("path"));

        return homeSpaceDataAsMap(space, user);
    }

    @RequestMapping(value = "/space/cover/generic/{spaceId}", method = RequestMethod.PUT)
    public Map<String, Object> updateGenericSpaceCover(@PathVariable String userName, @PathVariable Long spaceId, @RequestBody Map<String, String> values) {

        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);
        space = spaceService.updateCoverPath(space, values.get("path"));

        return genericSpaceDataAsMap(space, user);
    }

    @RequestMapping(value = "/space/home", method = RequestMethod.GET)
    public Map<String, Object> getHomeSpaceData(@PathVariable String userName) {
        User user = userService.getUser(userName);
        Space space = userService.getHomeSpace(user.getUsername());

        return homeSpaceDataAsMap(space, user);
    }

    @RequestMapping(value = "/space/generic/{spaceId}", method = RequestMethod.GET)
    public Map<String, Object> getGenericSpaceData(@PathVariable String userName, @PathVariable Long spaceId) {
        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);

        return genericSpaceDataAsMap(space, user);
    }

    private Map<String, Object> genericSpaceDataAsMap(Space space, User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("space", space);
        data.put("userdata", user.getUserData());
        data.put("members", spaceService.getMembersCount(space.getId()));
        data.put("isMember", spaceService.isMember(space.getId(), user));
        return data;
    }

    private Map<String, Object> homeSpaceDataAsMap(Space space, User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("space", space);
        data.put("userdata", user.getUserData());
        data.put("friends", userService.getFriendsCount(user.getUsername()));
        data.put("followers", userService.getFollowersCount(user.getUsername()));
        return data;
    }

    private Map<String, Object> asMap(AbstractMap.SimpleEntry<String, Object>... entries) {
        return Arrays.stream(entries).collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue()));
    }

    private BiFunction<String, Object, AbstractMap.SimpleEntry> entry = AbstractMap.SimpleEntry::new;

}
