package ru.antalas;

import io.undertow.server.HttpServerExchange;
import ru.antalas.persistence.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;

class Controllers {
    private final Connection connection;

    Controllers(Connection connection) {
        this.connection = connection;
    }

    void account(HttpServerExchange exchange) throws SQLException {
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

    void transfer(HttpServerExchange exchange) throws SQLException {
        Map<String, Deque<String>> params = exchange.getQueryParameters();
        String src = params.get("src").getFirst();
        String dst = params.get("dst").getFirst();
        String amt = params.get("amt").getFirst();

        Account.transfer(connection, src, dst, amt);
    }
}
