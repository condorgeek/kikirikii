package com.kikirikii.services;

import com.kikirikii.exceptions.InvalidResourceException;
import com.kikirikii.model.Chat;
import com.kikirikii.model.ChatEntry;
import com.kikirikii.repos.ChatEntryRepository;
import com.kikirikii.repos.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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

    public Stream<ChatEntry> getChatEntriesAsStream(Chat chat) {
        return chatEntryRepository.findAllByChatIdAsStream(chat.getId());
    }
}
