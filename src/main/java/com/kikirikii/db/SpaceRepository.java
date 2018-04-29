package com.kikirikii.db;

import com.kikirikii.model.Post;
import com.kikirikii.model.Space;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.stream.Stream;

public interface SpaceRepository extends CrudRepository<Space, Long> {

//    Optional<Space> findByUserIdAndType(User user, Space.Type type);

    @Query("select s from Space s where s.user.id = :userId and s.type = 'HOME'")
    Optional<Space> findHomeSpace(@Param("userId") String userId);

    @Query("select s from Space s where s.user.id = :userId and s.type = 'GLOBAL'")
    Optional<Space> findGlobalSpace(@Param("userId") String userId);

    @Query("select p from Post p where p.space.id = :spaceId order by created desc")
    Stream<Post> findAllPostsById(@Param("spaceId") Long spaceId);
}
