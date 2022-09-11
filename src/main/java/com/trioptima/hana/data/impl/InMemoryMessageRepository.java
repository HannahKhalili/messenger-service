package com.trioptima.hana.data.impl;

import com.trioptima.hana.data.MessageRepository;
import com.trioptima.hana.models.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryMessageRepository implements MessageRepository {

    private final Map<Long, Message> data;
    private final Map<String, List<Long>> receivedIndex;
    private Long counter;

    public InMemoryMessageRepository() {
        counter = 0L;
        data = new ConcurrentHashMap<>();
        receivedIndex = new ConcurrentHashMap<>();
    }

    @Override
    public Message save(Message msg) {
        Message message = new Message(msg.getReceiverId(), msg.getText());
        message.setId(counter++);
        message.setTimestamp(System.currentTimeMillis());
        message.setSeen(false);

        data.put(message.getId(), message);

        receivedIndex.computeIfAbsent(message.getReceiverId(), key -> new ArrayList<>());

        List<Long> list = receivedIndex.get(message.getReceiverId());
        list.add(message.getId());

        return message;
    }

    @Override
    public Message getById(Long messageId) {
        return data.get(messageId);
    }

    @Override
    public List<Message> getAllMessagesByTimestamp(String receiver, Long from, Long to) {
        List<Message> messages = getAllMessagesByReceiver(receiver);
        List<Message> result = new ArrayList<>();
        for (Message message : messages) {
            if (message.getTimestamp() >= from && message.getTimestamp() <= to) {
                result.add(message);
            }
        }
        return result;
    }

    @Override
    public List<Message> getAllMessagesByReceiver(String receiver) {
        List<Message> result = new ArrayList<>();
        List<Long> list = receivedIndex.get(receiver);
        if (list != null) {
            for (Long messageId : list) {
                result.add(data.get(messageId));
            }
        }
        return result;
    }

    @Override
    public List<Message> getUnreadMessagesByTimestamp(String receiver, Long from, Long to) {
        List<Message> messages = getAllMessagesByTimestamp(receiver, from, to);
        List<Message> result = new ArrayList<>();
        for (Message message : messages) {
            if (!message.isSeen()) {
                result.add(message);
            }
        }
        return result;
    }

    @Override
    public List<Message> getUnreadMessagesByReceiver(String receiver) {
        List<Message> messages = getAllMessagesByReceiver(receiver);
        List<Message> result = new ArrayList<>();
        for (Message message : messages) {
            if (!message.isSeen()) {
                result.add(message);
            }
        }
        return result;
    }

    @Override
    public void delete(Long messageId) {
        Message message = getById(messageId);
        if (message != null) {
            List<Long> list = receivedIndex.get(message.getReceiverId());
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).equals(messageId)) {
                    list.remove(i);
                    break;
                }
            }
        }
        data.remove(messageId);

    }

    @Override
    public void update(Message message) {
        data.put(message.getId(), message);
    }

    @Override
    public void deleteAll() {
        data.clear();
        receivedIndex.clear();
        counter = 0L;
    }
}
