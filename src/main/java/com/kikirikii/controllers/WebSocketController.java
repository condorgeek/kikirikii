package com.kikirikii.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
public class WebSocketController {
    private static Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    public WebSocketController() {
        logger.info("WebSocketController instantiated and waiting on /app/hello");
    }

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Map<String, String> greeting(Map<String, String> values) {
        String message = values.get("message");
        String from = values.get("from");

        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello and greetings from " + from);
        response.put("echo", message);

        return response;
    }

}
