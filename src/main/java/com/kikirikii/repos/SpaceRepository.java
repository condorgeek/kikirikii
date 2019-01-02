/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [SpaceRepository.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 07.05.18 14:32
 */

package com.kikirikii.repos;

import com.kikirikii.model.Space;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface SpaceRepository extends CrudRepository<Space, Long> {

    @Query("select s from Space s where s.user.id = :userId and s.type = 'HOME'")
    Optional<Space> findHomeSpace(@Param("userId") Long userId);

    @Query("select s from Space s where s.user.id = :userId and s.type = 'GLOBAL'")
    Optional<Space> findGlobalSpace(@Param("userId") Long userId);

    @Query("select s from Space s where s.name = :spacename")
    Optional<Space> findBySpacename(@Param("spacename") String spacename);

    @Query("select s from Space s where s.user.id = :userId and s.type not in ('GLOBAL', 'HOME')")
    Stream<Space> findAllByUserId(@Param("userId") Long userId);

    /* find PUBLIC and RESTRICTED access */
    @Query("select s from Space s where s.user.id = :userId and s.state = 'ACTIVE' and s.type = 'GENERIC'")
    List<Space> findActiveByUserId(@Param("userId") Long userId);

    /* find PUBLIC and RESTRICTED access */
    @Query("select s from Space s where s.user.id = :userId and s.state = 'ACTIVE' and s.type = 'EVENT'")
    List<Space> findActiveEventsByUserId(@Param("userId") Long userId);

    /* find PUBLIC and RESTRICTED access */
    @Query("select s from Space s where s.user.id = :userId and s.state = 'ACTIVE' and s.type = 'SHOP'")
    List<Space> findActiveShopsByUserId(@Param("userId") Long userId);

    @Query("select count(m) from Member m where m.space.id = :spaceId")
    Long countBySpaceId(@Param("spaceId") Long spaceId);

    @Query("select count(m) from Member m where m.space.id = :spaceId and m.state='ACTIVE'")
    Long countActiveBySpaceId(@Param("spaceId") Long spaceId);


}
