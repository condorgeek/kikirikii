package com.kikirikii.services;

import com.kikirikii.model.*;
import com.kikirikii.repos.CommentRepository;
import com.kikirikii.repos.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import javax.transaction.Transactional;
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
        Optional<Post> post =  postRepository.findById(postId);
        assert post.isPresent() : "PostId not found " + postId;

        return post.get();
    }

    public Comment addComment(User user, Long postId, String text) {
        return commentRepository.save(Comment.of(getPost(postId), user, text));
    }

    public List<Like> getLikesByPostId(Long postId) {
        return null;
    }

    public List<CommentLike> getLikesByCommentId(Long commentId) {
        return null;
    }
}
