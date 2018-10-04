/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [Topic.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 24.09.18 13:00
 */

package com.kikirikii.model.dto;

public enum Topic {
    GENERIC("/topic/event/generic"),
    CHAT_SIMPLE("/topic/chat/simple");

    String path;

    Topic(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
