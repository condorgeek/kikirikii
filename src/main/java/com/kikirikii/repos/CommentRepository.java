package com.kikirikii.repos;

import com.kikirikii.model.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.stream.Stream;

public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("select c from Comment c where c.post.id = :postId order by created desc")
    Stream<Comment> findAllByPostId(@Param("postId") Long postId);
}
