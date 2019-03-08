/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [PostController.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 04.10.18 11:34
 */

package com.kikirikii.controllers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kikirikii.model.*;
import com.kikirikii.model.dto.PostRequest;
import com.kikirikii.model.enums.Reaction;
import com.kikirikii.services.PostService;
import com.kikirikii.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
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

    @Secured("ROLE_USER")
    @RequestMapping(value = "/comments/{postId}", method = RequestMethod.POST)
    public Comment addComment(@PathVariable String userName, @PathVariable Long postId, @RequestBody CommentRequest commentRequest) {
        User user = userService.getUser(commentRequest.getUsername());
        return postService.addComment(user, postId, commentRequest.text);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/likes/{postId}", method = RequestMethod.POST)
    public List<Like> addLike(@PathVariable String userName, @PathVariable Long postId, @RequestBody LikeRequest addLike) {
        User user = userService.getUser(addLike.getUsername());

        return postService.addLike(user, postId, addLike.getReaction());
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/likes/{postId}/remove/{likeId}", method = RequestMethod.DELETE)
    public List<Like> removeLike(@PathVariable String userName, @PathVariable Long postId,
                                 @PathVariable Long likeId) {
        User user = userService.getUser(userName);

        return postService.removeLike(user, postId, likeId);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/commentlikes/{commentId}", method =  RequestMethod.POST)
    public List<CommentLike> addCommentLike(@PathVariable String userName, @PathVariable Long commentId, @RequestBody LikeRequest addCommentLike) {
        User user = userService.getUser(addCommentLike.getUsername());

        return postService.addCommentLike(user, commentId, addCommentLike.getReaction());
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/commentlikes/{commentId}/remove/{likeId}", method = RequestMethod.DELETE)
    public List<CommentLike> removeCommentLike(@PathVariable String userName, @PathVariable Long commentId, @PathVariable Long likeId) {
        User user = userService.getUser(userName);

        return postService.removeCommentLike(user, commentId, likeId);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/posts/{postId}/ranking/{ranking}", method = RequestMethod.PUT)
    public Post updatePostRanking(@PathVariable String userName, @PathVariable Long postId,
                                  @PathVariable Integer ranking) {
        User user = userService.getUser(userName);
        Post post = postService.getPost(postId);

        return postService.updateRanking(post, ranking);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/posts/{postId}/update", method = RequestMethod.POST)
    public Post updatePost(@PathVariable String userName, @PathVariable Long postId, @RequestBody PostRequest postRequest) {
        User user = userService.getUser(userName);
        Post post = postService.getPost(postId);

        return userService.updatePost(post, user, postRequest.getTitle(), postRequest.getText(),
                postRequest.getMediaAsList());
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/posts/{postId}/media/{mediaId}/delete", method = RequestMethod.DELETE)
    public Post deletePostMedia(@PathVariable String userName, @PathVariable Long postId, @PathVariable Long mediaId) {
        User user = userService.getUser(userName);
        Post post = postService.getPost(postId);
        Media media = postService.getMedia(mediaId);

        return userService.deleteMedia(post, media);
    }

    static class LikeRequest {
        private String username;

        @Enumerated(EnumType.STRING)
        private Reaction reaction;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Reaction getReaction() {
            return reaction;
        }

        public void setReaction(Reaction reaction) {
            this.reaction = reaction;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class CommentRequest {
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
