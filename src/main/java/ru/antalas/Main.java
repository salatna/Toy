package ru.antalas;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import ru.antalas.front.Controllers;

import java.sql.Connection;
import java.sql.SQLException;

import static io.undertow.Handlers.exceptionHandler;
import static io.undertow.Handlers.pathTemplate;
import static ru.antalas.back.Persistence.initBack;
import static ru.antalas.front.Routes.ACCOUNT;
import static ru.antalas.front.Routes.TRANSFER;

public class Main {
    public static void main(String[] args) throws SQLException {
        Config config = ConfigFactory.load("prod.properties");

        Connection conn = initBack(config);
        Undertow server = initFront(config, new Controllers(conn));

        server.start();
    }

    private static Undertow initFront(Config config, Controllers front) {
        return Undertow.builder()
                .addHttpListener(config.getInt("webserver.port"), config.getString("webserver.host"))
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
