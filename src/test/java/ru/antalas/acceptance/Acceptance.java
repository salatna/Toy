package ru.antalas.acceptance;

import com.typesafe.config.ConfigFactory;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.antalas.Main;
import ru.antalas.front.json.request.Account;
import ru.antalas.front.json.request.Transfer;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.config.RestAssuredConfig.newConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static org.hamcrest.Matchers.*;

public class Acceptance {
    @BeforeClass
    public static void initSystem() throws Exception {
        Main.main(null);

        RestAssured.config = newConfig().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = ConfigFactory.load().getInt("webserver.port");
    }

    @Test
    public void shouldCreateAccount() throws Exception {
        givenAccountWith(new BigDecimal("100.00")).
        then()
            .body("id", not(empty()))
            .statusCode(200);
    }

    @Test
    public void shouldGetAccount() throws Exception {
        ru.antalas.front.json.response.Account first = givenAccountWith(new BigDecimal("100.00")).as(ru.antalas.front.json.response.Account.class);

        when()
            .get("/accounts/"+first.getId()).
        then()
            .body("amount", is(new BigDecimal("100.00")));
    }

    @Test
    public void shouldReportMissingAccount() throws Exception {
        when()
            .get("/accounts/"+Integer.MAX_VALUE).
        then()
            .body("id", is(""+Integer.MAX_VALUE))
            .statusCode(404);
    }

    @Test
    public void shouldTransfer() throws Exception {
        ru.antalas.front.json.response.Account first = givenAccountWith(new BigDecimal("30.00")).as(ru.antalas.front.json.response.Account.class);
        ru.antalas.front.json.response.Account second = givenAccountWith(new BigDecimal("0.00")).as(ru.antalas.front.json.response.Account.class);

        given()
            .contentType("application/json")
            .body(new Transfer(first.getId(), second.getId(), new BigDecimal("15.00"))).
        when()
            .post("/transfers").
        then()
            .statusCode(200);

        when()
            .get("/accounts/"+first.getId()).
        then()
            .body("amount", is(new BigDecimal("15.00")));

        when()
            .get("/accounts/"+second.getId()).
        then()
            .body("amount", is(new BigDecimal("15.00")));

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
