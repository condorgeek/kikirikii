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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends CrudRepository<Member, Long> {

    @Query("select m from Member m where m.space.id = :spaceId")
    List<Member> findAllBySpaceId(@Param("spaceId") Long spaceId);

    @Query("select m from Member m where m.space.id = :spaceId and state= 'ACTIVE'")
    List<Member> findActiveBySpaceId(@Param("spaceId") Long spaceId);

    @Query("select m from Member m where m.user.id = :userId and state = 'ACTIVE' " +
            "and m.space.type = 'GENERIC' and m.space.state = 'ACTIVE'")
    List<Member> findMemberOfGenericByUserId(@Param("userId") Long userId);

    @Query("select m from Member m where m.space.id = :spaceId and m.user.id = :userId")
    Optional<Member> findMemberByUserId(@Param("spaceId") Long spaceId, @Param("userId") Long userId);

}
