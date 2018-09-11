package com.kikirikii.services;

import com.kikirikii.model.dto.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * /topic/event/generic - generic event signaling
 * /topic/chat - chat conversation 1-to-1
 */
@Service
public class WebsocketService {

    @Autowired
    private SimpMessagingTemplate template;

    public void sendToUser(String username, Topic topic, Map<String, String> message) {
        template.convertAndSendToUser(username, topic.getPath(), message);
    }

    public void sendToUser(String username, Topic topic, AbstractMap.SimpleEntry<String, String>... messages) {

        template.convertAndSendToUser(username, topic.getPath(),
                Arrays.stream(messages).collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue())));
    }


}
