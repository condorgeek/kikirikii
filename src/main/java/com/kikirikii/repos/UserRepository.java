package com.kikirikii.repos;

import com.kikirikii.model.Space;
import com.kikirikii.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.stream.Stream;

public interface UserRepository extends CrudRepository<User, String> {
    Optional<User> findByUsername(String username);

    @Query("select s from Space s where s.user.id = :userId and s.type = 'HOME'")
    Optional<Space> findHomeSpace(@Param("userId") Long userId);

    @Query("select s from Space s where s.user.id = :userId and s.type = 'GLOBAL'")
    Optional<Space> findGlobalSpace(@Param("userId") Long userId);

    @Query("select s from Space s where s.user.username = :username and s.type = 'HOME'")
    Optional<Space> findHomeSpaceByName(@Param("username") String username);

    @Query("select s from Space s where s.user.username = :username and s.type = 'GLOBAL'")
    Optional<Space> findGlobalSpaceByName(@Param("username") String username);

    @Query("select u from User u where u.state = 'ACTIVE'")
    Stream<User> findAllActiveUsers();

}