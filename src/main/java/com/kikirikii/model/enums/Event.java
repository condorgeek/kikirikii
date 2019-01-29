/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [Event.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 04.10.18 11:35
 */

package com.kikirikii.model.enums;

public enum Event {
    EVENT_FRIEND_REQUESTED,
    EVENT_FRIEND_CANCELLED,
    EVENT_FRIEND_ACCEPTED,
    EVENT_FRIEND_IGNORED,
    EVENT_FRIEND_DELETED,
    EVENT_FRIEND_BLOCKED,
    EVENT_FRIEND_UNBLOCKED,
    EVENT_FOLLOWER_BLOCKED,
    EVENT_FOLLOWER_UNBLOCKED,
    EVENT_FOLLOWER_ADDED,
    EVENT_FOLLOWER_DELETED,

    EVENT_CHAT_DELIVERED,
    EVENT_CHAT_DELIVERED_ACK,
    EVENT_CHAT_CONSUMED,
    EVENT_CHAT_CONSUMED_ACK,
    EVENT_CHAT_DELETED,
    EVENT_CHAT_DELETED_ACK,
    EVENT_CHAT_COUNT
}
