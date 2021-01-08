package io.quarkus.ts.openshift.microprofile;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.when;
import static org.awaitility.Awaitility.with;
import static org.hamcrest.CoreMatchers.is;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class AbstractMicroProfileTest {
    @Test
    @Order(1)
    public void hello() {
        when()
                .post("/hello/enable")
        .then()
                .statusCode(204);

        with().pollInterval(Duration.ofSeconds(1)).and()
                .with().pollDelay(Duration.ofSeconds(10)).await()
                .atLeast(Duration.ofSeconds(1))
                .atMost(25, TimeUnit.SECONDS)
                .with()
                .untilAsserted(() -> {
                    when()
                            .get("/client")
                            .then()
                            .statusCode(200)
                            .log().body()
                            .log().status()
                            .body(is("Client got: Hello, World!"));
                });
    }

    @Test
    @Order(10)
    @Disabled("https://github.com/quarkusio/quarkus/issues/8650")
    public void fallback() {
        when()
                .post("/hello/disable")
        .then()
                .statusCode(204);

        when()
                .get("/client")
        .then()
                .statusCode(200)
                .body(is("Client got: Fallback"));
    }
}
