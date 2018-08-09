package com.kikirikii.controllers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @RequestMapping(value = "/followers", method = RequestMethod.GET)
    public List<User> getFollowers(@PathVariable String userName) {
        User user = userService.getUser(userName);

        return userService.getUserFollowers(user);
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
