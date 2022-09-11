package com.trioptima.hana.service;

import com.trioptima.hana.models.Message;

import java.util.List;

public interface MessageService {
    void submit(String userId, String text);

    Message getMessageById(Long id);

    List<Message> getReceivedMessage(String receiver, boolean onlyUnread);

    List<Message> getReceivedMessage(String receiver, long from, long to ,boolean onlyUnread);

    void delete(Long messageId);

    void delete(List<Long> list);

}
