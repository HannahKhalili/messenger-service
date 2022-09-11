package com.hana.assignment;

import com.hana.assignment.service.MessageService;
import com.sun.net.httpserver.HttpServer;
import com.hana.assignment.data.impl.InMemoryMessageRepository;
import com.hana.assignment.handler.RequestHandler;
import com.hana.assignment.service.impl.MessageServiceImpl;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {
        InMemoryMessageRepository messageRepository = new InMemoryMessageRepository();
        MessageService messageService = new MessageServiceImpl(messageRepository);
        RequestHandler handler = new RequestHandler(messageService);

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/", handler);
            server.start();
            System.out.println("Server started successfully on port 8080");
        } catch (IOException e) {
            System.err.println("Failed to start server on port 8080");
        }
    }
}
