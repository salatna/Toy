package ru.antalas.acceptance;

import com.typesafe.config.ConfigFactory;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import ru.antalas.Main;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class Acceptance {
    @Before
    public void setUp() throws Exception {
        Main.main(null);

        RestAssured.port = ConfigFactory.load().getInt("webserver.port");
    }

    @Test
    public void shouldGetAccount() throws Exception {
        get("/account/1").then().body("", hasValue("100"));
    }
}
