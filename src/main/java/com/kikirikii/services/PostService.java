package com.kikirikii.services;

import com.kikirikii.model.Comment;
import com.kikirikii.model.CommentLike;
import com.kikirikii.model.Like;
import com.kikirikii.repos.CommentRepository;
import com.kikirikii.repos.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
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

    public List<Like> getLikesByPostId(Long postId) {
        return null;
    }

    public List<CommentLike> getLikesByCommentId(Long commentId) {
        return null;
    }
}
