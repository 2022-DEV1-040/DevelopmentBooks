package be.bnp.developmentBooks.service.impl;


import be.bnp.developmentBooks.dto.Cart;
import be.bnp.developmentBooks.entity.Book;
import be.bnp.developmentBooks.repository.BookRepository;
import be.bnp.developmentBooks.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private BookRepository bookRepository;

    private Cart cart = new Cart();

    final int BOOK_VALUE = (50);

    @Override
    public void add(long id) throws Exception {
        Book book = findBookById(id);
        Integer quantity = cart.getListBooks().get(book);
        if (quantity != null) {
            quantity++;
        } else {
            quantity = 1;
        }
        cart.getListBooks().put(book, quantity);
    }

    @Override
    public void decrease(long id) throws Exception {
        Book book = findBookById(id);
        Integer quantity = cart.getListBooks().get(book);
        if (quantity != null && quantity != 0) {
            quantity--;
        }
        if (quantity == 0) {
            cart.getListBooks().remove(book);
        } else {
            cart.getListBooks().put(book, quantity);
        }
    }

    @Override
    public String displayCart() throws Exception {
        HashMap<Book, Integer> listBook = cart.getListBooks();
        if (listBook.keySet().size() > 0) {
            StringBuilder content = new StringBuilder();
            for (Book book : listBook.keySet()) {
                String name = book.getName();
                int quantity = listBook.get(book);
                content.append(name + " quantity :  " + quantity + "</br></br>");
            }

            content.append("Total price : " + computeTotalPrice() + "€");
            return content.toString();
        } else {
            return "cart is empty";
        }
    }

    @Override
    public double computeTotalPrice() throws Exception {
        double totalBookValue = 0;
        double reduction = 0;
        int maxOccurences = Collections.min(cart.getListBooks().values());
        Iterator it = cart.getListBooks().entrySet().iterator();

        switch (cart.getListBooks().size()) {
            case 2:
                reduction = 0.05;
                break;
            case 3:
                reduction = 0.1;
                break;
            case 4:
                reduction = 0.2;
                break;
            case 5:
                reduction = 0.25;
                break;
            default:
                reduction = 0;
        }
        while (it.hasNext()) {
            Map.Entry book = (Map.Entry) it.next();

            totalBookValue += maxOccurences * (BOOK_VALUE - (BOOK_VALUE * reduction));
            totalBookValue += BOOK_VALUE * ((Integer) book.getValue() - maxOccurences);
            if ((Integer) book.getValue() - maxOccurences > 0) {
                decrease(((Book) book.getKey()).getId());
            } else {
                it.remove();
            }
        }
        return totalBookValue;
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
    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }
}
