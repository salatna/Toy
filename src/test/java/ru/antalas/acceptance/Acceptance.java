package ru.antalas.acceptance;

import com.typesafe.config.ConfigFactory;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.antalas.Main;
import ru.antalas.front.json.Transfer;
import ru.antalas.model.Account;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.config.RestAssuredConfig.newConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static java.lang.Integer.MAX_VALUE;
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
            .body("balance", is(new BigDecimal("100.00")))
            .statusCode(200);
    }

    @Test
    public void shouldGetAccount() throws Exception {
        Account first = givenAccountWith(new BigDecimal("100.00")).as(Account.class);

        assertAccountHasBalance(first, "100.00");
    }

    @Test
    public void shouldReportMissingAccount() throws Exception {
        when()
            .get("/accounts/"+ MAX_VALUE).
        then()
            .body("statusCode", is(404))
            .body("message", is("Account " + MAX_VALUE + " not found."))
            .statusCode(404);
    }

    @Test
    public void shouldTransfer() throws Exception {
        Account first = givenAccountWith(new BigDecimal("30.00")).as(Account.class);
        Account second = givenAccountWith(new BigDecimal("0.00")).as(Account.class);

        given()
            .contentType("application/json")
            .body(new Transfer(first.getId(), second.getId(), new BigDecimal("15.00"))).
        when()
            .post("/transfers").
        then()
            .statusCode(200);

        assertAccountHasBalance(first, "15.00");
        assertAccountHasBalance(second, "15.00");
    }

    @Test
    public void shouldErrWhenTransferOverdrafts() throws Exception {
        Account first = givenAccountWith(new BigDecimal("30.00")).as(Account.class);
        Account second = givenAccountWith(new BigDecimal("0.00")).as(Account.class);

        given()
            .contentType("application/json")
            .body(new Transfer(first.getId(), second.getId(), new BigDecimal("100.00"))).
        when()
            .post("/transfers").
        then()
            .body("statusCode", is(400))
            .body("errorType", is("OverdraftException"))
            .statusCode(400);
    }

    @Test
    public void shouldErrWhenTransferAccountMissing() throws Exception {
        Account first = givenAccountWith(new BigDecimal("30.00")).as(Account.class);

        given()
            .contentType("application/json")
            .body(new Transfer(first.getId(), MAX_VALUE, new BigDecimal("100.00"))).
        when()
            .post("/transfers").
        then()
            .body("statusCode", is(400))
            .body("errorType", is("TransferException"))
            .body("message", is(MAX_VALUE + " not found."))
            .statusCode(400);
    }

    private static void assertAccountHasBalance(Account account, String expectedBalance) {
        when()
            .get("/accounts/"+account.getId()).
        then()
            .body("id", not(empty()))
            .body("balance", is(new BigDecimal(expectedBalance)));
    }

    private static Response givenAccountWith(BigDecimal amount) {
        return
        given()
            .contentType("application/json")
            .body(new ru.antalas.front.json.Account(amount)).
        when()
            .post("/accounts");
    }
}
