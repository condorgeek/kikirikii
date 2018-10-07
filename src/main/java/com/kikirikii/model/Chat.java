/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [Chat.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 26.09.18 19:46
 */

package com.kikirikii.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "chats")
public class Chat {
    public enum State {ACTIVE, BLOCKED, DELETED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "friend_id")
    private Friend friend;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "surrogate_id")
    private Friend surrogate;

    @NotNull
    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private State state;

    @Transient
    /* dynamic value - not saved */
    private long delivered;

    @Transient
    /* dynamic value - not saved */
    private long consumed;

    @NotNull
    private Date created;

    private Chat() {}

    public static Chat of(Friend friend, Friend surrogate) {
        Chat chat = new Chat();
        chat.friend = friend;
        chat.surrogate = surrogate;
        chat.state = State.ACTIVE;
        chat.created = new Date();
        return chat;
    }

    public Friend getFriend() {
        return friend;
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }

    public Friend getSurrogate() {
        return surrogate;
    }

    public void setSurrogate(Friend surrogate) {
        this.surrogate = surrogate;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public long getDelivered() {
        return delivered;
    }

    public void setDelivered(long delivered) {
        this.delivered = delivered;
    }

    public long getConsumed() {
        return consumed;
    }

    public void setConsumed(long consumed) {
        this.consumed = consumed;
    }

    public Chat incrementConsumed() {
        ++this.consumed;
        return this;
    }

    public Chat incrementDelivered() {
        ++this.delivered;
        return this;
    }

    public long getId() {
        return id;
    }
}
