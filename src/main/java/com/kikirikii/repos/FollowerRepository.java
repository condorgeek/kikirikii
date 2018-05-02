package com.kikirikii.repos;

import com.kikirikii.model.Follower;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.stream.Stream;

public interface FollowerRepository extends CrudRepository<Follower, Long> {

    @Query("select f from Follower f where f.user.id = :userId")
    Stream<Follower> findAllByUserId(@Param("userId") String userId);
}
