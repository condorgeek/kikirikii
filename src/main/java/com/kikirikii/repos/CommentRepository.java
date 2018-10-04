/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [CommentRepository.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 02.05.18 16:41
 */

package com.kikirikii.repos;

import com.kikirikii.model.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.stream.Stream;

public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("select c from Comment c where c.post.id = :postId order by created desc")
    Stream<Comment> findAllByPostId(@Param("postId") Long postId);
}
