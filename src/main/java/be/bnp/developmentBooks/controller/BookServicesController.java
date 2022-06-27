package be.bnp.developmentBooks.controller;

import be.bnp.developmentBooks.dto.Basket;
import be.bnp.developmentBooks.service.BasketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookServicesController {

    private static final Logger logger = LoggerFactory.getLogger(BookServicesController.class);

    @Autowired
    BasketService basketService;

    Basket basket = new Basket();


    @GetMapping(value = "/ping")
    public ResponseEntity<String> ping()
    {
        logger.info("Démarrage des services OK .....");
        return new ResponseEntity<String>("Réponse du serveur: "+ HttpStatus.OK.name(), HttpStatus.OK);
    }

    @GetMapping(value = "/addToBasket/{id}")
    public void addToBasket(long id) {
        basketService.add(id, basket);
    }

    public Basket getBasket() {
        return basket;
    }

    public void setBasket(Basket basket) {
        this.basket = basket;
    }
}