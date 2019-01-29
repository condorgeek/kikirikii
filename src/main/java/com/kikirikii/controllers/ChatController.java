/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [ChatController.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 04.10.18 11:33
 */

package com.kikirikii.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kikirikii.model.Chat;
import com.kikirikii.model.ChatEntry;
import com.kikirikii.model.enums.Event;
import com.kikirikii.model.User;
import com.kikirikii.services.ChatService;
import com.kikirikii.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.AbstractMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping("/user/{userName}")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    UserService userService;

    @Autowired
    private ObjectMapper mapper;

    @RequestMapping(value = "/chat/{chatId}/entries", method = RequestMethod.GET)
    List<ChatEvent<ChatEntry>> getChatEntries(@PathVariable String userName, @PathVariable String chatId) {

        User user = userService.getUser(userName);
        Chat chat = chatService.getChat(chatId);

        return chatService.getChatEntries(chat).stream()
                .map(entry -> {
                    String event = entry.getFrom().equals(user.getUsername()) ? Event.EVENT_CHAT_DELIVERED_ACK.name() : Event.EVENT_CHAT_DELIVERED.name();
                    return new ChatEvent<>(event, entry);
                })
                .collect(Collectors.toList());

    }

    @RequestMapping(value = "/chat/{chatId}/count", method = RequestMethod.GET)
    @SuppressWarnings("unchecked")
    ChatEvent<Chat> getChatCount(@PathVariable String userName, @PathVariable String chatId) {

        User user = userService.getUser(userName);
        Chat chat = chatService.getChat(chatId);

        chat.setConsumed(chatService.getConsumedFromCount(chat.getId(), user.getUsername()));
        chat.setDelivered(chatService.getDeliveredToCount(chat.getId(), user.getUsername()));

        return new ChatEvent<>(Event.EVENT_CHAT_COUNT.name(), chat);
    }

    class ChatEvent <T>{
        private String event;
        private T data;

        public ChatEvent() {
        }

        public ChatEvent(String event, T data) {
            this.event = event;
            this.data = data;
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }

    private BiFunction<String, String, AbstractMap.SimpleEntry> entry = AbstractMap.SimpleEntry::new;

    private Function<Object, String> toJSON = (o) -> {
        try {
            return mapper.writeValueAsString(o);
        } catch (Exception e) { // none }
            return null;
        }
    };
}
