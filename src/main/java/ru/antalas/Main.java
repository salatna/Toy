package ru.antalas;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import org.h2.Driver;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import static io.undertow.Handlers.pathTemplate;
import static java.lang.Integer.parseInt;
import static java.sql.Connection.TRANSACTION_READ_COMMITTED;
import static ru.antalas.Routes.ACCOUNT;
import static ru.antalas.Routes.TRANSFER;

public class Main {
    public static void main(String[] args) throws SQLException {
        Connection conn = initPersistence();
        Controllers front = new Controllers(conn);

        Undertow server = Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler(
                        Handlers.exceptionHandler(exchange -> {
                                    pathTemplate()
                                            .add(ACCOUNT.getPath(), front::account)
                                            .add(TRANSFER.getPath(), front::transfer)
                                            .handleRequest(exchange);
                                    if (exchange.isResponseChannelAvailable()) {
                                        if (exchange.getStatusCode() == 404) {
                                            exchange.getResponseSender().send("Not found");
                                        }
                                    }
                                }
                        ).addExceptionHandler(Exception.class,
                                exchange -> {
                                    if (exchange.isResponseChannelAvailable()) {
                                        exchange.setStatusCode(500);
                                        exchange.getResponseSender().send("Error occured, please contact support");
                                    }
                                }
                        )
                )
                .build();


        server.start();


    }

    private static Connection initPersistence() throws SQLException {
        Driver.load();

        Connection conn = new Driver().connect("jdbc:h2:~/test", new Properties());
        conn.createStatement().execute("DROP TABLE IF EXISTS ACCOUNTS");
        conn.createStatement().execute("CREATE TABLE IF NOT EXISTS ACCOUNTS(ID INT PRIMARY KEY, AMOUNT NUMBER(10,2))");
        conn.createStatement().execute("INSERT INTO ACCOUNTS VALUES (1, 100)");
        conn.createStatement().execute("INSERT INTO ACCOUNTS VALUES (2, 0)");
        return conn;
    }

    static void transfer(Connection conn, String src, String dst, String amt) throws SQLException {
        conn.setAutoCommit(false);
        int currentIsolation = conn.getTransactionIsolation();
        conn.setTransactionIsolation(TRANSACTION_READ_COMMITTED);

        update(conn, parseInt(src), new BigDecimal(amt).negate());
        update(conn, parseInt(dst), new BigDecimal(amt));

        conn.setTransactionIsolation(currentIsolation);
        conn.setAutoCommit(true);
    }

    public static ResultSet account(Connection conn, String item) throws SQLException {
        PreparedStatement query = conn.prepareStatement("SELECT AMOUNT FROM ACCOUNTS WHERE ID = ?");
        query.setInt(1, parseInt(item));
        query.execute();
        return query.getResultSet();
    }

    public static void out(HttpServerExchange exchange, String item, ResultSet rs) throws SQLException {
        if (rs.next()) {
            String amount = rs.getBigDecimal("AMOUNT").toPlainString();
            exchange.getResponseSender().send(amount);
        } else {
            exchange.setStatusCode(404);
            exchange.getResponseSender().send("Account #" + item + " not found.");
        }
    }

    private static void update(Connection conn, int accountId, BigDecimal amount) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE ACCOUNTS SET AMOUNT = AMOUNT + ? WHERE ID = ?");
        stmt.setInt(2, accountId);
        stmt.setBigDecimal(1, amount);
        stmt.executeUpdate();
    }
}
