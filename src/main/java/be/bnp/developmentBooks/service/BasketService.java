package be.bnp.developmentBooks.service;


import be.bnp.developmentBooks.dto.Basket;

public interface BasketService {

    void add(long id) throws Exception;

    String displayBasket();

    Basket getBasket();
}
