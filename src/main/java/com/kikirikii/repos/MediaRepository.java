/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [MediaRepository.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 02.05.18 16:41
 */

package com.kikirikii.repos;

import com.kikirikii.model.Media;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.stream.Stream;

public interface MediaRepository extends CrudRepository<Media, Long> {

    @Query("select m from Media m where m.post.id = :postId")
    Stream<Media> findAllByPostId(@Param("postId") Long postId);

    @Query("select m from Media m where m.post.user.id = :userId and m.state = 'ACTIVE' and m.post.state = 'ACTIVE'")
    List<Media> findAllByUserId(@Param("userId") Long userId);

    @Query("select m from Media m where m.post.user.id = :userId and m.post.space.id = :spaceId and m.state = 'ACTIVE' and m.post.state = 'ACTIVE'")
    List<Media> findMediaByUserIdAndSpaceId(@Param("userId") Long userId, @Param("spaceId") Long spaceId);

    @Query("select m from Media m where m.post.user.username = :username and m.state = 'ACTIVE' and m.post.state = 'ACTIVE'")
    List<Media> findAllByUsername(@Param("username") String username);

}
