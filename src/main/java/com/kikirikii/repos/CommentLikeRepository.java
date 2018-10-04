/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [CommentLikeRepository.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 02.05.18 19:58
 */

package com.kikirikii.repos;

import com.kikirikii.model.CommentLike;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.stream.Stream;

public interface CommentLikeRepository extends CrudRepository<CommentLike, Long> {

    @Query("select l from CommentLike l where l.comment.id = :commentId")
    Stream<CommentLike> findAllByCommentId(@Param("commentId") Long commentId);
}
