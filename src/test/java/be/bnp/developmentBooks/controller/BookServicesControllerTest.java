package be.bnp.developmentBooks.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BookServicesControllerTest {

    @Autowired
    BookServicesController bookServicesController;

    @Test
    void whenPingThenStatusOK() {
        assertEquals(HttpStatus.OK, bookServicesController.ping().getStatusCode());
    }

    @Test
    void whenAddToBasketThenQuantityAndPriceUpdate() {
        bookServicesController.addToBasket(1L);
        assertEquals(1, bookServicesController.getBasket());
    }


}