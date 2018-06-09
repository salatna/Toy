package ru.antalas.front;

import io.undertow.server.HttpServerExchange;
import ru.antalas.back.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;

public class Controllers {
    private final Connection connection;

    public Controllers(Connection connection) {
        this.connection = connection;
    }

    public void account(HttpServerExchange exchange) throws SQLException {
        Map<String, Deque<String>> params = exchange.getQueryParameters();
        String item = params.get("id").getFirst();

        Optional<BigDecimal> amount = Account.amount(connection, item);

        if (amount.isPresent()) {
            exchange.getResponseSender().send(amount.get().toPlainString());
        } else {
            exchange.setStatusCode(404);
            exchange.getResponseSender().send("Account #" + item + " not found.");
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
