package com.kikirikii.services;

import com.kikirikii.controllers.UserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebsocketService {

    @Autowired
    private SimpMessagingTemplate template;

    public void sendToUser(String username, Map<String, String> message) {
        template.convertAndSendToUser(username, "/topic/greetings", message);
    }

    public void sendToUser(String username, AbstractMap.SimpleEntry<String, String>... messages) {
        Map<String, String> message = new HashMap<>();
        Arrays.stream(messages).forEach(entry -> message.put(entry.getKey(), entry.getValue()));

        template.convertAndSendToUser(username, "/topic/greetings", message);
    }

}
