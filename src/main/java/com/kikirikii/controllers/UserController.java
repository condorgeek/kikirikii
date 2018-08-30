package com.kikirikii.controllers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kikirikii.exceptions.DuplicateResourceException;
import com.kikirikii.exceptions.InvalidResourceException;
import com.kikirikii.model.*;
import com.kikirikii.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping("/user/{userName}")
public class UserController {

    @Autowired
    private UserService userService;

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

    @RequestMapping(value = "/posts/{postId}", method = RequestMethod.GET)
    public Post getPost(@PathVariable String userName, @PathVariable Long postId) {
        User user = userService.getUser(userName);
        return userService.getPostById(postId);
    }

    @RequestMapping(value = "/posts/home", method = RequestMethod.POST)
    public Post addHomePost(@PathVariable String userName, @RequestBody PostProspect postProspect) {
        User user = userService.getUser(userName);
        Space space = userService.getHomeSpace(userName);

        return userService.addPost(space, user, postProspect.title, postProspect.text, toSet.apply(postProspect.media));
    }

    @RequestMapping(value = "/posts/global", method = RequestMethod.POST)
    public Post addGlobalPost(@PathVariable String userName, @RequestBody PostProspect postProspect) {
        User user = userService.getUser(userName);
        Space space = userService.getGlobalSpace(userName);

        return userService.addPost(space, user, postProspect.title, postProspect.text, toSet.apply(postProspect.media));
    }

    @RequestMapping(value = "/friends", method = RequestMethod.GET)
    public List<User> getFriends(@PathVariable String userName) {
        User user = userService.getUser(userName);

        return userService.getUserFriends(user);
    }

    @RequestMapping(value = "/friends/pending", method = RequestMethod.GET)
    public List<User> getFriendsPending(@PathVariable String userName) {
        User user = userService.getUser(userName);

        return userService.getUserFriendsPending(user);
    }

    @RequestMapping(value = "/friend/add", method = RequestMethod.PUT)
    public List<User> addFriend(@PathVariable String userName, @RequestBody Map<String, String> values) {
        User user = userService.getUser(userName);
        User surrogate = userService.getUser(values.get("friend"));

        if(user.getUsername().equals(surrogate.getUsername())) {
            throw new InvalidResourceException("User and friend cannot be equal");
        } else if(userService.isFriend(user, surrogate)) {
            throw new InvalidResourceException("Users are already friends");
        }

        userService.addFriend(user, surrogate);
        return userService.getUserFriendsPending(user);
    }

    // TODO need to return change in actual friend list as well....
    @RequestMapping(value = "/friend/accept", method = RequestMethod.PUT)
    public List<User> acceptFriendRequest(@PathVariable String userName, @RequestBody Map<String, String> values) {
        User user = userService.getUser(userName);
        User surrogate = userService.getUser(values.get("friend"));

        userService.acceptFriend(user, surrogate);
        return userService.getUserFriendsPending(user);
    }

    // TODO need to return change in actual friend list as well....
    @RequestMapping(value = "/friend/ignore", method = RequestMethod.PUT)
    public List<User> ignoreFriendRequest(@PathVariable String userName, @RequestBody Map<String, String> values) {
        User user = userService.getUser(userName);
        User surrogate = userService.getUser(values.get("friend"));

        userService.ignoreFriendRequest(user, surrogate);
        return userService.getUserFriendsPending(user);
    }
    @RequestMapping(value = "/friend/cancel", method = RequestMethod.PUT)
    public List<User> cancelFriendRequest(@PathVariable String userName, @RequestBody Map<String, String> values) {
        User user = userService.getUser(userName);
        User surrogate = userService.getUser(values.get("friend"));

        userService.cancelFriendRequest(user, surrogate);
        return userService.getUserFriendsPending(user);
    }

    @RequestMapping(value = "/friend/block", method = RequestMethod.PUT)
    public List<User> blockFriend(@PathVariable String userName, @RequestBody Map<String, String> values) {
        User user = userService.getUser(userName);
        User surrogate = userService.getUser(values.get("friend"));

        userService.blockFriend(user, surrogate);
        return userService.getUserFriends(user);
    }

