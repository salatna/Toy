package ru.antalas;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.sql.Connection;
import java.sql.SQLException;

import static io.undertow.Handlers.exceptionHandler;
import static io.undertow.Handlers.pathTemplate;
import static ru.antalas.Routes.ACCOUNT;
import static ru.antalas.Routes.TRANSFER;
import static ru.antalas.persistence.Persistence.initBack;

public class Main {
    public static void main(String[] args) throws SQLException {
        Connection conn = initBack();
        Undertow server = initFront(new Controllers(conn));

        server.start();
    }

    private static Undertow initFront(Controllers front) {
        return Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler(
                        exceptionHandler(exchange -> {
                                    pathTemplate()
                                            .add(ACCOUNT.getPath(), front::account)
                                            .add(TRANSFER.getPath(), front::transfer)
                                            .handleRequest(exchange);
                                    if (exchange.isResponseChannelAvailable()) {
                                        notFound(exchange);
                                    }
                                }
                        ).addExceptionHandler(Exception.class, genericHandler())
                )
                .build();
    }

    private static HttpHandler genericHandler() {
        return exchange -> {
            if (exchange.isResponseChannelAvailable()) {
                exchange.setStatusCode(500);
                exchange.getResponseSender().send("Error occured, please contact support");
            }
        };
    }

    private static void notFound(HttpServerExchange exchange) {
        if (exchange.getStatusCode() == 404) {
            exchange.getResponseSender().send("Not found");
        }
    }


}
