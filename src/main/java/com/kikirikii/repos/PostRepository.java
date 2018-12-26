/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [PostRepository.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 25.05.18 19:06
 */

package com.kikirikii.repos;

import com.kikirikii.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.stream.Stream;

public interface PostRepository extends PagingAndSortingRepository<Post, Long> {

    @Query("select p from Post p where p.space.id = :spaceId and p.state in ('ACTIVE', 'SHARED') order by created desc")
    Stream<Post> findActiveBySpaceId(@Param("spaceId") Long spaceId);

    @Query("select p from Post p where p.space.id = :spaceId and p.state in ('ACTIVE', 'SHARED') order by created desc")
    Page<Post> findActivePageBySpaceId(@Param("spaceId") Long spaceId, Pageable pageable);
}
