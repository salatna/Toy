package ru.antalas.acceptance;

import com.typesafe.config.ConfigFactory;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.antalas.Main;
import ru.antalas.front.json.Account;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
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

        givenAccountWith(new BigDecimal("100.00"));
        givenAccountWith(new BigDecimal("0.00"));
    }

    @Test
    public void shouldCreateAccount() throws Exception {
        givenAccountWith(new BigDecimal("100.00")).
        then()
            .body("accountURI", is("/accounts/3"))
            .statusCode(200);
    }

    @Test
    public void shouldGetAccount() throws Exception {
        when()
            .get("/accounts/1").
        then()
            .body("amount", is(new BigDecimal("100.00")));
    }

    @Test
    public void shouldReportMissingAccount() throws Exception {
        when()
            .get("/accounts/99").
        then()
            .body("id", is("99"))
            .statusCode(404);
    }

    private static Response givenAccountWith(BigDecimal amount) {
        return
        given()
            .contentType("application/json")
            .body(new Account(amount)).
        when()
            .post("/accounts");
    }
}
