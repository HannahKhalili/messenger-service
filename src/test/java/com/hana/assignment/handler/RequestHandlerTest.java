package com.hana.assignment.handler;

import com.hana.assignment.service.MessageService;
import com.hana.assignment.data.impl.InMemoryMessageRepository;
import com.hana.assignment.service.impl.MessageServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class RequestHandlerTest {
    InMemoryMessageRepository messageRepository = new InMemoryMessageRepository();
    MessageService messageService = new MessageServiceImpl(messageRepository);
    RequestHandler requestHandler = new RequestHandler(messageService);


    @Test
    void getQueryParams() {
        String query = "k1=v1&k2=v2";
        Map<String, String> queryParams = requestHandler.getQueryParams(query);
        Assertions.assertEquals(2, queryParams.size());
        Assertions.assertEquals("v1", queryParams.get("k1"));
        Assertions.assertEquals("v2", queryParams.get("k2"));

        query = "k1=v1&k2=v2&k3";
        queryParams = requestHandler.getQueryParams(query);
        Assertions.assertEquals(3, queryParams.size());
        Assertions.assertEquals("", queryParams.get("k3"));

        query = "";
        queryParams = requestHandler.getQueryParams(query);
        Assertions.assertEquals(0, queryParams.size());

        query = null;
        queryParams = requestHandler.getQueryParams(query);
        Assertions.assertEquals(0, queryParams.size());

    }
}