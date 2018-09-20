package com.kikirikii.controllers;

import com.kikirikii.model.Chat;
import com.kikirikii.model.ChatEntry;
import com.kikirikii.model.Event;
import com.kikirikii.model.User;
import com.kikirikii.services.ChatService;
import com.kikirikii.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping("/user/{userName}")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/chat/{chatId}/entries", method = RequestMethod.GET)
    List<ChatEvent> getChatEntries(@PathVariable String userName, @PathVariable String chatId) {

        User user = userService.getUser(userName);
        Chat chat = chatService.getChat(chatId);

        return chatService.getChatEntries(chat).stream()
                .map(entry -> {
                    String event = entry.getFrom().equals(user.getUsername()) ? Event.EVENT_CHAT_ACK.name() : Event.EVENT_CHAT_SIMPLE.name();
                    return new ChatEvent(event, entry);
                })
                .collect(Collectors.toList());
    }

    class ChatEvent {
        private String event;
        private ChatEntry data;

        public ChatEvent() {
        }

        public ChatEvent(String event, ChatEntry data) {
            this.event = event;
            this.data = data;
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public ChatEntry getData() {
            return data;
        }

        public void setData(ChatEntry data) {
            this.data = data;
        }
    }
}
