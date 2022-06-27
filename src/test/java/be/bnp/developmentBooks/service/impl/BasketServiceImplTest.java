package be.bnp.developmentBooks.service.impl;

import be.bnp.developmentBooks.entity.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BasketServiceImplTest {

    @Autowired
    BasketServiceImpl basketService;

    @Test
    void whenFindBookByIdWithExistingIdThenReturnTheBook() throws Exception {
        Book book = basketService.findBookById(1L);
        assertEquals(1L, book.getId());
        assertEquals("Clean Code (Robert Martin, 2008)", book.getName());
    }

    @Test
    void whenFindBookByIdWithNoExistingIdThenReturnTheBook() {
        try {
            Book book = basketService.findBookById(7L);
        } catch (Exception e) {
            assertEquals("book with id: 7 not exist",e.getMessage());
        }
    }
}