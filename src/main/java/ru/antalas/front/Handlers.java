package ru.antalas.front;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import ru.antalas.front.json.Account;
import ru.antalas.front.json.Mapper;
import ru.antalas.front.json.Transfer;
import ru.antalas.model.exceptions.OverdraftException;
import ru.antalas.persistence.Persistence;

import java.util.Deque;
import java.util.Map;
import java.util.Optional;

import static io.undertow.util.Headers.CONTENT_TYPE;

public class Handlers {
    private static final Mapper mapper = new Mapper();
    private static final Persistence data = new Persistence();

    public static void createAccount(HttpServerExchange exchange) throws JsonProcessingException {
        Account input = mapper.fromInputStream(exchange.getInputStream(), new TypeReference<Account>() {
        });

        ru.antalas.model.Account account = data.createAccount(input.getBalance());

        sendJson(exchange, account);
    }

    public static void getAccount(HttpServerExchange exchange) throws JsonProcessingException {
        Map<String, Deque<String>> params = exchange.getQueryParameters();
        String id = params.get("id").getFirst();

        Optional<ru.antalas.model.Account> account = data.getAccount(Integer.parseInt(id));

        if (account.isPresent()) {
            sendJson(exchange, account.get());
        } else {
            notFoundApiResult(exchange, "Account " + id + " not found.");
        }

    }

    public static void transfer(HttpServerExchange exchange) throws JsonProcessingException {
        Transfer transfer = mapper.fromInputStream(exchange.getInputStream(), new TypeReference<Transfer>() {
        });

        try {
            data.transfer(transfer.getSourceAccountId(), transfer.getDestinationAccountId(), transfer.getAmount());
        }catch (OverdraftException e){
            badRequest(exchange, e.getMessage());
        }
    }

    private static void badRequest(HttpServerExchange exchange, String message) throws JsonProcessingException {
        ApiError error = new ApiError(400, message);
        exchange.setStatusCode(error.getStatusCode());
        sendJson(exchange, error);
    }

    private static void notFoundApiResult(HttpServerExchange exchange, String message) throws JsonProcessingException {
        ApiError error = new ApiError(404, message);
        exchange.setStatusCode(error.getStatusCode());
        sendJson(exchange, error);
    }

    public static void notFoundFallbackHandler(HttpServerExchange exchange) {
        exchange.setStatusCode(404);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send("Page Not Found!");
    }

    private static void sendJson(HttpServerExchange exchange, Object content) throws JsonProcessingException {
        exchange.getResponseHeaders().add(CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(mapper.json(content));
    }

    private static class ApiError {
        private final int statusCode;
        private final String message;

        ApiError(int statusCode, String message) {
            super();
            this.statusCode = statusCode;
            this.message = message;
        }

        int getStatusCode() {
            return statusCode;
        }

        public String getMessage() {
            return message;
        }
    }

}
