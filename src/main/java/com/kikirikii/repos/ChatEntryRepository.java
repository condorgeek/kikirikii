package com.kikirikii.repos;

import com.kikirikii.model.ChatEntry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatEntryRepository extends CrudRepository<ChatEntry, Long> {

    @Query("select c from ChatEntry c where c.chat.id = :chatid and c.state = 'ACTIVE' order by c.created asc")
    List<ChatEntry> findAllByChatId(@Param("chatid") Long chatId);
}
