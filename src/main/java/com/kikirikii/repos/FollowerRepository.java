/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [FollowerRepository.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 30.08.18 16:47
 */

package com.kikirikii.repos;

import com.kikirikii.model.Follower;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface FollowerRepository extends CrudRepository<Follower, Long> {

    @Query("select f from Follower f where f.user.id = :userId")
    Stream<Follower> findAllByUserId(@Param("userId") Long userId);

    @Query("select f from Follower f where f.user.id = :userId")
    List<Follower> findAsListByUserId(@Param("userId") Long userId);

    @Query("select count(f) from Follower f where f.user.username = :username")
    Long countByUsername(@Param("username") String username);

    @Query("select f from Follower f where f.user.username = :username and f.surrogate.username = :surrogate and f.state = :state")
    Optional<Follower> findByUserSurrogateAndState(@Param("username") String username, @Param("surrogate") String surrogate, @Param("state") Follower.State state);

    @Query("select f from Follower f where f.user.username = :username and f.surrogate.username = :surrogate and f.state in('ACTIVE', 'BLOCKED')")
    Optional<Follower> findByUserSurrogateActiveState(@Param("username") String username, @Param("surrogate") String surrogate);

    @Query("select f from Follower f where f.surrogate.username = :surrogate and f.state = :state")
    Optional<Follower> findBySurrogateAndState(@Param("surrogate") String surrogate, @Param("state") Follower.State state);

    @Query("select f from Follower f where f.user.username = :username and f.state = :state")
    Optional<Follower> findByUsernameAndState(@Param("username") String username, @Param("state") Follower.State state);

    @Query("select f from Follower f where f.user.username = :username and f.state in('ACTIVE', 'BLOCKED')")
    List<Follower> findActiveBlockedFollowees(@Param("username") String username);

    @Query("select f from Follower f where f.surrogate.username = :username and f.state in('ACTIVE', 'BLOCKED')")
    List<Follower> findActiveBlockedFollowers(@Param("username") String username);
}
