package ru.antalas;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import ru.antalas.back.persistence.Persistence;
import ru.antalas.front.Controllers;

import static io.undertow.Handlers.exceptionHandler;
import static io.undertow.Handlers.pathTemplate;
import static ru.antalas.front.Routes.ACCOUNT;
import static ru.antalas.front.Routes.ACCOUNT_CREATE;
import static ru.antalas.front.Routes.TRANSFER;

public class Main {
    public static void main(String[] args) throws Exception {
        Undertow server = initFront(ConfigFactory.load(), new Controllers(new Persistence()));

        server.start();
    }

    private static Undertow initFront(Config config, Controllers front) throws Exception {
        return Undertow.builder()
                .addHttpListener(config.getInt("webserver.port"), config.getString("webserver.host"))
                .setHandler(
                        exceptionHandler(exchange -> {
                                    pathTemplate()
                                            .add(ACCOUNT_CREATE.getPath(), front::createAccount)
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
