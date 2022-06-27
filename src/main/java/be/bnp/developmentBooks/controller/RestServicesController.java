package be.bnp.developmentBooks.controller;

import be.bnp.developmentBooks.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestServicesController {

    private static final Logger logger = LoggerFactory.getLogger(RestServicesController.class);

    @Autowired
    BookService bookService;

    @GetMapping(value = "/ping")
    public ResponseEntity<String> ping()
    {
        logger.info("Démarrage des services OK .....");
        return new ResponseEntity<String>("Réponse du serveur: "+ HttpStatus.OK.name(), HttpStatus.OK);
    }
}