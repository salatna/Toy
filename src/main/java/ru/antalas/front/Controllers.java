package ru.antalas.front;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import ru.antalas.back.Account;
import ru.antalas.back.persistence.Persistence;
import ru.antalas.front.json.Mapper;

import java.math.BigDecimal;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;

import static io.undertow.util.Headers.CONTENT_TYPE;

public class Controllers {
    private static final Mapper mapper = new Mapper();
    private static final Persistence data = new Persistence();

    public static void createAccount(HttpServerExchange exchange) throws JsonProcessingException {
        String id = exchange.getQueryParameters().get("id").getFirst();
        String amount = exchange.getQueryParameters().get("amount").getFirst();

        ru.antalas.model.Account account = Account.account(data, id, amount);

        exchange.getResponseHeaders().add(CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(mapper.json(Operations.account(account)));
    }

    public static void account(HttpServerExchange exchange) throws JsonProcessingException {
        Map<String, Deque<String>> params = exchange.getQueryParameters();
        String id = params.get("id").getFirst();

        Optional<BigDecimal> amount = Account.amount(data, id);

        exchange.getResponseHeaders().add(CONTENT_TYPE, "application/json");
        if (amount.isPresent()) {
            exchange.getResponseSender().send(mapper.json(Operations.account(amount.get())));
        } else {
            exchange.setStatusCode(404);
            exchange.getResponseSender().send(mapper.json(Operations.accountNotFound(id)));
        }
    }

    public static void transfer(HttpServerExchange exchange) {
        Map<String, Deque<String>> params = exchange.getQueryParameters();
        String src = params.get("src").getFirst();
        String dst = params.get("dst").getFirst();
        String amt = params.get("amt").getFirst();

        Account.transfer(data, src, dst, amt);
    }

    public static void notFoundHandler(HttpServerExchange exchange) {
        exchange.setStatusCode(404);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send("Page Not Found!");
    }
}
