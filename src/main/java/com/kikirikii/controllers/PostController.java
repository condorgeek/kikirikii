package com.kikirikii.controllers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kikirikii.model.Comment;
import com.kikirikii.model.Like;
import com.kikirikii.model.Post;
import com.kikirikii.model.User;
import com.kikirikii.services.PostService;
import com.kikirikii.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
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
    public Post addLike(@PathVariable String userName, @PathVariable Long postId, @RequestBody AddLike addLike) {
        User user = userService.getUser(addLike.getUsername());
        return postService.addLike(user, postId, addLike.getType());
    }

    static class AddLike {
        private String username;

        @Enumerated(EnumType.STRING)
        private Like.Type type;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Like.Type getType() {
            return type;
        }

        public void setType(Like.Type type) {
            this.type = type;
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
