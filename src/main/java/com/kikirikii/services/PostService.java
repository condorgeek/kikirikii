/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [PostService.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 14.05.18 12:06
 */

package com.kikirikii.services;

import com.kikirikii.exceptions.InvalidResourceException;
import com.kikirikii.model.*;
import com.kikirikii.model.enums.Reaction;
import com.kikirikii.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private MediaRepository mediaRepository;

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findAllByPostId(postId).collect(Collectors.toList());
    }

    public Post getPost(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        assert post.isPresent() : "PostId not found " + postId;

        return post.get();
    }

    public Like getLike(Long likeId) {
        Optional<Like> like = likeRepository.findById(likeId);
        if (like.isPresent()) {
            return like.get();
        }
        throw new InvalidResourceException("Cannot find like " + likeId);
    }

    public Media getMedia(Long mediaId) {
        Optional<Media> media = mediaRepository.findById(mediaId);
        if(media.isPresent()) {
            return  media.get();
        }
        throw new InvalidResourceException("Cannot find media " + mediaId);
    }

    public CommentLike getCommentLike(Long likeId) {
        Optional<CommentLike> like = commentLikeRepository.findById(likeId);
        if (like.isPresent()) {
            return like.get();
        }
        throw new InvalidResourceException("Cannot find comment like " + likeId);
    }

    public Comment getComment(Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        assert comment.isPresent() : "CommentId not found" + commentId;

        return comment.get();
    }

    public Comment addComment(User user, Long postId, String text) {
        return commentRepository.save(Comment.of(getPost(postId), user, text));
    }

    public List<Like> addLike(User user, Long postId, Reaction type) {
        Post post = getPost(postId);
        post.addLike(Like.of(user, type));

        return new ArrayList<>(postRepository.save(post).getLikes());
    }

    public List<Like> removeLike(User user, Long postId, Long likeId) {
        Post post = getPost(postId);
        post.deleteLike(getLike(likeId));

        postRepository.save(post);
        return likeRepository.findAllActiveByPostId(postId);
    }

    public List<CommentLike> removeCommentLike(User user, Long commentId, Long likeId) {
        Comment comment = getComment(commentId);
        comment.deleteLike(getCommentLike(likeId));

        commentRepository.save(comment);
        return commentLikeRepository.findAllActiveByCommentId(commentId);
    }

    public List<CommentLike> addCommentLike(User user, Long commentId, Reaction reaction) {
        Comment comment = getComment(commentId);
        comment.addLike(CommentLike.of(user, reaction));

        return new ArrayList<>(commentRepository.save(comment).getLikes());
    }

}
