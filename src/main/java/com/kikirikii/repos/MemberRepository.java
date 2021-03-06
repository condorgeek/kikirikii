/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [MemberRepository.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 21.10.18 16:32
 */

package com.kikirikii.repos;

import com.kikirikii.model.Member;
import com.kikirikii.model.Space;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends CrudRepository<Member, Long> {

    @Query("select m from Member m where m.space.id = :spaceId")
    List<Member> findAllBySpaceId(@Param("spaceId") Long spaceId);

    @Query("select m from Member m where m.space.id = :spaceId and m.state= 'ACTIVE'")
    List<Member> findActiveBySpaceId(@Param("spaceId") Long spaceId);

    @Query("select m from Member m where m.space.id = :spaceId and m.state= 'ACTIVE' order by m.user.ranking desc, m.user.lastname asc")
    Page<Member> findActivePageBySpaceId(@Param("spaceId") Long spaceId, Pageable page);

    @Query("select m from Member m where m.user.id = :userId and m.state = 'ACTIVE' " +
            "and m.space.type = 'GENERIC' and m.space.state = 'ACTIVE' order by m.user.ranking desc, m.user.lastname asc")
    List<Member> findMemberOfGenericByUserId(@Param("userId") Long userId);

    /* only parent space nodes */
    @Query("select m from Member m where m.user.id = :userId and m.state = 'ACTIVE' " +
            "and m.space.type = :type and m.space.state = 'ACTIVE' and m.space.parent = null order by m.user.ranking desc, m.space.ranking desc")
    List<Member> findMemberOfByTypeAndUserId(@Param("type") Space.Type type, @Param("userId") Long userId);

    @Query("select m from Member m where m.user.id = :userId and m.state = 'ACTIVE' " +
            "and m.space.type = :type and m.space.state = 'ACTIVE' and m.space.access = 'PUBLIC' and m.space.parent = null order by m.user.ranking desc, m.space.ranking desc")
    List<Member> findPublicByTypeAndUserId(@Param("type") Space.Type type, @Param("userId") Long userId);

    @Query("select m from Member m where m.space.id = :spaceId and m.user.id = :userId")
    List<Member> findMemberByUserId(@Param("spaceId") Long spaceId, @Param("userId") Long userId);

    @Query("select m from Member m where m.space.id = :spaceId and m.user.username = :username and m.state = 'ACTIVE'")
    Optional<Member> findMemberByUsername(@Param("spaceId") Long spaceId, @Param("username") String username);

    @Query("select m from Member m where m.space.id = :spaceId and m.user.id = :userId and m.state = 'ACTIVE'")
    Optional<Member> findActiveMemberByUserId(@Param("spaceId") Long spaceId, @Param("userId") Long userId);

    @Query("select m from Member m where m.space.id = :spaceId and m.user.username = :username and m.state = 'ACTIVE'")
    Optional<Member> findActiveMemberByUsername(@Param("spaceId") Long spaceId, @Param("username") String username);

}
