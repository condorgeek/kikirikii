package com.kikirikii.db;

import com.kikirikii.model.PostLike;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.stream.Stream;

public interface LikeRepository extends CrudRepository<PostLike, Long> {

    @Query("select l from PostLike l where l.post.id = :postId")
    Stream<PostLike> findAllByPostId(@Param("postId") Long postId);
}
