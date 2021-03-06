/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [PublicController.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 15.01.19 13:17
 */

package com.kikirikii.controllers;

import com.kikirikii.exceptions.InvalidResourceException;
import com.kikirikii.exceptions.NotFoundException;
import com.kikirikii.model.*;
import com.kikirikii.services.*;
import com.kikirikii.storage.SiteProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Support for anonymous user. Read mode only of PUBLIC data. {userName} is the default user which
 * will be used as home for the anonymous public mode. See site configuration ie. site.properties.
 */

@RestController
@RequestMapping("/public/{userName}")
public class PublicController {
    public static Logger logger = Logger.getLogger("PublicController");

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private SiteProperties configuration;

    @Autowired
    private SearchService searchService;

    @Autowired
    private WidgetService widgetService;

    @Autowired
    private PageService pageService;

    @Autowired
    private Utils utils;

    /**********************
     * PAGE entry points *
     **********************/

    @RequestMapping(value = "/page/{pageId}/id", method = RequestMethod.GET)
    public com.kikirikii.model.Page getPageById(@PathVariable String userName, @PathVariable Long pageId) {
        User user = resolvePublicUser(userName);
        return pageService.getPage(pageId);
    }

    @RequestMapping(value = "/page/{name}", method = RequestMethod.GET)
    public com.kikirikii.model.Page getPageByName(@PathVariable String userName, @PathVariable String name) {
        User user = resolvePublicUser(userName);
        return pageService.getPage(name);
    }

    @RequestMapping(value = "/pages", method = RequestMethod.GET)
    public List<com.kikirikii.model.Page> getPages(@PathVariable String userName) {
        User user = resolvePublicUser(userName);
        return pageService.getPages();
    }

    /**********************
     * WIDGET entry points *
     **********************/

    @RequestMapping(value = "/widgets", method = RequestMethod.GET)
    public List<Widget> getWidgets(@PathVariable String userName) {
        User user = resolvePublicUser(userName);
        return widgetService.getWidgets();
    }

    @RequestMapping(value = "/widgets/{position}", method = RequestMethod.GET)
    public List<Widget> getWidgets(@PathVariable String userName, @PathVariable String position) {
        User user = resolvePublicUser(userName);
        return widgetService.getWidgets(Widget.Position.valueOf(position));
    }

    /**********************
     * USER entry points *
     **********************/

    @RequestMapping(value = "/posts/global", method = RequestMethod.GET)
    List<Post> getUserGlobalPosts(@PathVariable String userName) {
        User user = resolvePublicUser(userName);
        return userService.getUserGlobalPosts(user);
    }

    @RequestMapping(value = "/posts/home", method = RequestMethod.GET)
    List<Post> getUserHomePosts(@PathVariable String userName) {
        User user = resolvePublicUser(userName);
        return userService.getUserHomePosts(user);
    }

    @RequestMapping(value = "/posts/home/page/{page}/{size}", method = RequestMethod.GET)
    Page<Post> getPageableHomePosts(@PathVariable String userName, @PathVariable Integer page, @PathVariable Integer size) {
        User user = resolvePublicUser(userName);
        return userService.getPageableHomePosts(user, page, size);
    }

    @RequestMapping(value = "/posts/media/home", method = RequestMethod.GET)
    List<Media> getUserHomeMedia(@PathVariable String userName) {
        User user = resolvePublicUser(userName);
        Space space = userService.getHomeSpace(user.getUsername());

        return userService.getUserSpaceMedia(user, space);
    }

    @RequestMapping(value = "/posts/media/generic/{spaceId}", method = RequestMethod.GET)
    List<Media> getUserGenericMedia(@PathVariable String userName, @PathVariable Long spaceId) {
        User user = resolvePublicUser(userName);
        Space space = spaceService.getSpace(spaceId);

        return userService.getUserSpaceMedia(user, space);
    }

    @RequestMapping(value = "/posts/generic/{spaceId}", method = RequestMethod.GET)
    List<Post> getUserGenericPosts(@PathVariable String userName, @PathVariable Long spaceId) {
        User user = resolvePublicUser(userName);
        return userService.getSpacePosts(spaceId);
    }

    @RequestMapping(value = "/posts/generic/{spaceId}/page/{page}/{size}", method = RequestMethod.GET)
    Page<Post> getPageableGenericPosts(@PathVariable String userName, @PathVariable Long spaceId,
                                       @PathVariable Integer page, @PathVariable Integer size) {
        User user = resolvePublicUser(userName);
        return userService.getPageableSpacePosts(spaceId, page, size);
    }

    @RequestMapping(value = "/posts/event/{spaceId}", method = RequestMethod.GET)
    List<Post> getUserEventPosts(@PathVariable String userName, @PathVariable Long spaceId) {
        User user = resolvePublicUser(userName);
        return Collections.emptyList();
    }

    @RequestMapping(value = "/posts/shop/{spaceId}", method = RequestMethod.GET)
    List<Post> getUserShopPosts(@PathVariable String userName, @PathVariable Long spaceId) {
        return Collections.emptyList();
    }

    @RequestMapping(value = "/posts/{postId}", method = RequestMethod.GET)
    public Post getPost(@PathVariable String userName, @PathVariable Long postId) {
        User user = resolvePublicUser(userName);
        return userService.getPostById(postId);
    }

    @Deprecated /* no effect in public mode */
    @RequestMapping(value = "/user/friends", method = RequestMethod.GET)
    public List<User> getUserFriends(@PathVariable String userName) {
        User user = resolvePublicUser(userName);
        return Collections.emptyList();
    }

    @Deprecated /* no effect in public mode */
    @RequestMapping(value = "/friends", method = RequestMethod.GET)
    public List<Friend> getFriends(@PathVariable String userName) {
        User user = resolvePublicUser(userName);
        return Collections.emptyList();
    }

    @Deprecated /* no effect in public mode */
    @RequestMapping(value = "/user/friends/pending", method = RequestMethod.GET)
    public List<User> getUserFriendsPending(@PathVariable String userName) {
        User user = resolvePublicUser(userName);
        return Collections.emptyList();
    }

    @Deprecated /* no effect in public mode */
    @RequestMapping(value = "/friends/pending", method = RequestMethod.GET)
    public List<Friend> getFriendsPending(@PathVariable String userName) {
        User user = resolvePublicUser(userName);
        return Collections.emptyList();
    }

    @Deprecated /* no effect in public mode */
    @RequestMapping(value = "/user/followers", method = RequestMethod.GET)
    public List<User> getUserFollowers(@PathVariable String userName) {
        User user = resolvePublicUser(userName);
        return Collections.emptyList();
    }

    @Deprecated /* no effect in public mode */
    @RequestMapping(value = "/user/followees", method = RequestMethod.GET)
    public List<User> getUserFollowees(@PathVariable String userName) {
        User user = resolvePublicUser(userName);
        return Collections.emptyList();
    }

    @Deprecated /* no effect in public mode */
    @RequestMapping(value = "/followers", method = RequestMethod.GET)
    public List<Follower> getFollowers(@PathVariable String userName) {
        User user =resolvePublicUser(userName);
        return Collections.emptyList();
    }

    @Deprecated /* no effect in public mode */
    @RequestMapping(value = "/followees", method = RequestMethod.GET)
    public List<Follower> getFollowees(@PathVariable String userName) {
        User user = resolvePublicUser(userName);
        return Collections.emptyList();
    }

    @RequestMapping(value = "/userdata", method = RequestMethod.GET)
    public Map<String, Object> getUserData(@PathVariable String userName) {
        User user = resolvePublicUser(userName);
        return utils.userDataAsMap(user);
    }

    /**********************
     * SPACE entry points *
     **********************/

    /*  active, generic|shop|event and restricted spaces */
    @RequestMapping(value = "/spaces/{spaceType}", method = RequestMethod.GET)
    public List<Space> getUserSpaces(@PathVariable String userName, @PathVariable String spaceType) {

        User user = resolvePublicUser(userName);
        return spaceService.getPublicSpacesByType(spaceType, user.getId());
    }

    // TODO check this one - is it doing anything at all ?
    @RequestMapping(value = "/spaces/*", method = RequestMethod.GET)
    public Map<String, List<Space>> getAnyUserSpaces(@PathVariable String userName) {

        User user = resolvePublicUser(userName);
        List<Space> generic = spaceService.getMemberOfSpacesByType(Space.Type.GENERIC, user.getId());
        List<Space> events = spaceService.getMemberOfSpacesByType(Space.Type.EVENT, user.getId());
        List<Space> shops = spaceService.getMemberOfSpacesByType(Space.Type.SHOP, user.getId());

        return utils.spacesAsMap(generic, events, shops);
    }

    @RequestMapping(value = "/space/{spaceId}/members", method = RequestMethod.GET)
    public List<Member> getSpaceMembers(@PathVariable String userName, @PathVariable Long spaceId) {
        return spaceService.getMembersBySpace(spaceId);
    }

    @RequestMapping(value = "/space/{spaceId}/members/{page}/{size}", method = RequestMethod.GET)
    public Page<Member> getPageableSpaceMembers(@PathVariable String userName, @PathVariable Long spaceId,
                                                @PathVariable Integer page, @PathVariable Integer size ) {
        return spaceService.getPageableMembersBySpace(spaceId, page, size);
    }

    /* as opposed to the user version, the public version has no principal */
    @RequestMapping(value = "/space/home", method = RequestMethod.GET)
    public Map<String, Object> getHomeSpaceData(@PathVariable String userName) {
        User user = resolvePublicUser(userName);
        Space space = userService.getHomeSpace(user.getUsername());

        return utils.homeSpaceDataAsMap(space, user, null);
    }

    @RequestMapping(value = "/space/generic/{spaceId}", method = RequestMethod.GET)
    public Map<String, Object> getGenericSpaceData(@PathVariable String userName, @PathVariable Long spaceId) {
        User user = resolvePublicUser(userName);
        Space space = spaceService.getSpace(spaceId);

        return utils.genericSpaceDataAsMap(space, user);
    }

    /*********************************
     * SEARCH entry points  *
     *********************************/

    /* no '/space' in this one */
    @RequestMapping(value = "/search/{term}/{size}", method = RequestMethod.GET)
    public List<Map<String, Object>> searchByTerm(@PathVariable String userName, @PathVariable String term,
                                                  @PathVariable Integer size) {

        return searchService.searchGlobalByTerm(term, size);
    }

    /*********************************
     * POST Controller entry points  *
     *********************************/

    @RequestMapping(value = "/comments/{postId}", method = RequestMethod.GET)
    public List<Comment> getPostComments(@PathVariable String userName, @PathVariable Long postId) {
        User user = resolvePublicUser(userName);
        return postService.getCommentsByPostId(postId);
    }

    private User resolvePublicUser(String username) {
        try {
            return username.equals("public") ? userService.getUser(configuration.getSiteConfiguration().getPublicpage()) :
                    userService.getUser(username);

        } catch(Exception e) {
            throw new NotFoundException("User invalid (public) or page does not exist.");
        }
    }

}
