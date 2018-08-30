package com.kikirikii.repos;

import com.kikirikii.model.Friend;
import com.kikirikii.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface FriendRepository extends CrudRepository<Friend, Long> {

    @Query("select f from Friend f where f.user.id = :userId")
    Stream<Friend> findAllByUserId(@Param("userId") Long userId);

    @Query("select f from Friend f where f.user.id = :userId")
    List<Friend> findAsListByUserId(@Param("userId") Long userId);

    @Query("select count(f) from Friend f where f.user.username = :username")
    Long countByUsername(@Param("username") String username);

    @Query("select f from Friend f where f.user.username = :username and f.surrogate.username = :surrogate and f.state = :state")
    Optional<Friend> findBySurrogateAndState(@Param("username") String username, @Param("surrogate") String surrogate, @Param("state") Friend.State state);

    @Query("select f from Friend f where f.user.username = :username and f.surrogate.username = :surrogate and f.state in ('ACTIVE', 'PENDING', 'BLOCKED')")
    Optional<Friend> findBySurrogateActiveState(@Param("username") String username, @Param("surrogate") String surrogate);

    @Query("select f from Friend f where f.user.username = :username and f.state in ('ACTIVE', 'PENDING', 'BLOCKED')")
    List<Friend> findActivePendingBlocked(@Param("username") String username);

    @Query("select f from Friend f where f.user.username = :username and f.state in ('ACTIVE', 'BLOCKED')")
    List<Friend> findActiveBlocked(@Param("username") String username);

    @Query("select f from Friend f where f.user.username = :username and f.state = :state")
    List<Friend> findByState(@Param("username") String username, @Param("state") Friend.State state);
}
