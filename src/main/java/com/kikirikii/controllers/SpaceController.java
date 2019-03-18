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
import com.kikirikii.model.dto.*;
import com.kikirikii.security.authorization.JwtAuthorizationToken;
import com.kikirikii.security.model.UserContext;
import com.kikirikii.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private SearchService searchService;

    @Autowired
    private WidgetService widgetService;

    @Autowired
    private PageService pageService;

    @Autowired
    private Utils utils;


    @RequestMapping(value = "/page/{pageId}/id", method = RequestMethod.GET)
    public Page getPageById(@PathVariable String userName, @PathVariable Long pageId) {
        User user = userService.getUser(userName);
        return pageService.getPage(pageId);
    }

    @RequestMapping(value = "/page/{name}", method = RequestMethod.GET)
    public Page getPageByName(@PathVariable String userName, @PathVariable String name) {
        User user = userService.getUser(userName);
        return pageService.getPage(name);
    }

    @RequestMapping(value = "/pages", method = RequestMethod.GET)
    public List<Page> getPages(@PathVariable String userName) {
        User user = userService.getUser(userName);
        return pageService.getPages();
    }

    @RequestMapping(value = "/page/{name}/create" , method = RequestMethod.PUT)
    public Page createPage(@PathVariable String userName, @RequestBody Map<String, String> values) {
        // TODO
        return null;
    }

    @RequestMapping(value = "/widgets", method = RequestMethod.GET)
    public List<Widget> getWidgets(@PathVariable String userName) {
        User user = userService.getUser(userName);
        return widgetService.getWidgets();
    }

    @RequestMapping(value = "/widgets/{position}", method = RequestMethod.GET)
    public List<Widget> getWidgets(@PathVariable String userName, @PathVariable String position) {
        User user = userService.getUser(userName);
        return widgetService.getWidgets(Widget.Position.valueOf(position));
    }

    @Secured("ROLE_SUPERUSER")
    @RequestMapping(value = "/widgets/space/create", method = RequestMethod.POST)
    public Widget createSpaceWidget(@PathVariable String userName, @RequestBody WidgetRequest values) {
        User user = userService.getUser(userName);

        Space space = spaceService.getSpace(values.getText());
        return widgetService.save(space, values.getPos(), values.getRanking());
    }

    @Secured("ROLE_SUPERUSER")
    @RequestMapping(value = "/widgets/user/create", method = RequestMethod.POST)
    public Widget createUserWidget(@PathVariable String userName, @RequestBody WidgetRequest values) {
        User user = userService.getUser(userName);

        User target = userService.getUser(values.getText());
        return widgetService.save(target, values.getPos(), values.getRanking());
    }

    @Secured("ROLE_SUPERUSER")
    @RequestMapping(value = "/widgets/text/create", method = RequestMethod.POST)
    public Widget createTextWidget(@PathVariable String userName, @RequestBody WidgetRequest values) {
        User user = userService.getUser(userName);

        return widgetService.save(values.getUrl(), values.getCover(), values.getTitle(), values.getText(),
                values.getPos(), values.getRanking());
    }

    @Secured("ROLE_SUPERUSER")
    @RequestMapping(value = "/widgets/{widgetId}/delete", method = RequestMethod.DELETE)
    public Widget deleteWidget(@PathVariable String userName, @PathVariable Long widgetId) {
        User user = userService.getUser(userName);
        Widget widget = widgetService.getWidget(widgetId);

        return widgetService.delete(widget);
    }

    @Secured("ROLE_SUPERUSER")
    @RequestMapping(value = "/widgets/{widgetId}/update", method = RequestMethod.POST)
    public Widget updateWidget(@PathVariable String userName, @PathVariable Long widgetId,
                               @RequestBody WidgetRequest values) {

        User user = userService.getUser(userName);
        Widget widget = widgetService.getWidget(widgetId);

        return widgetService.update(widget, values);
    }

    /*  active, generic|shop|event and restricted spaces */
    @RequestMapping(value = "/spaces/{spaceType}", method = RequestMethod.GET)
    public List<Space> getUserSpaces(@PathVariable String userName, @PathVariable String spaceType) {

        User user = userService.getUser(userName);
        return spaceService.getMemberOfSpacesByType(spaceType, user.getId());
    }

    // TODO is this API still relevant ?

    @SuppressWarnings("Duplicates")
    @RequestMapping(value = "/spaces/*", method = RequestMethod.GET)
    public Map<String, List<Space>> getAnyUserSpaces(@PathVariable String userName) {

        User user = userService.getUser(userName);
        List<Space> generic = spaceService.getMemberOfSpacesByType(Space.Type.GENERIC, user.getId());
        List<Space> events = spaceService.getMemberOfSpacesByType(Space.Type.EVENT, user.getId());
        List<Space> shops = spaceService.getMemberOfSpacesByType(Space.Type.SHOP, user.getId());

        return utils.spacesAsMap(generic, events, shops);
    }

    @RequestMapping(value = "/spaces/ranking", method = RequestMethod.PUT)
    public List<Space> reorderSpacesRanking(@PathVariable String userName, @RequestBody RankingRequest ranking) {
        User user = userService.getUser(userName);
        return spaceService.reorderRanking(ranking);
    }

    @RequestMapping(value = "/spaces/{spaceId}/assign")
    public Space addSpaceChildren(@PathVariable String userName, @PathVariable Long spaceId,
                                           @RequestBody ChildrenRequest childrenRequest) {
        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);

        return spaceService.addChildren(space, childrenRequest.getChildren());
    }

    @RequestMapping(value = "/space/{spaceId}/members", method = RequestMethod.GET)
    public List<Member> getSpaceMembers(@PathVariable String userName, @PathVariable Long spaceId) {
        return spaceService.getMembersBySpace(spaceId);
    }

    @RequestMapping(value = "/space/{spaceId}/members/{page}/{size}", method = RequestMethod.GET)
    public org.springframework.data.domain.Page<Member> getPageableSpaceMembers(@PathVariable String userName, @PathVariable Long spaceId,
                                                                                @PathVariable Integer page, @PathVariable Integer size ) {
        return spaceService.getPageableMembersBySpace(spaceId, page, size);
    }

    /* no '/space' in this one */
    @RequestMapping(value = "/search/{term}/{size}", method = RequestMethod.GET)
    public List<Map<String, Object>> searchByTerm(@PathVariable String userName, @PathVariable String term,
                                            @PathVariable Integer size) {
        return searchService.searchGlobalByTerm(term, size);
    }

    @SuppressWarnings("unchecked")
    @Secured("ROLE_USER")
    @RequestMapping(value = "/space/{spaceType}/create", method = RequestMethod.POST)
    public Space createSpace(@PathVariable String userName, @PathVariable String spaceType, @RequestBody Map<String, String> values) {

        User user = userService.getUser(userName);

        if(Space.Type.valueOf(spaceType) == Space.Type.EVENT) {
            return spaceService.createSpaceAndJoin(user, spaceType, values.get("name"),  values.get("description"),
                    values.get("access"), values.get("start"), values.get("end"));
        } else {
            return spaceService.createSpaceAndJoin(user, spaceType, values.get("name"),  values.get("description"),
                    values.get("access"));
        }
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/space/{spaceId}/update", method = RequestMethod.POST)
    public Space updateSpace(@PathVariable String userName, @PathVariable Long spaceId,
                             @RequestBody SpaceRequest spaceRequest) {
        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);

        return  spaceService.save(spaceRequest.update(space));
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/space/{spaceId}/member/{memberName}/add", method = RequestMethod.POST)
    public Member addMember(@PathVariable String userName, @PathVariable Long spaceId, @PathVariable String memberName,
                            @RequestBody Map<String, String> values) {

        User referer = userService.getUser(userName);
        User member = userService.getUser(memberName);
        Space space = spaceService.getSpace(spaceId);

        /* current user is reference - role one of 'ADMIN, MEMBER'*/
        return spaceService.addMember(space, member, referer, values.get("role"));
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/space/{spaceId}/join", method = RequestMethod.POST)
    public Member joinSpace(@PathVariable String userName, @PathVariable Long spaceId) {

        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);

        return spaceService.addMember(space, user, user, "MEMBER");
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/space/{spaceId}/share/{postId}", method = RequestMethod.POST)
    public Post sharePost(@PathVariable String userName, @PathVariable Long spaceId, @PathVariable Long postId, @RequestBody Map<String, String> values) {

        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);
        Post post = userService.getPostById(postId);
        String comment = values.get("comment");

        return userService.sharePost(space, user, post, comment);
    }

    /* member itself leaves space */
    @Secured("ROLE_USER")
    @RequestMapping(value = "/space/{spaceId}/leave/{memberId}", method = RequestMethod.POST)
    public Member leaveSpaceById(@PathVariable String userName, @PathVariable Long spaceId, @PathVariable Long memberId) {

        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);
        Member member = spaceService.getMember(memberId);

        return spaceService.leaveSpace(space, member);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/space/{spaceId}/leave", method = RequestMethod.POST)
    public Member leaveSpace(@PathVariable String userName, @PathVariable Long spaceId) {

        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);
        Member member = spaceService.getMember(spaceId, userName);

        return spaceService.leaveSpace(space, member);
    }

    /* owner ie. admin removes some member from space */
    @Secured("ROLE_USER")
    @RequestMapping(value = "space/{spaceId}/delete/{memberId}", method = RequestMethod.DELETE)
    public Member deleteMember(@PathVariable String userName, @PathVariable Long spaceId, @PathVariable Long memberId) {

        Space space = spaceService.getSpace(spaceId);
        Member admin = spaceService.findMember(spaceId, userName);
        Member member = spaceService.getMember(memberId);

        return spaceService.deleteMember(space, admin, member);
    }

    /* owner ie. admin blocks some member from space */
    @Secured("ROLE_USER")
    @RequestMapping(value = "space/{spaceId}/block/{memberId}", method = RequestMethod.DELETE)
    public Member blockMember(@PathVariable String userName, @PathVariable Long spaceId, @PathVariable Long memberId) {

        Space space = spaceService.getSpace(spaceId);
        Member admin = spaceService.findMember(spaceId, userName);
        Member member = spaceService.getMember(memberId);

        return spaceService.blockMember(space, admin, member);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/space/{spaceId}/delete", method = RequestMethod.DELETE)
    public Space deleteSpace(@PathVariable String userName, @PathVariable Long spaceId) {
        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);

        return spaceService.deleteSpace(space);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/space/{spaceId}/block", method = RequestMethod.PUT)
    public Space blockSpace(@PathVariable String userName, @PathVariable Long spaceId) {
        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);

        return spaceService.blockSpace(space);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/space/{spaceId}/unblock", method = RequestMethod.PUT)
    public Space unblockSpace(@PathVariable String userName, @PathVariable Long spaceId) {
        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);

        return spaceService.unblockSpace(space);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/space/cover/home", method = RequestMethod.PUT)
    public Map<String, Object> updateHomeCover(Principal principal, @PathVariable String userName, @RequestBody Map<String, String> values) {
        User user = userService.getUser(userName);
        Space space = userService.getHomeSpace(userName);
        space = spaceService.updateCoverPath(space, values.get("path"));

        return utils.homeSpaceDataAsMap(space, user, principal.getName());
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/space/cover/generic/{spaceId}", method = RequestMethod.PUT)
    public Map<String, Object> updateGenericSpaceCover(@PathVariable String userName, @PathVariable Long spaceId, @RequestBody Map<String, String> values) {

        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);
        space = spaceService.updateCoverPath(space, values.get("path"));

        return utils.genericSpaceDataAsMap(space, user);
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

        return utils.homeSpaceDataAsMap(space, user, principal.getName());
    }

    @RequestMapping(value = "/space/generic/{spaceId}", method = RequestMethod.GET)
    public Map<String, Object> getGenericSpaceData(@PathVariable String userName, @PathVariable Long spaceId) {
        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);

        return utils.genericSpaceDataAsMap(space, user);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/media/add/home", method = RequestMethod.POST)
    public Space addHomeSpaceMedia(@PathVariable String userName, @RequestBody SpaceMediaRequest request) {
        User user = userService.getUser(userName);
        Space space = userService.getHomeSpace(userName);

        space.setMedia(request.getMediaAsList(space));
        return spaceService.save(space);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/media/add/generic/{spaceId}", method = RequestMethod.POST)
    public Space addGenericSpaceMedia(@PathVariable String userName, @PathVariable Long spaceId, @RequestBody SpaceMediaRequest request) {
        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);

        space.setMedia(request.getMediaAsList(space));
        return spaceService.save(space);
    }

    private Map<String, Object> asMap(AbstractMap.SimpleEntry<String, Object>... entries) {
        return Arrays.stream(entries).collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue()));
    }

    private BiFunction<String, Object, AbstractMap.SimpleEntry> entry = AbstractMap.SimpleEntry::new;
    private Function<Principal, UserContext> getUserContext =  principal -> ((JwtAuthorizationToken) principal).getUserContext();

}
