/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [ChatEntryRepository.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 26.09.18 19:27
 */

package com.kikirikii.repos;

import com.kikirikii.model.ChatEntry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.stream.Stream;

public interface ChatEntryRepository extends CrudRepository<ChatEntry, Long> {

    @Query("select c from ChatEntry c where c.chat.id = :chatid and c.state in ('ACTIVE', 'CONSUMED', 'DELIVERED') order by c.created asc")
    List<ChatEntry> findAllByChatId(@Param("chatid") Long chatId);

    @Query("select c from ChatEntry c where c.chat.id = :chatid and c.state in ('ACTIVE', 'CONSUMED', 'DELIVERED') order by c.created asc")
    Stream<ChatEntry> findAllByChatIdAsStream(@Param("chatid") Long chatId);

    @Query("select count(*) from ChatEntry c where c.chat.id = :chatid and c.to = :username and c.state = 'DELIVERED'")
    Long countDeliveredToByChatId(@Param("chatid") Long chatId, @Param("username") String username);

    @Query("select count(*) from ChatEntry c where c.chat.id = :chatid and c.from = :username and c.state = 'CONSUMED'")
    Long countConsumedFromByChatId(@Param("chatid") Long chatId, @Param("username") String username);

}
