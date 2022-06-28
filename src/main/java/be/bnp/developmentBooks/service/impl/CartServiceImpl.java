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

    private double[] reductions = {0,0.05,0.1,0.2,0.25};

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
    public String displayCart() {
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
    public double computeTotalPrice() {
        double totalMin = 0;
        double totalTemp = 0;
        int maxOccurences;
        int totalBookValue;

        HashMap<Book,Integer> listBooks = (HashMap<Book, Integer>) cart.getListBooks().clone();

        for(int i=listBooks.size(); i>0; i--) {
            int j = i;
            while (j>0 && listBooks.size()>0) {
                maxOccurences = Collections.min(listBooks.values());
                totalBookValue = BOOK_VALUE * j;
                totalTemp += maxOccurences * (totalBookValue - (totalBookValue * reductions[j-1]));
                decreaseNumberBooksBy(maxOccurences, listBooks, j);
                j = listBooks.size();
            }

            if(totalTemp > 0 && (totalTemp < totalMin || totalMin == 0)) {
                totalMin = totalTemp;
            }
            listBooks = (HashMap<Book, Integer>) cart.getListBooks().clone();
            totalTemp = 0;
        }
        return totalMin;
    }

    private void decreaseNumberBooksBy(int maxOccurences, HashMap<Book, Integer> listBooks, int numberBookToDecrease) {
        for (Book book : listBooks.keySet()) {
            int quantity = listBooks.get(book);
            quantity -= maxOccurences;
            listBooks.put(book,quantity);
            numberBookToDecrease--;
            if (numberBookToDecrease == 0) {
                break;
            }
        }
        listBooks.entrySet().removeIf(entry -> entry.getValue() == 0);
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
