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

import com.kikirikii.model.*;
import com.kikirikii.model.dto.SpaceRequest;
import com.kikirikii.security.authorization.JwtAuthorizationToken;
import com.kikirikii.security.model.UserContext;
import com.kikirikii.services.SpaceService;
import com.kikirikii.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Logger;
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
    public static Logger logger = Logger.getLogger("SpaceController");


    @Autowired
    private SpaceService spaceService;

    @Autowired
    private UserService userService;

    /*  active, generic|shop|event and restricted spaces */
    @RequestMapping(value = "/spaces/{spaceType}", method = RequestMethod.GET)
    public List<Space> getUserSpaces(@PathVariable String userName, @PathVariable String spaceType) {

        User user = userService.getUser(userName);
        return spaceService.getMemberOfSpacesByType(spaceType, user.getId());
    }

    @RequestMapping(value = "/spaces/*", method = RequestMethod.GET)
    public Map<String, List<Space>> getAnyUserSpaces(@PathVariable String userName) {

        User user = userService.getUser(userName);
        List<Space> generic = spaceService.getMemberOfSpacesByType(Space.Type.GENERIC, user.getId());
        List<Space> events = spaceService.getMemberOfSpacesByType(Space.Type.EVENT, user.getId());
        List<Space> shops = spaceService.getMemberOfSpacesByType(Space.Type.SHOP, user.getId());

        return spacesAsMap(generic, events, shops);
    }

    @RequestMapping(value = "/space/{spaceId}/members", method = RequestMethod.GET)
    public List<Member> getSpaceMembers(@PathVariable String userName, @PathVariable Long spaceId) {
        return spaceService.getMembersBySpace(spaceId);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/space/{spaceType}/create", method = RequestMethod.POST)
    public Space createSpace(@PathVariable String userName, @PathVariable String spaceType, @RequestBody Map<String, String> values) {

        User user = userService.getUser(userName);
        return spaceService.createSpaceCombined(user, spaceType, values.get("name"),  values.get("description"),
                values.get("access"));
    }

    @RequestMapping(value = "/space/{spaceId}/update", method = RequestMethod.POST)
    public Space updateSpace(@PathVariable String userName, @PathVariable Long spaceId,
                             @RequestBody SpaceRequest spaceRequest) {

        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);

        return  spaceService.save(spaceRequest.update(space));
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

    @RequestMapping(value = "/space/{spaceId}/join", method = RequestMethod.POST)
    public Member joinSpace(@PathVariable String userName, @PathVariable Long spaceId) {

        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);

        return spaceService.addMember(space, user, user, "MEMBER");
    }

    @RequestMapping(value = "/space/{spaceId}/share/{postId}", method = RequestMethod.POST)
    public Post sharePost(@PathVariable String userName, @PathVariable Long spaceId, @PathVariable Long postId, @RequestBody Map<String, String> values) {

        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);
        Post post = userService.getPostById(postId);
        String comment = values.get("comment");

        return userService.sharePost(space, user, post, comment);
    }

    /* member itself leaves space */
    @RequestMapping(value = "/space/{spaceId}/leave/{memberId}", method = RequestMethod.POST)
    public Member leaveSpaceById(@PathVariable String userName, @PathVariable Long spaceId, @PathVariable Long memberId) {

        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);
        Member member = spaceService.getMember(memberId);

        return spaceService.leaveSpace(space, member);
    }

    @RequestMapping(value = "/space/{spaceId}/leave", method = RequestMethod.POST)
    public Member leaveSpace(@PathVariable String userName, @PathVariable Long spaceId) {

        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);
        Member member = spaceService.getMember(spaceId, userName);

        return spaceService.leaveSpace(space, member);
    }

    /* owner ie. admin removes some member from space */
    @RequestMapping(value = "space/{spaceId}/delete/{memberId}", method = RequestMethod.DELETE)
    public Member deleteMember(@PathVariable String userName, @PathVariable Long spaceId, @PathVariable Long memberId) {

        Space space = spaceService.getSpace(spaceId);
        Member admin = spaceService.findMember(spaceId, userName);
        Member member = spaceService.getMember(memberId);

        return spaceService.deleteMember(space, admin, member);
    }

    /* owner ie. admin blocks some member from space */
    @RequestMapping(value = "space/{spaceId}/block/{memberId}", method = RequestMethod.DELETE)
    public Member blockMember(@PathVariable String userName, @PathVariable Long spaceId, @PathVariable Long memberId) {

        Space space = spaceService.getSpace(spaceId);
        Member admin = spaceService.findMember(spaceId, userName);
        Member member = spaceService.getMember(memberId);

        return spaceService.blockMember(space, admin, member);
    }

    @RequestMapping(value = "/space/{spaceId}/delete", method = RequestMethod.DELETE)
    public Space deleteSpace(@PathVariable String userName, @PathVariable Long spaceId) {
        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);

        return spaceService.deleteSpace(space);
    }

    @RequestMapping(value = "/space/{spaceId}/block", method = RequestMethod.PUT)
    public Space blockSpace(@PathVariable String userName, @PathVariable Long spaceId) {
        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);

        return spaceService.blockSpace(space);
    }

    @RequestMapping(value = "/space/{spaceId}/unblock", method = RequestMethod.PUT)
    public Space unblockSpace(@PathVariable String userName, @PathVariable Long spaceId) {
        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);

        return spaceService.unblockSpace(space);
    }

    @RequestMapping(value = "/space/cover/home", method = RequestMethod.PUT)
    public Map<String, Object> updateHomeCover(Principal principal, @PathVariable String userName, @RequestBody Map<String, String> values) {
        User user = userService.getUser(userName);
        Space space = userService.getHomeSpace(userName);
        space = spaceService.updateCoverPath(space, values.get("path"));

        return homeSpaceDataAsMap(space, user, principal);
    }

    @RequestMapping(value = "/space/cover/generic/{spaceId}", method = RequestMethod.PUT)
    public Map<String, Object> updateGenericSpaceCover(@PathVariable String userName, @PathVariable Long spaceId, @RequestBody Map<String, String> values) {

        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);
        space = spaceService.updateCoverPath(space, values.get("path"));

        return genericSpaceDataAsMap(space, user);
    }

    /* userName is the id of the home page to get the data for!! and is not the id of the authorized
     * user, as implicit for other user and space requests. Further, Principal is always an instance of
     * JwtAuthorizationToken  */
    @RequestMapping(value = "/space/home", method = RequestMethod.GET)
    public Map<String, Object> getHomeSpaceData(Principal principal, @PathVariable String userName) {

        UserContext userContext = getUserContext.apply(principal);
        logger.info("getHomeSpaceData: " + principal.getName() + " for " + userName + " login(" + userContext.getUsername() +")");

        User user = userService.getUser(userName);
        Space space = userService.getHomeSpace(user.getUsername());

        return homeSpaceDataAsMap(space, user, principal);
    }

    @RequestMapping(value = "/space/generic/{spaceId}", method = RequestMethod.GET)
    public Map<String, Object> getGenericSpaceData(@PathVariable String userName, @PathVariable Long spaceId) {
        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);

        return genericSpaceDataAsMap(space, user);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> genericSpaceDataAsMap(Space space, User user) {
        Member member = spaceService.findMember(space.getId(), user);

        return new DataMap<String, Object>().put("space", space)
                .put("spacedata", space.getSpacedata())
                .put("userdata", user.getUserData())
                .put("members", spaceService.getMembersCount(space.getId()))
                .put("isMember", member != null)
                .put("member", member)
                .get();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> homeSpaceDataAsMap(Space space, User user, Principal principal) {

        Friend friend = userService.getFriend(principal.getName(), user);
        boolean isOwner = user.getUsername().equals(principal.getName());
        boolean isFriend = isOwner || friend != null;
        boolean isFollowee = isOwner || userService.isFollowee(principal.getName(), user);

        return new DataMap<String, Object>().put("space", space)
                .put("spacedata", space.getSpacedata())
                .put("userdata", user.getUserData())
                .put("friends", userService.getFriendsCount(user.getUsername()))
                .put("followers", userService.getFollowersCount(user.getUsername()))
                .put("isFriend", isFriend)
                .put("friend", friend)
                .put("isFollowee", isFollowee)
                .put("isOwner", isOwner)
                .get();
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<Space>> spacesAsMap(List<Space> generic, List<Space> events, List<Space> shops) {

        return new DataMap<String, List<Space>>().put("generic", generic)
                .put("events", events)
                .put("shops", shops)
                .get();
    }

    private class DataMap <K, O>{
        private Map<K, O> map = new HashMap<>();

        DataMap() {}
        DataMap put(K key, O data) {
            this.map.put(key, data);
            return this;
        }
        Map<K, O> get() {
            return map;
        }
    }

    private Map<String, Object> asMap(AbstractMap.SimpleEntry<String, Object>... entries) {
        return Arrays.stream(entries).collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue()));
    }

    private BiFunction<String, Object, AbstractMap.SimpleEntry> entry = AbstractMap.SimpleEntry::new;
    private Function<Principal, UserContext> getUserContext =  principal -> ((JwtAuthorizationToken) principal).getUserContext();

}
