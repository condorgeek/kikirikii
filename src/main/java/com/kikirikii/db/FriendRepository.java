package com.kikirikii.db;

import com.kikirikii.model.Friend;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.stream.Stream;

public interface FriendRepository extends CrudRepository<Friend, Long> {

    @Query("select f from Friend f where f.user.id = :userId")
    Stream<Friend> findAllByUserId(@Param("userId") String userId);
}
