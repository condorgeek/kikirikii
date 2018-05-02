package com.kikirikii.repos;

import com.kikirikii.model.CommentLike;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.stream.Stream;

public interface CommentLikeRepository extends CrudRepository<CommentLike, Long> {

    @Query("select l from CommentLike l where l.comment.id = :commentId")
    Stream<CommentLike> findAllByCommentId(@Param("commentId") Long commentId);
}
