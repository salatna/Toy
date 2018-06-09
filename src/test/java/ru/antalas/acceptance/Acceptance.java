package ru.antalas.acceptance;

import com.typesafe.config.ConfigFactory;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import ru.antalas.Main;

import java.math.BigDecimal;

import static io.restassured.RestAssured.*;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static org.hamcrest.Matchers.*;

public class Acceptance {
    @Before
    public void setUp() throws Exception {
        Main.main(null);

        RestAssured.port = ConfigFactory.load().getInt("webserver.port");
        config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
    }

    @Test
    public void shouldGetAccount() throws Exception {
        when().
            get("/account/1").
        then().
            body("amount", is(new BigDecimal("100.0")));
    }

    @Test
    public void shouldReportMissingAccount() throws Exception {
        get("/account/3").then()
                .body("not found", hasValue("3"))
                .statusCode(404);
    }
}
