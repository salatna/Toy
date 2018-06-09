package ru.antalas;

import io.undertow.server.HttpServerExchange;
import ru.antalas.persistence.Account;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Deque;
import java.util.Map;

import static ru.antalas.Main.out;

public class Controllers {
    private final Connection connection;

    public Controllers(Connection connection) {
        this.connection = connection;
    }

    public void account(HttpServerExchange exchange) throws SQLException {
        Map<String, Deque<String>> params = exchange.getQueryParameters();
        String item = params.get("id").getFirst();
        out(exchange, item, Account.account(connection, item));
    }

    public void transfer(HttpServerExchange exchange) throws SQLException {
        Map<String, Deque<String>> params = exchange.getQueryParameters();
        String src = params.get("src").getFirst();
        String dst = params.get("dst").getFirst();
        String amt = params.get("amt").getFirst();

        Account.transfer(connection, src, dst, amt);
    }
}
