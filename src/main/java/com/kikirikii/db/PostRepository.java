package com.kikirikii.db;

import com.kikirikii.model.Comment;
import com.kikirikii.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.stream.Stream;

public interface PostRepository extends CrudRepository<Post, Long> {

    @Query("select c from Comment c where c.post.id = :postId order by created desc")
    Stream<Comment> findAllCommentsById(@Param("postId") Long postId);
}
