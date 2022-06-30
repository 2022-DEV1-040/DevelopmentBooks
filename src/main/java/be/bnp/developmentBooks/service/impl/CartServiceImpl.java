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

    private double[] REDUCTIONS = {0,0.05,0.1,0.2,0.25};

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
        // if size = 0 the cart is empty
        if (listBook.size() > 0) {
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
        double bestTotalFound = 0;
        boolean packageSizeUpdated;
        int packageSize;

        // totalTemp is compared with bestTotalFound after each calcul to find the best total
        double totalTemp = 0;

        // maxNumberOfPackage this is the largest feasible package with n different books
        int maxNumberOfPackage;

        /* previousTotalTemp needed because when we have maxNumberOfPackage > 1 we want to try with maxNumberOfPackage - 1 for see if we can make the next package lower
           example: we can make 2 package of 5 differents books and 1 package of 3 differents books.
            If we reduce the maxNumberOfPackage to take only 1 package of 5 differents books so we can make 1 package of 4 differents books and the price can be better.
         */
        double previousTotalTemp = 0;

        // the price without REDUCTIONS for one package
        int totalPackageValue;

        HashMap<Book,Integer> listBooks = (HashMap<Book, Integer>) cart.getListBooks().clone();

        // previousListBooks same usage of previousTotal Temp
        HashMap<Book,Integer> previousListBooks;

        /* 2 "for" the first is necessary for try with lower package otherwise he take only the biggest.
            We begin with listBooks.size(), the size give us the numbers of differents books.
            example : si the test whenComputeTotalPriceSimpleExampleThenTotalPriceMustCalculate.
            the second is for calculate the amount with all package possible.
         */
        for(int i=listBooks.size(); i>0; i--) {
            packageSize = i;
            listBooks = (HashMap<Book, Integer>) cart.getListBooks().clone();
            totalTemp = 0;
            while (packageSize>0 && listBooks.size()>0) {
                packageSizeUpdated = false;
                previousTotalTemp = totalTemp;
                maxNumberOfPackage = calculateMaxNumberOfPackage((HashMap<Book, Integer>) listBooks.clone(), packageSize);
                totalPackageValue = BOOK_VALUE * packageSize;
                totalTemp += maxNumberOfPackage * (totalPackageValue - (totalPackageValue * REDUCTIONS[packageSize - 1]));
                // We must clone otherwise when we modify listBooks the previousListBooks is modify too (same reference)
                previousListBooks = (HashMap<Book, Integer>) listBooks.clone();
                // decrease the quantity for all books depending maxNumberOfPackage and package size
                decreaseBooksBy(maxNumberOfPackage, listBooks, packageSize);

                // if the list books is empty all maxNumberOfPackage are optimize and if maxNumberOfPackage > 1 it's not necessary to try with a lower maxNumberOfPackage because it's try with the next iteration of i
                if (!listBooks.isEmpty() && maxNumberOfPackage > 1) {
                    Cart cart = recalculateWithLowerNumberOfPackage(maxNumberOfPackage, previousListBooks, listBooks, previousTotalTemp, totalTemp, packageSize);
                    // if best result, the listBooks becomes the one used in the recalculateWithLowerNumberOfPackage method
                    if (totalTemp > cart.getTotalPrice()) {
                        listBooks = cart.getListBooks();
                        totalTemp = cart.getTotalPrice();
                        if (listBooks.size() > 0) {
                            // we need to decrease package size because when we try with lower maxNumberOfPackage the listBooks.size is not modify
                            packageSize--;
                            packageSizeUpdated = true;
                        }
                    }
                }

                if(!packageSizeUpdated) {
                    packageSize = listBooks.size();
                }
            }

            if(totalTemp > 0 && (totalTemp < bestTotalFound || bestTotalFound == 0)) {
                bestTotalFound = totalTemp;
            }
        }
        return bestTotalFound;
    }

    private int calculateMaxNumberOfPackage(HashMap<Book, Integer> listBook, int packageSize) {
        int maxNumberOfPackage = 0;
        List<Integer> values = new ArrayList<>(listBook.values());

        // if the package size is the same as listBook we can take the minimum value as maxNumberOfPackage
        if (packageSize == listBook.size()) {
            return Collections.min(values);
        }

        // begin with the max value to avoid deleted books with lower quantity
        Collections.sort(values, Collections.reverseOrder());
        boolean continueSearch = true;

        while (continueSearch) {
            // loop for reduces 1 packageSize in the list
            for (int i = 0; i < packageSize; i++) {
                int value = values.get(i);
                value--;
                values.set(i, value);
            }
            maxNumberOfPackage++;
            values.removeIf(v -> v == 0);
            // if we no longer have enough value for 1 package size we stop de loop and we have the max maxNumberOfPackage
            if (values.size() < packageSize) {
                continueSearch = false;
            }
        }
        return maxNumberOfPackage;
    }

    private Cart recalculateWithLowerNumberOfPackage(int maxNumberOfPackage, HashMap<Book, Integer> previousListBooks, HashMap<Book, Integer> listBooks, double previousTotalTemp, double totalTemp, int packageSize) throws Exception {
        int totalBookValue;
        /* maxNumberOfPackage > 1 because we try to decrease books with maxNumberOfPackage -1
           if listBook.size == 1 or 0 we can not make another lower package
         */
        if (maxNumberOfPackage > 1 && listBooks.size() > 1) {
            int sizeOfListBooks = listBooks.size();
            decreaseBooksBy(maxNumberOfPackage - 1, previousListBooks, packageSize);
            // if after decreaseBooksBy(maxNumberOfPackage - 1) we have more differentsBooks so it's more advantageous in this case we take the previous result (before it was decrease with the maxNumberOfPackage for recalculate with a lower numberOfPackage)
            if (sizeOfListBooks < previousListBooks.size()) {
                totalTemp = previousTotalTemp;
                listBooks = (HashMap<Book, Integer>) previousListBooks.clone();
                maxNumberOfPackage--;
                totalBookValue = BOOK_VALUE * listBooks.size();
                totalTemp += maxNumberOfPackage * (totalBookValue - (totalBookValue * REDUCTIONS[listBooks.size() - 1]));
            }
        }
        return new Cart(totalTemp, listBooks);
    }

    private void decreaseBooksBy(int maxNumberOfPackage, HashMap<Book, Integer> listBooks, int numberBookToDecrease) throws Exception {
        if (numberBookToDecrease > listBooks.size()) {
            throw new Exception("Impossible to decrease " + numberBookToDecrease + " we have only " + listBooks.size() + " books in the list");
        }

        int totalQuantityDecrease = numberBookToDecrease * maxNumberOfPackage;

        Iterator<Map.Entry<Book, Integer>> iterator = listBooks.entrySet().iterator();
        // We don't mention iterator.hasNext() in the while car if he has not next so we come back to the begin
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
                    iterator = listBooks.entrySet().iterator();
                }
            }
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
