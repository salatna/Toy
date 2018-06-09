package ru.antalas.front;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.undertow.server.HttpServerExchange;
import ru.antalas.back.Account;
import ru.antalas.front.json.Mapper;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;

import static io.undertow.util.Headers.CONTENT_TYPE;

public class Controllers {
    private final Connection connection;

    private final Mapper mapper = new Mapper();

    public Controllers(Connection connection) {
        this.connection = connection;
    }

    public void account(HttpServerExchange exchange) throws SQLException, JsonProcessingException {
        Map<String, Deque<String>> params = exchange.getQueryParameters();
        String item = params.get("id").getFirst();

        Optional<BigDecimal> amount = Account.amount(connection, item);

        exchange.getResponseHeaders().add(CONTENT_TYPE, "application/json");
        if (amount.isPresent()) {
            exchange.getResponseSender().send(mapper.json(Operations.account(amount.get())));
        } else {
            exchange.setStatusCode(404);
            exchange.getResponseSender().send(mapper.json(Operations.accountNotFound(item)));
        }
    }

    public void transfer(HttpServerExchange exchange) throws SQLException {
        Map<String, Deque<String>> params = exchange.getQueryParameters();
        String src = params.get("src").getFirst();
        String dst = params.get("dst").getFirst();
        String amt = params.get("amt").getFirst();

        Account.transfer(connection, src, dst, amt);
    }
}
