package be.bnp.developmentBooks.dto;

import be.bnp.developmentBooks.entity.Book;
import lombok.Data;

import java.util.HashMap;

@Data
public class Cart {

    double totalPrice;
    // book with quantity
    HashMap<Book,Integer> listBooks = new HashMap<>();

    public Cart() {
    }

    public Cart(double totalPrice, HashMap<Book, Integer> listBooks) {
        this.totalPrice = totalPrice;
        this.listBooks = listBooks;
    }

    public Cart(Cart cart) {
        this.totalPrice = cart.getTotalPrice();
        this.listBooks = (HashMap<Book, Integer>) cart.getListBooks().clone();
    }
}
