package com.trioptima.hana.data;

import com.trioptima.hana.models.Message;

import java.util.List;


public interface MessageRepository {
    Message save(Message message);

    Message getById(Long messageId);

    List<Message> getAllMessagesByTimestamp(String receiver, Long from, Long to);

    List<Message> getAllMessagesByReceiver(String receiver);

    List<Message> getUnreadMessagesByTimestamp(String receiver, Long from, Long to);

    List<Message> getUnreadMessagesByReceiver(String receiver);

    void delete(Long messageId);

    void update(Message message);

    void deleteAll();

}
