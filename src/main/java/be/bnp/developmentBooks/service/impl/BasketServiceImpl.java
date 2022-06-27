package be.bnp.developmentBooks.service.impl;


import be.bnp.developmentBooks.dto.Basket;
import be.bnp.developmentBooks.entity.Book;
import be.bnp.developmentBooks.repository.BookRepository;
import be.bnp.developmentBooks.service.BasketService;
import com.fasterxml.jackson.databind.util.TypeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BasketServiceImpl implements BasketService {

    @Autowired
    private BookRepository bookRepository;

    private Basket basket = new Basket();

    @Override
    public void add(long id) throws Exception {
        Book book = findBookById(id);
        Integer quantity = basket.getListBooks().get(book);
        if (quantity != null) {
            quantity++;
        } else {
            quantity = 1;
        }
        basket.getListBooks().put(book, quantity);
    }

    @Override
    public String displayBasket() {
        if (basket.getListBooks().keySet().size() > 0) {
            StringBuilder content = new StringBuilder();
            for (Book book : basket.getListBooks().keySet()) {
                String name = book.getName();
                int quantity = basket.getListBooks().get(book);
                content.append(name + " quantity :  " + quantity);
            }
            return content.toString();
        } else {
            return "basket is empty";
        }
    }

    public Book findBookById(long id) throws Exception {
        Optional<Book> book = bookRepository.findById(id);
        if (!book.isPresent()) {
            throw new Exception("book with id: " + id + " not exist");
        } else {
            return book.get();
        }
    }

    @Override
    public Basket getBasket() {
        return basket;
    }

    public void setBasket(Basket basket) {
        this.basket = basket;
    }
}
