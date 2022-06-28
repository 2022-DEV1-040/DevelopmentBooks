package be.bnp.developmentBooks.controller;

import be.bnp.developmentBooks.dto.Cart;
import be.bnp.developmentBooks.service.CartService;
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
    CartService cartService;

    @GetMapping(value = "/ping")
    public ResponseEntity<String> ping() {
        logger.info("Démarrage des services OK .....");
        return new ResponseEntity<String>("Réponse du serveur: "+ HttpStatus.OK.name(), HttpStatus.OK);
    }

    @GetMapping(value = "/addToCart/{id}")
    public String addToCart(@PathVariable long id) throws Exception {
        cartService.add(id);

        return "Book with id " + id + " added to cart <br/><br/>";
    }


    @GetMapping(value = "/decreaseFromCart/{id}")
    public String decreaseFromCart(@PathVariable long id) throws Exception {
        cartService.decrease(id);
        return "Book with id " + id + " decreased from cart <br/><br/>";
    }

    @GetMapping(value = "/showCart")
    public String showCart() throws Exception {
        return  cartService.displayCart();
    }

    @GetMapping(value = "/computeTotalPriceFromCart")
    public String computeTotalPriceFromCart() {
        try {
            return "Total price : " + String.format("%.2f",cartService.computeTotalPrice()) + "€";
        } catch (Exception e) {
            logger.error("an exception was thrown", e);
            return "An exception was thrown see the log";
        }
    }

    @ExceptionHandler(Exception.class)
    public String handleError(HttpServletRequest req, Exception ex) {
        logger.error("Request: " + req.getRequestURL() + " ERROR " + ex.getStackTrace());

        return ex.getMessage();
    }

    public Cart getCart() {
        return cartService.getCart();
    }
}