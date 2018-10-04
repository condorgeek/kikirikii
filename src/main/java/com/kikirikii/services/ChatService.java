/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [ChatService.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 26.09.18 19:27
 */

package com.kikirikii.services;

import com.kikirikii.exceptions.InvalidResourceException;
import com.kikirikii.model.Chat;
import com.kikirikii.model.ChatEntry;
import com.kikirikii.repos.ChatEntryRepository;
import com.kikirikii.repos.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DELIVERED - the chat entry has been delivered but not read yet
 * CONSUMED - the chat entry has been delivered and read
 *
 * EVENT_CHAT_DELIVERED - client incoming chat entry
 * EVENT_CHAT_DELIVERED_ACK - client outgoing chat entry
 */
@Service
@Transactional
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatEntryRepository chatEntryRepository;

    public ChatEntry saveChatEntryAsDelivered(Chat chat, String from, String to, String message) {
        return chatEntryRepository.save(ChatEntry.of(chat, from, to, message, ChatEntry.State.DELIVERED));
    }

    public ChatEntry saveChatEntry(Chat chat, String from, String to, String message) {
        return chatEntryRepository.save(ChatEntry.of(chat, from, to, message));
    }

    public ChatEntry saveChatEntry(ChatEntry chatEntry) {
        return chatEntryRepository.save(chatEntry);
    }

    public Chat getChat(String id) {
        try {
            Optional<Chat> chat = chatRepository.findById(new Long(id));
            if(chat.isPresent()) {
                return chat.get();
            }
        } catch(Exception e) { /*empty*/ }

        throw new InvalidResourceException("Chat Id " + id + " is invalid.");
    }

    public ChatEntry getChatEntry(String id) {
        try {
            Optional<ChatEntry> chatEntry = chatEntryRepository.findById(new Long(id));
            if(chatEntry.isPresent()) {
                return chatEntry.get();
            }
        } catch (Exception e) {/* empty */}

        throw new InvalidResourceException("ChatEntry Id " + id + " is invalid.");
    }

    public List<ChatEntry> getChatEntries(Chat chat) {
        return chatEntryRepository.findAllByChatId(chat.getId());
    }

    public Long getDeliveredToCount(Long id, String username) {
        return chatEntryRepository.countDeliveredToByChatId(id, username);
    }

    public Long getConsumedFromCount(Long id, String username) {
        return chatEntryRepository.countConsumedFromByChatId(id, username);
    }

    public Stream<ChatEntry> getChatEntriesAsStream(Chat chat) {
        return chatEntryRepository.findAllByChatIdAsStream(chat.getId());
    }

    public static Map<String, String> asMap(AbstractMap.SimpleEntry<String, String>... messages) {
        return Arrays.stream(messages).collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue()));
    }
}
