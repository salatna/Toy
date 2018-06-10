package ru.antalas.front;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableMap;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import ru.antalas.persistence.Persistence;
import ru.antalas.front.json.Mapper;
import ru.antalas.front.json.request.Transfer;
import ru.antalas.model.exceptions.OverdraftException;

import java.util.Deque;
import java.util.Map;
import java.util.Optional;

import static io.undertow.util.Headers.CONTENT_TYPE;

public class Controllers {
    private static final Mapper mapper = new Mapper();
    private static final Persistence data = new Persistence();

    public static void createAccount(HttpServerExchange exchange) throws JsonProcessingException {
        ru.antalas.front.json.request.Account input = mapper.fromInputStream(exchange.getInputStream(), new TypeReference<ru.antalas.front.json.request.Account>() {
        });

        ru.antalas.model.Account account = data.createAccount(input.getBalance());

        sendJson(exchange, new ru.antalas.front.json.response.Account(account.getId(), account.getBalance()));
    }

    public static void getAccount(HttpServerExchange exchange) throws JsonProcessingException {
        Map<String, Deque<String>> params = exchange.getQueryParameters();
        String id = params.get("id").getFirst();

        Optional<ru.antalas.model.Account> account = data.getAccount(Integer.parseInt(id));

        Object result;
        if (account.isPresent()) {
            result = new ru.antalas.front.json.response.Account(account.get().getId(), account.get().getBalance());
            sendJson(exchange, result);
        } else {
            exchange.setStatusCode(404);
            sendJson(exchange, ImmutableMap.of("statusCode", 404, "message", "Account " + id + " not found."));
        }

    }

    public static void transfer(HttpServerExchange exchange) throws JsonProcessingException {
        Transfer transfer = mapper.fromInputStream(exchange.getInputStream(), new TypeReference<Transfer>() {
        });

        try {
            data.transfer(transfer.getSourceAccountId(), transfer.getDestinationAccountId(), transfer.getAmount());
        }catch (OverdraftException e){
            exchange.setStatusCode(400);
            sendJson(exchange, ImmutableMap.of("statusCode", 400, "message", "Account " + transfer.getSourceAccountId() + " overdrawn."));
        }
    }

    public static void notFoundHandler(HttpServerExchange exchange) {
        exchange.setStatusCode(404);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send("Page Not Found!");
    }

    private static void sendJson(HttpServerExchange exchange, Object content) throws JsonProcessingException {
        exchange.getResponseHeaders().add(CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(mapper.json(content));
    }
}
