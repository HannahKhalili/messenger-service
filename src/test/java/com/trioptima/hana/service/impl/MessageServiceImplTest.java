package com.trioptima.hana.service.impl;

import com.trioptima.hana.data.MessageRepository;
import com.trioptima.hana.data.impl.InMemoryMessageRepository;
import com.trioptima.hana.models.Message;
import com.trioptima.hana.service.MessageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class MessageServiceImplTest {
    MessageRepository messageRepository = new InMemoryMessageRepository();
    MessageService messageService = new MessageServiceImpl(messageRepository);

    @BeforeEach
    void clear() {
        messageRepository.deleteAll();
    }

    @Test
    void submit() {
        messageService.submit("test-user1", "Hello");
        messageService.submit("test-user2", "Bye");


        List<Message> messageIds = messageRepository.getAllMessagesByReceiver("test-user1");
        Assertions.assertEquals(1, messageIds.size());
        Assertions.assertEquals("Hello", messageRepository.getById(messageIds.get(0).getId()).getText());

        messageIds = messageRepository.getAllMessagesByReceiver("test-user2");
        Assertions.assertEquals(1, messageIds.size());
        Assertions.assertEquals("Bye", messageRepository.getById(messageIds.get(0).getId()).getText());
    }

    @Test
    void getReceivedMessage() {
        Message message = new Message("test-user3", "test");

        messageRepository.save(message);

        List<Message> receivedMessage = messageService.getReceivedMessage("test-user3", true);
        Assertions.assertEquals(message.getReceiverId(), receivedMessage.get(0).getReceiverId());
        Assertions.assertEquals(message.getText(), receivedMessage.get(0).getText());


        receivedMessage = messageService.getReceivedMessage("test-user3", true);
        Assertions.assertEquals(0, receivedMessage.size());

        receivedMessage = messageService.getReceivedMessage("test-user3", false);
        Assertions.assertEquals(1, receivedMessage.size());

    }

    @Test
    void getReceivedMessageWithTime() throws InterruptedException {
        long time1 = System.currentTimeMillis();
        messageService.submit("test-user", "message1");

        Thread.sleep(100);
        long time2 = System.currentTimeMillis();
        Thread.sleep(100);

        messageService.submit("test-user", "message2");

        Thread.sleep(100);
        long time3 = System.currentTimeMillis();
        Thread.sleep(100);

        List<Message> receivedMessage =
                messageService.getReceivedMessage("test-user", time1, time2, true);
        Assertions.assertEquals(1, receivedMessage.size());
        Assertions.assertEquals("message1",receivedMessage.get(0).getText());

        receivedMessage =
                messageService.getReceivedMessage("test-user", time2, time3, true);
        Assertions.assertEquals(1, receivedMessage.size());
        Assertions.assertEquals("message2",receivedMessage.get(0).getText());


        receivedMessage =
                messageService.getReceivedMessage("test-user", time1, time3, false);
        Assertions.assertEquals(2, receivedMessage.size());
    }


    @Test
    void delete() {
        Message message = new Message("test-user", "test");

        Message saved = messageRepository.save(message);

        messageService.delete(saved.getId());
        List<Message> receivedMessage = messageService.getReceivedMessage("test-user", true);
        Assertions.assertEquals(0, receivedMessage.size());

    }

}