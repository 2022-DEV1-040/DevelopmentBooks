package be.bnp.developmentBooks.service;


import be.bnp.developmentBooks.dto.Cart;

public interface CartService {

    void add(long id) throws Exception;

    void decrease(long id) throws Exception;

    String displayCart();

    double computeTotalPrice() throws Exception;

    Cart getCart();

    void setCart(Cart previousCart);

    void clear();
}
