package com.kikirikii.model.dto;

public enum Topic {
    GENERIC("/topic/event/generic"),
    CHAT("/topic/chat/simple");

    String path;

    Topic(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
