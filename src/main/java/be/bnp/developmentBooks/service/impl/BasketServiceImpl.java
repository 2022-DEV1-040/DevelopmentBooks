package be.bnp.developmentBooks.service.impl;


import be.bnp.developmentBooks.dto.Basket;
import be.bnp.developmentBooks.entity.Book;
import be.bnp.developmentBooks.repository.BookRepository;
import be.bnp.developmentBooks.service.BasketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BasketServiceImpl implements BasketService {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public void add(long id, Basket basket) {

    }

    public Book findBookById(long id) throws Exception {
        Optional<Book> book = bookRepository.findById(id);
        if (!book.isPresent()) {
            throw new Exception("book with id: " + id + " not exist");
        } else {
            return  book.get();
        }
    }
}
