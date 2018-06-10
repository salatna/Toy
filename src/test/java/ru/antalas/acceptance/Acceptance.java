package ru.antalas.acceptance;

import com.typesafe.config.ConfigFactory;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.antalas.Main;

import java.math.BigDecimal;

import static io.restassured.RestAssured.post;
import static io.restassured.RestAssured.when;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.config.RestAssuredConfig.newConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static org.hamcrest.Matchers.is;

public class Acceptance {
    @BeforeClass
    public static void initSystem() throws Exception {
        Main.main(null);

        RestAssured.config = newConfig().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = ConfigFactory.load().getInt("webserver.port");

        post("/accounts/{id}/{amount}", 1, new BigDecimal("100.00"));
        post("/accounts/{id}/{amount}", 2, new BigDecimal("0.00"));

    }

    @Test
    public void shouldGetAccount() throws Exception {
        when().
            get("/account/1").
        then().
            body("amount", is(new BigDecimal("100.00")));
    }

    @Test
    public void shouldReportMissingAccount() throws Exception {
        when().
            get("/account/3").
        then().
            body("id", is("3")).
            statusCode(404);
    }
}
