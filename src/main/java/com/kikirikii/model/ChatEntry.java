package com.kikirikii.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "chat_entries")
public class ChatEntry {

    public enum State {ACTIVE, DELETED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Column(name = "username_from")
    private String from;

    @Column(name = "username_to")
    private String to;

    @Column(columnDefinition = "text", length=10485760)
    private String text;

    @NotNull
    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private State state;

    @NotNull
    private Date created;

    private ChatEntry() {}

    public static ChatEntry of(Chat chat, String from, String to, String text) {
        ChatEntry entry = new ChatEntry();
        entry.chat = chat;
        entry.text = text;
        entry.from = from;
        entry.to = to;
        entry.state = State.ACTIVE;
        entry.created = new Date();
        return entry;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}