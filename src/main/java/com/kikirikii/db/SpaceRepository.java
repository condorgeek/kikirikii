package com.kikirikii.db;

import com.kikirikii.model.Space;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.stream.Stream;

public interface SpaceRepository extends CrudRepository<Space, Long> {

    @Query("select s from Space s where s.user.id = :userId and s.type = 'HOME'")
    Optional<Space> findHomeSpace(@Param("userId") String userId);

    @Query("select s from Space s where s.user.id = :userId and s.type = 'GLOBAL'")
    Optional<Space> findGlobalSpace(@Param("userId") String userId);

    @Query("select s from Space s where s.user.id = :userId and s.type not in ('GLOBAL', 'HOME')")
    Stream<Space> findAllByUserId(@Param("userId") String userId);
}
