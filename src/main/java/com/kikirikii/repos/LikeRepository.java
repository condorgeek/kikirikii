/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [LikeRepository.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 02.05.18 19:12
 */

package com.kikirikii.repos;

import com.kikirikii.model.Like;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.stream.Stream;

public interface LikeRepository extends CrudRepository<Like, Long> {

    @Query("select l from Like l where l.post.id = :postId")
    Stream<Like> findAllByPostId(@Param("postId") Long postId);

    @Query("select l from Like l where l.post.id = :postId and l.state = 'ACTIVE'")
    List<Like> findAllActiveByPostId(@Param("postId") Long postId);
}
