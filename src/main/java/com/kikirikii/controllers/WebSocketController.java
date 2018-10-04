/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [WebSocketController.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 04.10.18 11:34
 */

package com.kikirikii.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kikirikii.model.Chat;
import com.kikirikii.model.ChatEntry;
import com.kikirikii.model.Event;
import com.kikirikii.model.User;
import com.kikirikii.model.dto.Topic;
import com.kikirikii.services.ChatService;
import com.kikirikii.services.UserService;
import com.kikirikii.services.WebsocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * client code subscribes using prefix /app ie. /user for ex. /app/message or /user/topic/event/generic
 */
@Controller
public class WebSocketController {
    private static Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private WebsocketService websocketService;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatService chatService;


    public WebSocketController() {
        logger.info("WebSocketController instantiated and waiting on /app/message");
    }

    @MessageMapping("/message")
    @SendToUser("/topic/event/generic")
    public Map<String, String> message(Principal principal, Map<String, String> values) {
        String message = values.get("message");
        String from = values.get("from");

        logger.info("Message from " + principal.getName());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello and greetings from " + from);
        response.put("echo", message);

        return response;
    }

    @MessageMapping("/chat/deliver")
    @SendToUser("/topic/chat/simple")
    @SuppressWarnings("unchecked")
    public Map<String, String> deliverChatEntry(Principal principal, Map<String, String> values) {
        String message = values.get("message");

        Chat chat = chatService.getChat(values.get("id"));
        User sendTo = userService.getUser(values.get("to"));

        ChatEntry chatEntry = chatService.saveChatEntryAsDelivered(chat.incrementDelivered(),
                principal.getName(), sendTo.getUsername(), message);

        websocketService.sendToUser(sendTo.getUsername(), Topic.CHAT_SIMPLE,
                entry.apply("event", Event.EVENT_CHAT_DELIVERED.name()),
                entry.apply("data", toJSON.apply(chatEntry)));

        return WebsocketService.asMap(
                entry.apply("event", Event.EVENT_CHAT_DELIVERED_ACK.name()),
                entry.apply("data", toJSON.apply(chatEntry)));
    }

    @MessageMapping("/chat/consume")
    @SendToUser("/topic/chat/simple")
    @SuppressWarnings("unchecked")
    public Map<String, String> consumeChatEntry(Principal principal, Map<String, String> values) {
        Chat chat = chatService.getChat(values.get("id"));
        User sendTo = userService.getUser(values.get("to"));

        ChatEntry chatEntry = chatService.getChatEntry(values.get("entryId"));
        chatEntry = chatService.saveChatEntry(chatEntry.setState(ChatEntry.State.CONSUMED));

        // TODO Chat: store simple username strings instead of full friend entities
        websocketService.sendToUser(sendTo.getUsername(), Topic.CHAT_SIMPLE,
                entry.apply("event", Event.EVENT_CHAT_CONSUMED.name()),
                entry.apply("data", toJSON.apply(chatEntry)));

        return WebsocketService.asMap(
                entry.apply("event", Event.EVENT_CHAT_CONSUMED_ACK.name()),
                entry.apply("data", toJSON.apply(chatEntry)));
    }

    private BiFunction<String, String, AbstractMap.SimpleEntry> entry = AbstractMap.SimpleEntry::new;

    private Function<Object, String> toJSON = (o) -> {
        try {
            return mapper.writeValueAsString(o);
        } catch (Exception e) { // none }
            return null;
        }
    };

    Function<AbstractMap.SimpleEntry<String, String>[], Map<String, String>> asMap = entries ->
            Arrays.stream(entries).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
}
