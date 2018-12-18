/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [UserController.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 04.10.18 11:34
 */

package com.kikirikii.controllers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kikirikii.exceptions.InvalidResourceException;
import com.kikirikii.model.*;
import com.kikirikii.model.dto.PostRequest;
import com.kikirikii.model.dto.Topic;
import com.kikirikii.model.dto.UserRequest;
import com.kikirikii.services.ChatService;
import com.kikirikii.services.SpaceService;
import com.kikirikii.services.UserService;
import com.kikirikii.services.WebsocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping("/user/{userName}")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private WebsocketService websocketService;

    @Autowired
    private ObjectMapper mapper;

    @RequestMapping(value = "/posts/global", method = RequestMethod.GET)
    List<Post> getUserGlobalPosts(@PathVariable String userName) {
        User user = userService.getUser(userName);
        return userService.getUserGlobalPosts(user);
    }

    @RequestMapping(value = "/posts/home", method = RequestMethod.GET)
    List<Post> getUserHomePosts(@PathVariable String userName) {
        User user = userService.getUser(userName);
        return userService.getUserHomePosts(user);
    }

    @RequestMapping(value = "/posts/media/home", method = RequestMethod.GET)
    List<Media> getUserHomeMedia(@PathVariable String userName) {
        User user = userService.getUser(userName);
        Space space = userService.getHomeSpace(user.getUsername());

        return userService.getUserSpaceMedia(user, space);
    }

    @RequestMapping(value = "/posts/media/generic/{spaceId}", method = RequestMethod.GET)
    List<Media> getUserGenericMedia(@PathVariable String userName, @PathVariable Long spaceId) {
        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);

        return userService.getUserSpaceMedia(user, space);
    }

    @RequestMapping(value = "/posts/generic/{spaceId}", method = RequestMethod.GET)
    List<Post> getUserGenericPosts(@PathVariable String userName, @PathVariable Long spaceId) {
        User user = userService.getUser(userName);
        return userService.getSpacePosts(user, spaceId);
    }

    @RequestMapping(value = "/posts/event/{spaceId}", method = RequestMethod.GET)
    List<Post> getUserEventPosts(@PathVariable String userName, @PathVariable Long spaceId) {
        return Collections.emptyList();
    }

    @RequestMapping(value = "/posts/shop/{spaceId}", method = RequestMethod.GET)
    List<Post> getUserShopPosts(@PathVariable String userName, @PathVariable Long spaceId) {
        return Collections.emptyList();
    }

    @RequestMapping(value = "/posts/{postId}", method = RequestMethod.GET)
    public Post getPost(@PathVariable String userName, @PathVariable Long postId) {
        User user = userService.getUser(userName);
        return userService.getPostById(postId);
    }

    @RequestMapping(value = "/posts/home", method = RequestMethod.POST)
    public Post addHomePost(@PathVariable String userName, @RequestBody PostRequest postRequest) {
        User user = userService.getUser(userName);
        Space space = userService.getHomeSpace(userName);

        return userService.addPost(space, user, postRequest.getTitle(), postRequest.getText(),
                postRequest.getMediaAsSet());
    }

    @RequestMapping(value = "/posts/{postId}/delete", method = RequestMethod.DELETE)
    public Post deletePost(@PathVariable String userName, @PathVariable Long postId) {
        User user = userService.getUser(userName);
        Post post = userService.getPostById(postId);

        if(isPostOwner.apply(post, user) || isSpaceOwner.apply(post.getSpace(), user)) {
            return userService.deletePostById(postId);
        }

        throw new InvalidResourceException("User has not enough authority to delete post");
    }

    private BiFunction<Post, User, Boolean> isPostOwner = (p, u) -> p.getUser().getUsername().equals(u.getUsername());
    private BiFunction<Space, User, Boolean> isSpaceOwner = (s, u) -> s.getUser().getUsername().equals(u.getUsername());


    @RequestMapping(value = "/posts/{postId}/hide", method = RequestMethod.PUT)
    public Post hidePost(@PathVariable String userName, @PathVariable Long postId) {
        User user = userService.getUser(userName);
        return userService.hidePostById(postId);
    }

    @RequestMapping(value = "/posts/generic/{spaceId}", method = RequestMethod.POST)
    public Post addGenericPost(@PathVariable String userName, @PathVariable Long spaceId, @RequestBody PostRequest postRequest) {
        User user = userService.getUser(userName);
        Space space = spaceService.getSpace(spaceId);

        return userService.addPost(space, user, postRequest.getTitle(), postRequest.getText(),
                postRequest.getMediaAsSet());
    }

    @RequestMapping(value = "/posts/global", method = RequestMethod.POST)
    public Post addGlobalPost(@PathVariable String userName, @RequestBody PostRequest postRequest) {
        User user = userService.getUser(userName);
        Space space = userService.getGlobalSpace(userName);

        return userService.addPost(space, user, postRequest.getTitle(), postRequest.getText(),
                postRequest.getMediaAsSet());
    }

    @RequestMapping(value = "/userdata/update", method = RequestMethod.POST)
    public UserData updateUserData(@PathVariable String userName, @RequestBody UserRequest userRequest) {
        User user = userService.getUser(userName);

        return userService.updateUser(user, userRequest).getUserData();
    }

    @RequestMapping(value = "/user/friends", method = RequestMethod.GET)
    public List<User> getUserFriends(@PathVariable String userName) {
        User user = userService.getUser(userName);

        return userService.getUserFriends(user);
    }

    @RequestMapping(value = "/friends", method = RequestMethod.GET)
    public List<Friend> getFriends(@PathVariable String userName) {
        User user = userService.getUser(userName);

        return userService.getFriends(user).stream().map(friend -> {
            Chat chat = friend.getChat();
            chat.setConsumed(chatService.getConsumedFromCount(chat.getId(), user.getUsername()));
            chat.setDelivered(chatService.getDeliveredToCount(chat.getId(), user.getUsername()));
            return friend;
        }).collect(Collectors.toList());
    }

    @RequestMapping(value = "/user/friends/pending", method = RequestMethod.GET)
    public List<User> getUserFriendsPending(@PathVariable String userName) {
        User user = userService.getUser(userName);

        return userService.getUserFriendsPending(user);
    }

    @RequestMapping(value = "/friends/pending", method = RequestMethod.GET)
    public List<Friend> getFriendsPending(@PathVariable String userName) {
        User user = userService.getUser(userName);

        return userService.getFriendsPending(user);
    }

    private static final int REQUEST = 0, PENDING = 1, CANCELLED = 1, IGNORED = 1, ACCEPTED = 1, DELETED = 1, BLOCKED = 1, UNBLOCKED = 1;

    @RequestMapping(value = "/friend/add", method = RequestMethod.PUT)
    public Friend addFriend(@PathVariable String userName, @RequestBody Map<String, String> values) {
        User user = userService.getUser(userName);
        User surrogate = userService.getUser(values.get("friend"));

        if(user.getUsername().equals(surrogate.getUsername())) {
            throw new InvalidResourceException("User and friend cannot be equal");
        } else if(userService.isFriend(user, surrogate)) {
            throw new InvalidResourceException("Users are already friends");
        }

        Friend[] friends = userService.addFriend(user, surrogate);

        websocketService.sendToUser(surrogate.getUsername(), Topic.GENERIC,
                entry.apply("event", Event.EVENT_FRIEND_REQUESTED.name()),
                entry.apply("message", user.getUsername() + " is requesting your friendship"),
                entry.apply("user", toJSON.apply(friends[PENDING])));

        return friends[REQUEST];
    }

    @RequestMapping(value = "/friend/accept", method = RequestMethod.PUT)
    public Friend acceptFriendRequest(@PathVariable String userName, @RequestBody Map<String, String> values) {
        User user = userService.getUser(userName);
        User surrogate = userService.getUser(values.get("friend"));

        Friend[] friends = userService.acceptFriend(user, surrogate);

        websocketService.sendToUser(surrogate.getUsername(), Topic.GENERIC,
                entry.apply("event", Event.EVENT_FRIEND_ACCEPTED.name()),
                entry.apply("message", user.getUsername() + " has accepted your friendship"),
                entry.apply("user", toJSON.apply(friends[ACCEPTED])));

    return friends[REQUEST];
    }

    @RequestMapping(value = "/friend/ignore", method = RequestMethod.PUT)
    public Friend ignoreFriendRequest(@PathVariable String userName, @RequestBody Map<String, String> values) {
        User user = userService.getUser(userName);
        User surrogate = userService.getUser(values.get("friend"));

        Friend[] friends = userService.ignoreFriendRequest(user, surrogate);

        websocketService.sendToUser(surrogate.getUsername(), Topic.GENERIC,
                entry.apply("event", Event.EVENT_FRIEND_IGNORED.name()),
                entry.apply("message", user.getUsername() + " has ignored your friendship request"),
                entry.apply("user", toJSON.apply(friends[IGNORED])));

        return friends[REQUEST];
    }

    @RequestMapping(value = "/friend/cancel", method = RequestMethod.PUT)
    public Friend cancelFriendRequest(@PathVariable String userName, @RequestBody Map<String, String> values) {
        User user = userService.getUser(userName);
        User surrogate = userService.getUser(values.get("friend"));

        Friend[] friends = userService.cancelFriendRequest(user, surrogate);

        websocketService.sendToUser(surrogate.getUsername(), Topic.GENERIC,
                entry.apply("event", Event.EVENT_FRIEND_CANCELLED.name()),
                entry.apply("message", user.getUsername() + " has cancelled her friendship request"),
                entry.apply("user", toJSON.apply(friends[CANCELLED])));

        return friends[REQUEST];
    }

    @RequestMapping(value = "/friend/block", method = RequestMethod.PUT)
    public Friend blockFriend(@PathVariable String userName, @RequestBody Map<String, String> values) {
        User user = userService.getUser(userName);
        User surrogate = userService.getUser(values.get("friend"));

        Friend[] friends = userService.blockFriend(user, surrogate);

        websocketService.sendToUser(surrogate.getUsername(), Topic.GENERIC,
                entry.apply("event", Event.EVENT_FRIEND_BLOCKED.name()),
                entry.apply("message", user.getUsername() + " has blocked you."),
                entry.apply("user", toJSON.apply(friends[BLOCKED])));

        return friends[REQUEST];
    }

    @RequestMapping(value = "/friend/unblock", method = RequestMethod.PUT)
    public Friend unblockFriend(@PathVariable String userName, @RequestBody Map<String, String> values) {
        User user = userService.getUser(userName);
        User surrogate = userService.getUser(values.get("friend"));

        Friend[] friends = userService.unblockFriend(user, surrogate);

        websocketService.sendToUser(surrogate.getUsername(), Topic.GENERIC,
                entry.apply("event", Event.EVENT_FRIEND_UNBLOCKED.name()),
                entry.apply("message", user.getUsername() + " has unblocked you."),
                entry.apply("user", toJSON.apply(friends[UNBLOCKED])));

        return friends[REQUEST];
    }

    @RequestMapping(value = "/friend/delete", method = RequestMethod.PUT)
    public Friend deleteFriend(@PathVariable String userName, @RequestBody Map<String, String> values) {
        User user = userService.getUser(userName);
        User surrogate = userService.getUser(values.get("friend"));

        Friend[] friends = userService.deleteFriend(user, surrogate);

        websocketService.sendToUser(surrogate.getUsername(), Topic.GENERIC,
                entry.apply("event", Event.EVENT_FRIEND_DELETED.name()),
                entry.apply("message", user.getUsername() + " has deleted your friendship."),
                entry.apply("user", toJSON.apply(friends[DELETED])));

        return friends[REQUEST];
    }

    @RequestMapping(value = "/followee/add", method = RequestMethod.PUT)
    public Follower addFollowee(@PathVariable String userName, @RequestBody Map<String, String> values) {
        User user = userService.getUser(userName);
        User surrogate = userService.getUser(values.get("followee"));

        if(user.getUsername().equals(surrogate.getUsername())) {
            throw new InvalidResourceException("User and followee cannot be equal");
        } else if(userService.isFollowee(user, surrogate)) {
            throw new InvalidResourceException("User is already a follower");
        }

        Follower follower = userService.addFollowee(user, surrogate);

        websocketService.sendToUser(surrogate.getUsername(), Topic.GENERIC,
                entry.apply("event", Event.EVENT_FOLLOWER_ADDED.name()),
                entry.apply("message", user.getUsername() + " is following you."),
                entry.apply("follower", toJSON.apply(follower)));

        return follower;
    }

    @RequestMapping(value = "/followee/delete", method = RequestMethod.PUT)
    public Follower deleteFollowee(@PathVariable String userName, @RequestBody Map<String, String> values) {
        User user = userService.getUser(userName);
        User surrogate = userService.getUser(values.get("followee"));

        Follower follower =userService.deleteFollowee(user, surrogate);

        websocketService.sendToUser(surrogate.getUsername(), Topic.GENERIC,
                entry.apply("event", Event.EVENT_FOLLOWER_DELETED.name()),
                entry.apply("message", user.getUsername() + " has stopped following you."),
                entry.apply("follower", toJSON.apply(follower)));

        return follower;
    }

    @RequestMapping(value = "/follower/block", method = RequestMethod.PUT)
    public List<Follower> blockFollower(@PathVariable String userName, @RequestBody Map<String, String> values) {
        User user = userService.getUser(userName);
        User surrogate = userService.getUser(values.get("follower"));

        Follower follower = userService.blockFollower(user, surrogate);

        websocketService.sendToUser(surrogate.getUsername(), Topic.GENERIC,
                entry.apply("event", Event.EVENT_FOLLOWER_BLOCKED.name()),
                entry.apply("message", user.getUsername() + " has blocked you."),
                entry.apply("follower", toJSON.apply(follower)));

        return userService.getFollowers(user);
    }

    @RequestMapping(value = "/follower/unblock", method = RequestMethod.PUT)
    public List<Follower> unblockFollower(@PathVariable String userName, @RequestBody Map<String, String> values) {
        User user = userService.getUser(userName);
        User surrogate = userService.getUser(values.get("follower"));

        Follower follower = userService.unblockFollower(user, surrogate);

        websocketService.sendToUser(surrogate.getUsername(), Topic.GENERIC,
                entry.apply("event", Event.EVENT_FOLLOWER_UNBLOCKED.name()),
                entry.apply("message", user.getUsername() + " has unblocked you."),
                entry.apply("follower", toJSON.apply(follower)));

        return userService.getFollowers(user);
    }

    @RequestMapping(value = "/user/followers", method = RequestMethod.GET)
    public List<User> getUserFollowers(@PathVariable String userName) {
        User user = userService.getUser(userName);

        return userService.getUserFollowers(user);
    }

    @RequestMapping(value = "/user/followees", method = RequestMethod.GET)
    public List<User> getUserFollowees(@PathVariable String userName) {
        User user = userService.getUser(userName);

        return userService.getUserFollowees(user);
    }

    @RequestMapping(value = "/followers", method = RequestMethod.GET)
    public List<Follower> getFollowers(@PathVariable String userName) {
        User user = userService.getUser(userName);

        return userService.getFollowers(user);
    }

    @RequestMapping(value = "/followees", method = RequestMethod.GET)
    public List<Follower> getFollowees(@PathVariable String userName) {
        User user = userService.getUser(userName);

        return userService.getFollowees(user);
    }

    @RequestMapping(value = "/userdata", method = RequestMethod.GET)
    public Map<String, Object> getUserData(@PathVariable String userName) {
        User user = userService.getUser(userName);

        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("userdata", user.getUserData());
        return data;
    }

    @RequestMapping(value = "/userdata/avatar", method = RequestMethod.PUT)
    public Map<String, Object> updateUserAvatar(@PathVariable String userName, @RequestBody Map<String, String> values) {
        User user = userService.getUser(userName);
        user.setAvatar(values.get("path"));
        user = userService.updateUser(user);

        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("userdata", user.getUserData());
        return data;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class MediaProspect {
        private String url;
        private Media.Type type;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Media.Type getType() {
            return type;
        }

        public void setType(Media.Type type) {
            this.type = type;
        }
    }

    public class Property {
        private String key;
        private String value;

        public Property() {}
        public Property(String key, String value) {this.key = key; this.value = value;}
        public String getKey() {return key;}
        public String getValue() {return value;}
    }

    private Function<MediaProspect[], Set<Media>> asMediaSet = media ->
            (media != null && media.length > 0) ? Arrays.stream(media)
                    .map(entry -> Media.of(entry.url, entry.type))
                    .collect(Collectors.toSet()) : null;

    private Function<Media[], Set<Media>> asSet = media ->
            (media != null && media.length > 0) ? Arrays.stream(media).collect(Collectors.toSet()) : null;

    private BiFunction<String, String, AbstractMap.SimpleEntry> entry = AbstractMap.SimpleEntry::new;

    Function<Object, String> toJSON = (o) -> {
        try {
            return mapper.writeValueAsString(o);
        } catch (Exception e) {}
        return null;
    };
}
