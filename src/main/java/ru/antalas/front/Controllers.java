package ru.antalas.front;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import ru.antalas.back.Account;
import ru.antalas.back.persistence.Persistence;
import ru.antalas.front.json.Mapper;
import ru.antalas.front.json.request.Transfer;

import java.math.BigDecimal;
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

        ru.antalas.model.Account account = Account.account(data, input);

        sendJson(exchange, new ru.antalas.front.json.response.Account(account.getId()));
    }

    public static void account(HttpServerExchange exchange) throws JsonProcessingException {
        Map<String, Deque<String>> params = exchange.getQueryParameters();
        String id = params.get("id").getFirst();

        Optional<BigDecimal> amount = Account.amount(data, id);

        if (!amount.isPresent()) {
            exchange.setStatusCode(404);
        }

        sendJson(exchange, amount.isPresent() ? Operations.account(amount.get()) : Operations.accountNotFound(id));
    }

    public static void transfer(HttpServerExchange exchange) {
        Transfer transfer = mapper.fromInputStream(exchange.getInputStream(), new TypeReference<Transfer>() {
        });

        Account.transfer(data, transfer);
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
