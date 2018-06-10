package ru.antalas;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.undertow.Undertow;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.BlockingHandler;
import ru.antalas.front.Handlers;

public class Main {
    private static final RoutingHandler ROUTES = new RoutingHandler()
            .post("/accounts", Handlers::createAccount)
            .get("/accounts/{id}", Handlers::getAccount)
            .post("/transfers", Handlers::transfer)
            .setFallbackHandler(Handlers::notFoundFallbackHandler);

    public static void main(String[] args) throws Exception {
        Config config = ConfigFactory.load();

        Undertow server = Undertow.builder()
                .addHttpListener(config.getInt("webserver.port"), config.getString("webserver.host"))
                .setHandler(
                        new BlockingHandler(ROUTES))
                .build();

        server.start();
    }
}
