package be.bnp.developmentBooks.controller;

import be.bnp.developmentBooks.entity.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
    void whenAddToCartNewBookThenQuantityMustBeOne() {
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
    void whenAddToCartManyBooksThenQuantityMustCalculate() {
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
    void whenAddListToCartManyBooksThenQuantityMustCalculate() {
        HashMap<Book, Integer> listBook = bookServicesController.getCart().getListBooks();
        List<Long> listIds = Arrays.asList(1L,1L,1L,2L);
        bookServicesController.addListToCart(listIds);
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
    void whenDecreaseFromCartAndQuantityBecomeZeroThenBookIsDelete() {
        HashMap<Book, Integer> listBook = bookServicesController.getCart().getListBooks();
        bookServicesController.addToCart(1L);
        assertEquals(1, listBook.entrySet().size());
        bookServicesController.decreaseFromCart(1L);
        assertEquals(0, listBook.entrySet().size());
    }

    @Test
    void whenDecreaseFromCartManyBooksThenQuantityMustCalculate() {
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
    void whenComputeTotalPriceOneBookThenTotalPriceMustBeBookPrice() {
        bookServicesController.addToCart(1L);
        // 1*(50-(50*0) = 50
        assertEquals("Total price : 50,00€", bookServicesController.computeTotalPriceFromCart());
    }

    @Test
    void whenComputeTotalPriceTwoSameBooksThenTotalPriceMustBeBookPriceMultiplyBy2() {
        bookServicesController.addToCart(1L);
        bookServicesController.addToCart(1L);
        // 2*(50-(50*0) = 100
        assertEquals("Total price : 100,00€", bookServicesController.computeTotalPriceFromCart());
    }

    @Test
    void whenComputeTotalPriceTwoDifferentBooksThenTotalPriceShouldBeReducedBy5Percent() {
        bookServicesController.addToCart(1L);
        bookServicesController.addToCart(2L);
        // 2*(50-(50*0.05) = 95
        assertEquals("Total price : 95,00€", bookServicesController.computeTotalPriceFromCart());
    }

    @Test
    void whenComputeTotalPriceTreeDifferentBooksThenTotalPriceShouldBeReducedBy10Percent() {
        bookServicesController.addToCart(1L);
        bookServicesController.addToCart(2L);
        bookServicesController.addToCart(3L);
        // 3*(50-(50*0.1) = 135
        assertEquals("Total price : 135,00€", bookServicesController.computeTotalPriceFromCart());
    }

    @Test
    void whenComputeTotalPriceFourDifferentBooksThenTotalPriceShouldBeReducedBy20Percent() {
        bookServicesController.addToCart(1L);
        bookServicesController.addToCart(2L);
        bookServicesController.addToCart(3L);
        bookServicesController.addToCart(4L);
        // 4*(50-(50*0.2) = 160
        assertEquals("Total price : 160,00€", bookServicesController.computeTotalPriceFromCart());
    }

    @Test
    void whenComputeTotalPriceFiveDifferentBooksThenTotalPriceShouldBeReducedBy25Percent() {
        bookServicesController.addToCart(1L);
        bookServicesController.addToCart(2L);
        bookServicesController.addToCart(3L);
        bookServicesController.addToCart(4L);
        bookServicesController.addToCart(5L);

        // 5*(50-(50*0.25) = 187,5
        assertEquals("Total price : 187,50€", bookServicesController.computeTotalPriceFromCart());
    }

    @Test
    void whenComputeTotalPriceTwoDifferentBooksWith2QuantityEachThenTotalPriceShouldBeReducedBy5Percent() {
        bookServicesController.addToCart(1L);
        bookServicesController.addToCart(1L);

        bookServicesController.addToCart(2L);
        bookServicesController.addToCart(2L);

        // 2*(50-(50*0.05) = 95
        // 2*(50-(50*0.05) = 95
        assertEquals("Total price : 190,00€", bookServicesController.computeTotalPriceFromCart());
    }

    @Test
    void whenComputeTotalPriceTwoSameBooksAndOneDifferentThenTotalPriceMustBe2ReducedPricePlusOneFullPrice() {
        bookServicesController.addToCart(1L);
        bookServicesController.addToCart(1L);

        bookServicesController.addToCart(2L);
        // 2*(50-(50*0,05)) + 50 = 145
        assertEquals("Total price : 145,00€", bookServicesController.computeTotalPriceFromCart());
    }

    @Test
    void whenComputeTotalPriceTwoSameBooks2TimesAndOneDifferentThenTotalPriceMustBe2ReducedPricePlusOneFullPrice() {
        bookServicesController.addToCart(1L);
        bookServicesController.addToCart(1L);

        bookServicesController.addToCart(2L);
        bookServicesController.addToCart(2L);
        bookServicesController.addToCart(2L);

        // 4*(50-(50*0,05)) + 50 = 240
        assertEquals("Total price : 240,00€", bookServicesController.computeTotalPriceFromCart());
    }

    @Test
    void whenComputeTotalPriceSimpleExampleThenTotalPriceMustCalculate() {
        List<Long> listIds = Arrays.asList(1L,1L,
                                            2L,2L,
                                            3L,3L,
                                            4L,
                                            5L);
        bookServicesController.addListToCart(listIds);

        // 2*(200-(200*0,2) = 320
        assertEquals("Total price : 320,00€", bookServicesController.computeTotalPriceFromCart());
    }

    @Test
    void whenComputeTotalPriceComplexExampleThenTotalPriceMustCalculate() {
        List<Long> listIds = Arrays.asList(2L,2L,2L,2L,
                                            3L,3L,3L,
                                            4L,4L,
                                            5L);
        bookServicesController.addListToCart(listIds);
        
        // 200-(200*0,2) = 160
        // 150-(150*0,1) = 135
        // 100-(100*0,05) = 95
        // 50
        assertEquals("Total price : 440,00€", bookServicesController.computeTotalPriceFromCart());
    }

    @Test
    void whenComputeTotalPriceComplexExample2ThenTotalPriceMustCalculate() {
        List<Long> listIds = Arrays.asList(1L,1L,1L,1L,1L,
                                            2L,2L,2L,2L,2L,
                                            3L,3L,3L,3L,
                                            4L,4L,4L,4L,4L,
                                            5L,5L,5L,5L);
        bookServicesController.addListToCart(listIds);
        
        // 3*(250-(250*0,25) = 562,5
        // 2*(200-(200*0,2) = 320
        assertEquals("Total price : 882,50€", bookServicesController.computeTotalPriceFromCart());
    }
}