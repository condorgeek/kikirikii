package com.kikirikii.repos;

import com.kikirikii.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.stream.Stream;

public interface PostRepository extends PagingAndSortingRepository<Post, Long> {

    @Query("select p from Post p where p.space.id = :spaceId order by created desc")
    Stream<Post> findAllBySpaceId(@Param("spaceId") Long spaceId);
}
