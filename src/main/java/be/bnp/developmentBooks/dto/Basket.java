package be.bnp.developmentBooks.dto;

import be.bnp.developmentBooks.entity.Book;
import lombok.Data;

import java.util.HashMap;

@Data
public class Basket {

    long totalPrice;
    // book with quantity
    HashMap<Book,Integer> listBooks = new HashMap<>();
}
