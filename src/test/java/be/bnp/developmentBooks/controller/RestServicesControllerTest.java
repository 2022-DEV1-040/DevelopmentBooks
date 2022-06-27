package be.bnp.developmentBooks.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RestServicesControllerTest {

    @Autowired
    RestServicesController restServicesController;

    @Test
    void ping() {
        assertEquals(HttpStatus.OK, restServicesController.ping().getStatusCode());
    }
}