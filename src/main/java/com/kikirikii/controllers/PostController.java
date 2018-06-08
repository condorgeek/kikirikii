package com.kikirikii.controllers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kikirikii.model.*;
import com.kikirikii.services.PostService;
import com.kikirikii.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;

@RestController
//@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "http://192.168.1.100:3000"})
@CrossOrigin(origins = {"*"})
@RequestMapping("/user/{userName}")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/comments/{postId}", method = RequestMethod.GET)
    public List<Comment> getPostComments(@PathVariable String userName, @PathVariable Long postId) {
        return postService.getCommentsByPostId(postId);
    }

    @RequestMapping(value = "/comments/{postId}", method = RequestMethod.POST)
    public Comment addComment(@PathVariable String userName, @PathVariable Long postId, @RequestBody AddComment addComment) {
        User user = userService.getUser(addComment.getUsername());
        return postService.addComment(user, postId, addComment.text);
    }

    @RequestMapping(value = "/likes/{postId}", method = RequestMethod.POST)
    public List<Like> addLike(@PathVariable String userName, @PathVariable Long postId, @RequestBody AddLike addLike) {
        User user = userService.getUser(addLike.getUsername());

        return postService.addLike(user, postId, addLike.getReaction());
    }

    @RequestMapping(value = "/commentlikes/{commentId}", method =  RequestMethod.POST)
    public List<CommentLike> addCommentLike(@PathVariable String userName, @PathVariable Long commentId, @RequestBody AddLike addCommentLike) {
        User user = userService.getUser(addCommentLike.getUsername());

        return postService.addCommentLike(user, commentId, addCommentLike.getReaction());
    }

    static class AddLike {
        private String username;

        @Enumerated(EnumType.STRING)
        private LikeReaction reaction;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public LikeReaction getReaction() {
            return reaction;
        }

        public void setReaction(LikeReaction reaction) {
            this.reaction = reaction;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class AddComment {
        private String text;
        private String username;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}