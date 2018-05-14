package com.kikirikii.services;

import com.kikirikii.model.*;
import com.kikirikii.repos.CommentRepository;
import com.kikirikii.repos.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findAllByPostId(postId).collect(Collectors.toList());
    }

    public Post getPost(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        assert post.isPresent() : "PostId not found " + postId;

        return post.get();
    }

    public Comment getComment(Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        assert comment.isPresent() : "CommentId not found" + commentId;

        return comment.get();
    }

    public Comment addComment(User user, Long postId, String text) {
        return commentRepository.save(Comment.of(getPost(postId), user, text));
    }

    public List<Like> addLike(User user, Long postId, LikeReaction type) {
        Post post = getPost(postId);
        post.addLike(Like.of(user, type));

        return new ArrayList<>(postRepository.save(post).getLikes());
    }

    public List<CommentLike> addCommentLike(User user, Long commentId, LikeReaction reaction) {
        Comment comment = getComment(commentId);
        comment.addLike(CommentLike.of(user, reaction));

        return new ArrayList<>(commentRepository.save(comment).getLikes());
    }

}
