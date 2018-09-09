package com.kikirikii.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class WebSocketController {
    private static Logger logger = LoggerFactory.getLogger(WebSocketController.class);

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

}
