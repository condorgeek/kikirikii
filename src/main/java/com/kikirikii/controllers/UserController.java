package com.kikirikii.controllers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kikirikii.model.Post;
import com.kikirikii.model.Space;
import com.kikirikii.model.User;
import com.kikirikii.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "http://192.168.1.100:3000"})
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
    public Post addHomePost(@PathVariable String userName, @RequestBody AddPost addPost) {
        User user = userService.getUser(userName);
        Space space = userService.getHomeSpace(userName);

        return userService.addPost(space, user, addPost.title, addPost.text);
    }

    @RequestMapping(value = "/posts/global", method = RequestMethod.POST)
    public Post addGlobalPost(@PathVariable String userName, @RequestBody AddPost addPost) {
        User user = userService.getUser(userName);
        Space space = userService.getGlobalSpace(userName);

        return userService.addPost(space, user, addPost.title, addPost.text);
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class AddPost {
        private String title;
        private String text;
        private String mediaUrl;

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

        public String getMediaUrl() {
            return mediaUrl;
        }

        public void setMediaUrl(String mediaUrl) {
            this.mediaUrl = mediaUrl;
        }
    }

}
