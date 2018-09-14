package com.kikirikii.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kikirikii.model.Event;
import com.kikirikii.model.User;
import com.kikirikii.model.dto.Topic;
import com.kikirikii.services.UserService;
import com.kikirikii.services.WebsocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
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

    // TODO CHAT and CHATENTRY entities with encryption
    @MessageMapping("/chat")
    @SendToUser("/topic/chat/simple")
    @SuppressWarnings("unchecked")
    public Map<String, String> chat(Principal principal, Map<String, String> values) {
        String message = values.get("message");
        String id = UUID.randomUUID().toString();
        User sendTo = userService.getUser(values.get("to"));

        websocketService.sendToUser(sendTo.getUsername(), Topic.CHAT,
                entry.apply("event", Event.EVENT_CHAT_SIMPLE.name()),
                entry.apply("id", id),
                entry.apply("message", message),
                entry.apply("from", principal.getName()));

        return WebsocketService.asMap(
                entry.apply("event", Event.EVENT_CHAT_ACK.name()),
                entry.apply("id", id),
                entry.apply("to", sendTo.getUsername()),
                entry.apply("from", principal.getName())
        );
    }


    private BiFunction<String, String, AbstractMap.SimpleEntry> entry = AbstractMap.SimpleEntry::new;

    Function<Object, String> toJSON = (o) -> {
        try {
            return mapper.writeValueAsString(o);
        } catch (Exception e) { // none }
            return null;
        }
    };

    Function<AbstractMap.SimpleEntry<String, String>[], Map<String, String>> asMap =  entries ->
        Arrays.stream(entries).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
}
