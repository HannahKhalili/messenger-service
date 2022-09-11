package com.trioptima.hana.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.trioptima.hana.models.Message;
import com.trioptima.hana.service.MessageService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHandler implements HttpHandler {
    private final MessageService messageService;

    public RequestHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        Map<String, String> queryParams = getQueryParams(exchange.getRequestURI().getQuery());
        String method = exchange.getRequestMethod().toUpperCase();

        if (!path.equals("/messages")) {
            respond(exchange, 404, "not found");
            return;
        }

        switch (method) {
            case "POST": {
                String receiver = queryParams.get("receiver");
                if (receiver == null || receiver.equals("")) {
                    respond(exchange, 400, "Undefined receiver");
                    return;
                }
                String text = getRequestBody(exchange);
                if (text.equals("")) {
                    respond(exchange, 400, "Empty text is not allowed");
                    return;
                }
                messageService.submit(receiver, text);
                respond(exchange, 201, "OK");
                return;
            }
            case "GET": {
                boolean onlyUnread = Boolean.parseBoolean(queryParams.get("only-unread"));
                String receiver = queryParams.get("receiver");
                if (receiver == null || receiver.equals("")) {
                    respond(exchange, 400, "Undefined receiver");
                    return;
                }
                String from = queryParams.get("from");
                String to = queryParams.get("to");
                if (from != null && to != null) {
                    try {
                        long startTime = Long.parseLong(from);
                        long endTime = Long.parseLong(to);
                        String receivedMessage = listToString(messageService.getReceivedMessage(receiver, startTime, endTime, onlyUnread));
                        respond(exchange, 200, receivedMessage);
                        return;
                    } catch (NumberFormatException e) {
                        respond(exchange, 400, "invalid time");
                        return;
                    }
                } else {
                    String receivedMessage = listToString(messageService.getReceivedMessage(receiver, onlyUnread));
                    respond(exchange, 200, receivedMessage);
                    return;
                }
            }
            case "DELETE": {
                String messageIdsParam = queryParams.get("messageIds");

                String[] messageIds = messageIdsParam.split(",");
                if (messageIds.length == 0) {
                    respond(exchange, 400, "messageId is required");
                    return;
                }
                List<Long> msgIds = new ArrayList<>();
                for (String messageId : messageIds) {
                    long id;
                    try {
                        id = Long.parseLong(messageId);
                        msgIds.add(Long.parseLong(messageId));
                    } catch (NumberFormatException e) {
                        respond(exchange, 400, "invalid id");
                        return;
                    }
                    if (messageService.getMessageById(id) != null) {
                        respond(exchange, 404, "message not found");
                        return;
                    }
                }
                messageService.delete(msgIds);
                respond(exchange, 200, "message deleted");
            }

        }

    }


    private String listToString(List<Message> list) {
        StringBuilder builder = new StringBuilder();
        for (Message message : list) {
            builder.append(message.toString()).append("\n");
        }
        return builder.toString();
    }

    private void respond(HttpExchange exchange, Integer httpStatus, String response) throws IOException {
        OutputStream outputStream = exchange.getResponseBody();
        exchange.sendResponseHeaders(httpStatus, response.length());
        outputStream.write(response.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    private String getRequestBody(HttpExchange exchange) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(exchange.getRequestBody());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        int character;
        StringBuilder builder = new StringBuilder();
        while ((character = bufferedReader.read()) != -1) {
            builder.append((char) character);
        }
        bufferedReader.close();
        inputStreamReader.close();
        return builder.toString();
    }

    Map<String, String> getQueryParams(String query) {
        if (query == null) {
            return Collections.emptyMap();
        }
        String[] params = query.split("&");
        Map<String, String> result = new HashMap<>();
        for (String param : params) {
            if (param.equals("")) {
                continue;
            }
            String[] keyValues = param.split("=");
            if (keyValues.length == 2) {
                result.put(keyValues[0], keyValues[1]);
            } else {
                result.put(keyValues[0], "");
            }
        }
        return result;
    }
}
