package com.kikirikii.db;

import com.kikirikii.model.Media;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.stream.Stream;

public interface MediaRepository extends CrudRepository<Media, Long> {

    @Query("select m from Media m where m.post.id = :postId")
    Stream<Media> findAllByPostId(@Param("postId") Long postId);
}
