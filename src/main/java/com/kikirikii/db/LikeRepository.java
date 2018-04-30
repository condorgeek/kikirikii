package com.kikirikii.db;

import com.kikirikii.model.Like;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.stream.Stream;

public interface LikeRepository extends CrudRepository<Like, Long> {

    @Query("select l from Like l where l.post.id = :postId")
    Stream<Like> findAllByPostId(@Param("postId") Long postId);
}
