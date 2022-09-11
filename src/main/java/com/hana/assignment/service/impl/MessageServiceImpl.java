package com.hana.assignment.service.impl;

import com.hana.assignment.data.MessageRepository;
import com.hana.assignment.models.Message;
import com.hana.assignment.service.MessageService;

import java.util.List;

public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }


    @Override
    public void submit(String userId, String text) {
        Message message = new Message(userId, text);
        messageRepository.save(message);
    }

    @Override
    public Message getMessageById(Long id) {
        return messageRepository.getById(id);
    }


    private void updateSeenStatus(List<Message> messages) {
        for (Message message : messages) {
            if (!message.isSeen()) {
                message.setSeen(true);
                messageRepository.update(message);
            }
        }
    }

    private void sort(List<Message> messages) {
        messages.sort((m1, m2) -> {
            if (m1.getTimestamp() > m2.getTimestamp()) {
                return 1;
            } else if (m1.getTimestamp() < m2.getTimestamp()) {
                return -1;
            }
            return 0;
        });
    }

    @Override
    public List<Message> getReceivedMessage(String receiver, boolean onlyUnread) {
        List<Message> messages;
        if (onlyUnread) {
            messages = messageRepository.getUnreadMessagesByReceiver(receiver);
        } else {
            messages = messageRepository.getAllMessagesByReceiver(receiver);
        }
        if(messages.size()==0){
            return messages;
        }
        updateSeenStatus(messages);
        sort(messages);
        return messages;
    }

    @Override
    public List<Message> getReceivedMessage(String receiver, long from, long to, boolean onlyUnread) {
        List<Message> messages;
        if (onlyUnread) {
            messages = messageRepository.getUnreadMessagesByTimestamp(receiver, from, to);
        } else {
            messages = messageRepository.getAllMessagesByTimestamp(receiver, from, to);
        }
        updateSeenStatus(messages);
        sort(messages);
        return messages;
    }

    @Override
    public void delete(Long messageId) {
        messageRepository.delete(messageId);
    }

    @Override
    public void delete(List<Long> list) {
        for (Long id : list) {
            delete(id);
        }

    }


}
