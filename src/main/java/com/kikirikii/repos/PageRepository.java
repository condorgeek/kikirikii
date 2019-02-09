/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [PageRepository.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 08.02.19 20:14
 */

package com.kikirikii.repos;

import com.kikirikii.model.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PageRepository extends CrudRepository<Page, Long> {

    @Query("select p from Page p where lower(p.name) = lower(:name) and p.state = 'ACTIVE'")
    Optional<Page> findByPageName(@Param("name") String name);

    @Query("select p from Page p where p.state = 'ACTIVE' order by p.ranking desc")
    List<Page> findAllByRanking();
}
