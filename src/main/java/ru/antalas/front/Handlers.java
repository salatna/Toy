package ru.antalas.front;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ExceptionHandler;
import io.undertow.util.Headers;
import ru.antalas.front.json.AccountDto;
import ru.antalas.front.json.Mapper;
import ru.antalas.front.json.Transfer;
import ru.antalas.model.Account;
import ru.antalas.persistence.Persistence;
import ru.antalas.persistence.SequenceGenerator;

import java.util.Deque;
import java.util.Map;

import static io.undertow.util.Headers.CONTENT_TYPE;

public class Handlers {
    private static final Mapper mapper = new Mapper();
    private static final Persistence data = new Persistence(new SequenceGenerator.AtomicIntegerSequence());

    public static void createAccount(HttpServerExchange exchange) {
        AccountDto input = mapper.fromInputStream(exchange.getInputStream(), new TypeReference<AccountDto>() {
        });

        Account account = data.createAccount(input.getBalance());

        sendJson(exchange, account);
    }

    public static void getAccount(HttpServerExchange exchange) {
        Map<String, Deque<String>> params = exchange.getQueryParameters();
        String id = params.get("id").getFirst();

        Account account = data.getAccount(Integer.parseInt(id));

        sendJson(exchange, account);
    }

    public static void transfer(HttpServerExchange exchange) {
        Transfer transfer = mapper.fromInputStream(exchange.getInputStream(), new TypeReference<Transfer>() {
        });

        data.transfer(transfer.getSourceAccountId(), transfer.getDestinationAccountId(), transfer.getAmount());
    }

    public static void handleFallbackNotFound(HttpServerExchange exchange) {
        exchange.setStatusCode(404);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send("Page Not Found!");
    }

    public static void handleNotFoundApi(HttpServerExchange exchange) {
        Throwable ex = exchange.getAttachment(ExceptionHandler.THROWABLE);
        exchange.setStatusCode(404);
        sendJson(exchange, new ApiError(404, ex.getMessage()));
    }

    public static void handleBadRequest(HttpServerExchange exchange) {
        Throwable ex = exchange.getAttachment(ExceptionHandler.THROWABLE);
        exchange.setStatusCode(400);
        sendJson(exchange, new ApiError(400, ex.getMessage()));
    }

    public static void handleServerError(HttpServerExchange exchange) {
        exchange.setStatusCode(500);
        sendJson(exchange, new ApiError(500, "Internal Server Error"));
    }

    private static void sendJson(HttpServerExchange exchange, Object content) {
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

        @SuppressWarnings("WeakerAccess")
        public int getStatusCode() {
            return statusCode;
        }

        @JsonInclude(Include.NON_NULL)
        public String getMessage() {
            return message;
        }
    }

}
