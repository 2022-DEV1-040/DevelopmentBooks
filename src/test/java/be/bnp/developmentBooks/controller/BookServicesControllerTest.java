package be.bnp.developmentBooks.controller;

import be.bnp.developmentBooks.entity.Book;
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
       // assertEquals(HttpStatus.OK, bookServicesController.ping().getStatusCode());
    }

    @Test
    void whenAddToBasketNewBookThenQuantityMustBeOne() throws Exception {
        bookServicesController.addToBasket(1L);
        assertEquals(1, bookServicesController.getBasket().getListBooks().entrySet().size());
        bookServicesController.addToBasket(2L);
        assertEquals(2, bookServicesController.getBasket().getListBooks().entrySet().size());
        for (Book book : bookServicesController.getBasket().getListBooks().keySet()) {
            int quantity = bookServicesController.getBasket().getListBooks().get(book);
            assertEquals(1, quantity);
        }
    }


}