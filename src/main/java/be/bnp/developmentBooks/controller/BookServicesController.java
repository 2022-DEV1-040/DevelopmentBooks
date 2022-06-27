package be.bnp.developmentBooks.controller;

import be.bnp.developmentBooks.dto.Basket;
import be.bnp.developmentBooks.service.BasketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class BookServicesController {

    private static final Logger logger = LoggerFactory.getLogger(BookServicesController.class);

    @Autowired
    BasketService basketService;

    @GetMapping(value = "/ping")
    public ResponseEntity<String> ping() {
        logger.info("Démarrage des services OK .....");
        return new ResponseEntity<String>("Réponse du serveur: "+ HttpStatus.OK.name(), HttpStatus.OK);
    }

    @GetMapping(value = "/addToBasket/{id}")
    public String addToBasket(@PathVariable long id) throws Exception {
        basketService.add(id);
        return "Book with id " + id + " added to basket ";
    }

    @GetMapping(value = "/showBasket")
    public String showBasket() throws Exception {
        return basketService.displayBasket();
    }

    @ExceptionHandler(Exception.class)
    public String handleError(HttpServletRequest req, Exception ex) {
        logger.error("Request: " + req.getRequestURL() + " ERROR " + ex);

        return ex.getMessage();
    }

    public Basket getBasket() {
        return basketService.getBasket();
    }
}