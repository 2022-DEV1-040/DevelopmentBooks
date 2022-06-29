package be.bnp.developmentBooks.service.impl;


import be.bnp.developmentBooks.dto.Cart;
import be.bnp.developmentBooks.entity.Book;
import be.bnp.developmentBooks.repository.BookRepository;
import be.bnp.developmentBooks.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

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
        } else {
           cart.getListBooks().remove(book);
        }

        cart.getListBooks().put(book, quantity);
    }

    @Override
    public String displayCart() {
        HashMap<Book, Integer> listBook = cart.getListBooks();
        if (listBook.keySet().size() > 0) {
            StringBuilder content = new StringBuilder();
            for (Book book : listBook.keySet()) {
                String name = book.getName();
                int quantity = listBook.get(book);
                content.append("id: " + book.getId() + " " + name + " quantity :  " + quantity + "</br></br>");
            }

            return content.toString();
        } else {
            return "cart is empty";
        }
    }

    @Override
    public double computeTotalPrice() throws Exception {
        double totalMin = 0;
        double totalTemp = 0;
        double previousTotalTemp = 0;
        int maxOccurences;
        int totalBookValue;

        HashMap<Book,Integer> listBooks = (HashMap<Book, Integer>) cart.getListBooks().clone();
        HashMap<Book,Integer> previousListBooks;

        for(int i=listBooks.size(); i>0; i--) {
            int j = i;
            while (j>0 && listBooks.size()>0) {
                maxOccurences = calculateMaxOccurence((HashMap<Book, Integer>) listBooks.clone(), j);
                totalBookValue = BOOK_VALUE * j;
                previousTotalTemp = totalTemp;
                totalTemp += maxOccurences * (totalBookValue - (totalBookValue * reductions[j - 1]));
                previousListBooks = (HashMap<Book, Integer>) listBooks.clone();
                decreaseBooksBy(maxOccurences, listBooks, j);
                // if the list books is empty all occurence are optimize
                if (!listBooks.isEmpty() && maxOccurences > 1) {
                    Cart cart = recalculateWithLowerOccurence(maxOccurences, previousListBooks, listBooks, previousTotalTemp, totalTemp, j);
                    if (totalTemp > cart.getTotalPrice()) {
                        listBooks = cart.getListBooks();
                        totalTemp = cart.getTotalPrice();
                    }
                }
                if(j == listBooks.size() && listBooks.size() > 1) {
                    j--;
                } else {
                    j = listBooks.size();
                }
            }

            if(totalTemp > 0 && (totalTemp < totalMin || totalMin == 0)) {
                totalMin = totalTemp;
            }
            listBooks = (HashMap<Book, Integer>) cart.getListBooks().clone();
            totalTemp = 0;
        }
        return totalMin;
    }

    private int calculateMaxOccurence(HashMap<Book, Integer> map, int numberOfDifferentBooks) {
        int maxOccurences = Collections.min(map.values());
        if (numberOfDifferentBooks == map.size()) {
            return maxOccurences;
        } else {
            maxOccurences = 0;
        }

        List<Integer> values = new ArrayList<>(map.values());
        // begin with the max value to avoid deleted books with lower quantity
        Collections.sort(values, Collections.reverseOrder());
        boolean continuous = true;

        while (continuous) {
            for (int i = 0; i < numberOfDifferentBooks; i++) {
                int value = values.get(i);
                value--;
                values.set(i, value);
            }
            maxOccurences++;
            values.removeIf(v -> v == 0);
            if (values.size() < numberOfDifferentBooks) {
                continuous = false;
            }
        }
        return maxOccurences;
    }

    private Cart recalculateWithLowerOccurence(int maxOccurences, HashMap<Book, Integer> previousListBooks, HashMap<Book, Integer> listBooks, double previousTotalTemp, double totalTemp, int numberOfDifferentBooks) throws Exception {
        int totalBookValue;
        if (maxOccurences > 2 && listBooks.size() > 2) {
            int sizeOfListBooks = listBooks.size();
            decreaseBooksBy(maxOccurences - 1, previousListBooks, numberOfDifferentBooks);
            if (sizeOfListBooks < previousListBooks.size()) {
                totalTemp = previousTotalTemp;
                maxOccurences--;
                listBooks = (HashMap<Book, Integer>) previousListBooks.clone();
                totalBookValue = BOOK_VALUE * listBooks.size();
                previousTotalTemp = totalTemp;
                totalTemp += maxOccurences * (totalBookValue - (totalBookValue * reductions[listBooks.size() - 1]));
            } else {
                return new Cart(totalTemp, listBooks);
            }
        } else {
            return new Cart(totalTemp, listBooks);
        }
        return recalculateWithLowerOccurence(maxOccurences, previousListBooks, listBooks, previousTotalTemp, totalTemp, numberOfDifferentBooks);
    }

    private void decreaseBooksBy(int maxOccurences, HashMap<Book, Integer> listBooks, int numberBookToDecrease) throws Exception {
        if (numberBookToDecrease > listBooks.size()) {
            throw new Exception("Impossible to decrease " + numberBookToDecrease + " we have only " + listBooks.size() + " books in the list");
        }

        int totalQuantityDecrease = numberBookToDecrease * maxOccurences;

        Iterator<Map.Entry<Book, Integer>> iterator = listBooks.entrySet().iterator();
        while (totalQuantityDecrease > 0) {
            Map.Entry<Book, Integer> book = iterator.next();
            int quantity = book.getValue() - 1;
            if (quantity == 0) {
                iterator.remove();
            } else {
                book.setValue(quantity);
            }
            totalQuantityDecrease--;
            if (!iterator.hasNext()) {
                if (listBooks.isEmpty()) {
                    break;
                } else {
                    iterator = listBooks.entrySet().iterator();
                }
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

    @Override
    public void setCart(Cart cart) {
        this.cart = cart;
    }

    @Override
    public void clear() {
        setCart(new Cart());
    }
}
