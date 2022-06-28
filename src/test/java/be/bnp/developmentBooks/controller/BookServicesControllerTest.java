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
        bookServicesController.addToCart(1L);
        // 1*(50-(50*0) = 50
        assertEquals("Total price : 50.0€", bookServicesController.computeTotalPriceFromCart());
    }

    @Test
    void whenComputeTotalPriceTwoSameBooksThenTotalPriceMustBeBookPriceMultiplyBy2() throws Exception {
        bookServicesController.addToCart(1L);
        bookServicesController.addToCart(1L);
        // 2*(50-(50*0) = 100
        assertEquals("Total price : 100.0€", bookServicesController.computeTotalPriceFromCart());
    }

    @Test
    void whenComputeTotalPriceTwoDifferentBooksThenTotalPriceShouldBeReducedBy5Percent() throws Exception {
        bookServicesController.addToCart(1L);
        bookServicesController.addToCart(2L);
        // 2*(50-(50*0.05) = 95
        assertEquals("Total price : 95.0€", bookServicesController.computeTotalPriceFromCart());
    }

    @Test
    void whenComputeTotalPriceTreeDifferentBooksThenTotalPriceShouldBeReducedBy10Percent() throws Exception {
        bookServicesController.addToCart(1L);
        bookServicesController.addToCart(2L);
        bookServicesController.addToCart(3L);
        // 3*(50-(50*0.1) =
        assertEquals("Total price : 135.0€", bookServicesController.computeTotalPriceFromCart());
    }

    @Test
    void whenComputeTotalPriceFourDifferentBooksThenTotalPriceShouldBeReducedBy20Percent() throws Exception {
        bookServicesController.addToCart(1L);
        bookServicesController.addToCart(2L);
        bookServicesController.addToCart(3L);
        bookServicesController.addToCart(4L);
        // 4*(50-(50*0.2) = 160
        assertEquals("Total price : 160.0€", bookServicesController.computeTotalPriceFromCart());
    }

    @Test
    void whenComputeTotalPriceFiveDifferentBooksThenTotalPriceShouldBeReducedBy25Percent() throws Exception {
        bookServicesController.addToCart(1L);
        bookServicesController.addToCart(2L);
        bookServicesController.addToCart(3L);
        bookServicesController.addToCart(4L);
        bookServicesController.addToCart(5L);

        // 5*(50-(50*0.25) = 187,5
        assertEquals("Total price : 187.5€", bookServicesController.computeTotalPriceFromCart());
    }

    @Test
    void whenComputeTotalPriceTwoDifferentBooksWith2QuantityEachThenTotalPriceShouldBeReducedBy5Percent() throws Exception {
        bookServicesController.addToCart(1L);
        bookServicesController.addToCart(1L);

        bookServicesController.addToCart(2L);
        bookServicesController.addToCart(2L);

        // 2*(50-(50*0.05) = 95
        // 2*(50-(50*0.05) = 95
        assertEquals("Total price : 190.0€", bookServicesController.computeTotalPriceFromCart());
    }

    @Test
    void whenComputeTotalPriceTwoSameBooksAndOneDifferentThenTotalPriceMustBe2ReducedPricePlusOneFullPrice() throws Exception {
        bookServicesController.addToCart(1L);
        bookServicesController.addToCart(1L);

        bookServicesController.addToCart(2L);
        // 2*(50-(50*0,05)) + 50 = 145
        assertEquals("Total price : 145.0€", bookServicesController.computeTotalPriceFromCart());
    }

    @Test
    void whenComputeTotalPriceTwoSameBooks2TimesAndOneDifferentThenTotalPriceMustBe2ReducedPricePlusOneFullPrice() throws Exception {
        bookServicesController.addToCart(1L);
        bookServicesController.addToCart(1L);

        bookServicesController.addToCart(2L);
        bookServicesController.addToCart(2L);
        bookServicesController.addToCart(2L);

        // 4*(50-(50*0,05)) + 50 = 240
        assertEquals("Total price : 240.0€", bookServicesController.computeTotalPriceFromCart());
    }

    @Test
    void whenComputeTotalPriceSimpleExampleThenTotalPriceMustCalculate() throws Exception {
        HashMap<Book, Integer> listBook = bookServicesController.getCart().getListBooks();
        bookServicesController.addToCart(1L);
        bookServicesController.addToCart(1L);

        bookServicesController.addToCart(2L);
        bookServicesController.addToCart(2L);

        bookServicesController.addToCart(3L);
        bookServicesController.addToCart(3L);

        bookServicesController.addToCart(4L);

        bookServicesController.addToCart(5L);

        // 2*(200-(200*0,2) = 320
        assertEquals("Total price : 320.0€", bookServicesController.computeTotalPriceFromCart());
    }
}