/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [WidgetRepository.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 06.02.19 09:57
 */

package com.kikirikii.repos;

import com.kikirikii.model.Widget;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WidgetRepository extends CrudRepository<Widget, Long> {

    @Query("select w from Widget w where w.state = 'ACTIVE' order by w.ranking desc, w.created desc")
    List<Widget> findAll();

    @Query("select w from Widget w where w.pos = :position and w.state = 'ACTIVE' order by w.ranking desc, w.created desc")
    List<Widget> findAllByPosition(@Param("position") Widget.Position position);
}