    @RequestMapping(value = "/friend/unblock", method = RequestMethod.PUT)
    public List<User> unblockFriend(@PathVariable String userName, @RequestBody Map<String, String> values) {
        User user = userService.getUser(userName);
        User surrogate = userService.getUser(values.get("friend"));

        userService.unblockFriend(user, surrogate);
        return userService.getUserFriends(user);
    }

    @RequestMapping(value = "/friend/delete", method = RequestMethod.PUT)
    public List<User> deleteFriend(@PathVariable String userName, @RequestBody Map<String, String> values) {
        User user = userService.getUser(userName);
        User surrogate = userService.getUser(values.get("friend"));

        userService.deleteFriend(user, surrogate);
        return userService.getUserFriends(user);
    }

    @RequestMapping(value = "/followee/add", method = RequestMethod.PUT)
    public List<User> addFollowee(@PathVariable String userName, @RequestBody Map<String, String> values) {
        User user = userService.getUser(userName);
        User surrogate = userService.getUser(values.get("followee"));

        if(user.getUsername().equals(surrogate.getUsername())) {
            throw new InvalidResourceException("User and followee cannot be equal");
        } else if(userService.isFollowee(user, surrogate)) {
            throw new InvalidResourceException("User is already a follower");
        }

        userService.addFollowee(user, surrogate);
        return userService.getUserFollowees(user);
    }

    @RequestMapping(value = "/followee/delete", method = RequestMethod.PUT)
    public List<Follower> deleteFollowee(@PathVariable String userName, @RequestBody Map<String, String> values) {
        User user = userService.getUser(userName);
        User surrogate = userService.getUser(values.get("followee"));

        userService.deleteFollowee(user, surrogate);
        return userService.getFollowees(user);
    }

    @RequestMapping(value = "/follower/block", method = RequestMethod.PUT)
    public List<Follower> blockFollower(@PathVariable String userName, @RequestBody Map<String, String> values) {
        User user = userService.getUser(userName);
        User surrogate = userService.getUser(values.get("follower"));

        userService.blockFollower(user, surrogate);
        return userService.getFollowers(user);
    }

    @RequestMapping(value = "/follower/unblock", method = RequestMethod.PUT)
    public List<Follower> unblockFollower(@PathVariable String userName, @RequestBody Map<String, String> values) {
        User user = userService.getUser(userName);
        User surrogate = userService.getUser(values.get("follower"));

        userService.unblockFollower(user, surrogate);
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
    public List<Follower> getRawFollowers(@PathVariable String userName) {
        User user = userService.getUser(userName);

        return userService.getFollowers(user);
    }

    @RequestMapping(value = "/followees", method = RequestMethod.GET)
    public List<Follower> getRawFollowees(@PathVariable String userName) {
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

    @RequestMapping(value = "/space/cover", method = RequestMethod.PUT)
    public Map<String, Object> updateHomeCover(@PathVariable String userName, @RequestBody Map<String, String> values) {
        Space space = userService.getHomeSpace(userName);
        space.setCover(values.get("path"));
        space = userService.updateSpace(space);

        Map<String, Object> data = new HashMap<>();
        data.put("space", space);
        return data;
    }

    @RequestMapping(value = "/space/home", method = RequestMethod.GET)
    public Map<String, Object> getHomeSpace(@PathVariable String userName) {
        Map<String, Object> data = new HashMap<>();
        data.put("space", userService.getHomeSpace(userName));
        data.put("userdata", userService.getUser(userName).getUserData());
        data.put("friends", userService.getFriendsCount(userName));
        data.put("followers", userService.getFollowersCount(userName));
        return data;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class PostProspect {
        private String title;
        private String text;
        private Media[] media;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Media[] getMedia() {
            return media;
        }

        public void setMedia(Media[] media) {
            this.media = media;
        }
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

    private Function<MediaProspect[], Set<Media>> toMediaSet = media ->
            (media != null && media.length > 0) ? Arrays.stream(media)
                    .map(entry -> Media.of(entry.url, entry.type))
                    .collect(Collectors.toSet()) : null;

    private Function<Media[], Set<Media>> toSet = media ->
            (media != null && media.length > 0) ? Arrays.stream(media).collect(Collectors.toSet()) : null;
}
