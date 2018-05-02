package com.kikirikii.controllers;

import com.kikirikii.model.Comment;
import com.kikirikii.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/{userName}")
public class PostController {

    @Autowired
    private PostService postService;

    @RequestMapping(value = "/comments/{postId}", method = RequestMethod.GET)
    public List<Comment> getPostComments(@PathVariable String userName, @PathVariable Long postId) {
        return postService.getCommentsByPostId(postId);
    }
}
