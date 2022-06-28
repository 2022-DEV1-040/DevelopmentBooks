package be.bnp.developmentBooks.controller;

import be.bnp.developmentBooks.entity.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
// Reset the context between each test
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookServicesControllerTest {

    @Autowired
    BookServicesController bookServicesController;

    @Test
    void whenPingThenStatusOK() {
        assertEquals(HttpStatus.OK, bookServicesController.ping().getStatusCode());
    }

    @Test
    void whenAddToCartNewBookThenQuantityMustBeOne() throws Exception {
        HashMap<Book, Integer> listBook = bookServicesController.getCart().getListBooks();
        bookServicesController.addToCart(1L);
        assertEquals(1, listBook.entrySet().size());
        bookServicesController.addToCart(2L);
        assertEquals(2, listBook.entrySet().size());
        for (Book book : listBook.keySet()) {
            int quantity = listBook.get(book);
            assertEquals(1, quantity);
        }
    }

    @Test
    void whenAddToCartManyBooksThenQuantityMustCalculate() throws Exception {
        HashMap<Book, Integer> listBook = bookServicesController.getCart().getListBooks();
        bookServicesController.addToCart(1L);
        assertEquals(1, listBook.entrySet().size());
        bookServicesController.addToCart(1L);
        assertEquals(1, listBook.entrySet().size());
        bookServicesController.addToCart(1L);
        assertEquals(1, listBook.entrySet().size());
        bookServicesController.addToCart(2L);
        assertEquals(2, listBook.entrySet().size());
        for (Book book : listBook.keySet()) {
            int quantity = listBook.get(book);
            if (book.getId() == 1) {
                assertEquals(3, quantity);
            } else {
                assertEquals(1, quantity);
            }
        }
    }

    @Test
    void whenDecreaseFromCartAndQuantityBecomeZeroThenBookIsDelete() throws Exception {
        HashMap<Book, Integer> listBook = bookServicesController.getCart().getListBooks();
        bookServicesController.addToCart(1L);
        assertEquals(1, listBook.entrySet().size());
        bookServicesController.decreaseFromCart(1L);
        assertEquals(0, listBook.entrySet().size());
    }

    @Test
    void whenDecreaseFromCartManyBooksThenQuantityMustCalculate() throws Exception {
        HashMap<Book, Integer> listBook = bookServicesController.getCart().getListBooks();
        bookServicesController.addToCart(1L);
        assertEquals(1, listBook.entrySet().size());
        bookServicesController.addToCart(1L);
        assertEquals(1, listBook.entrySet().size());
        bookServicesController.addToCart(1L);
        assertEquals(1, listBook.entrySet().size());
        bookServicesController.addToCart(2L);
        assertEquals(2, listBook.entrySet().size());
        bookServicesController.decreaseFromCart(1L);
        assertEquals(2, listBook.entrySet().size());
        bookServicesController.decreaseFromCart(2L);
        assertEquals(1, listBook.entrySet().size());
        for (Book book : listBook.keySet()) {
            int quantity = listBook.get(book);
            assertEquals(2, quantity);
        }
    }

    @Test
    void whenComputeTotalPriceOneBookThenTotalPriceMustBeBookPrice() throws Exception {
        HashMap<Book, Integer> listBook = bookServicesController.getCart().getListBooks();
        bookServicesController.addToCart(1L);
        // 1*(50-(50*0) = 50
        assertEquals("Total price : 50.0€", bookServicesController.computeTotalPriceFromCart());
    }
}