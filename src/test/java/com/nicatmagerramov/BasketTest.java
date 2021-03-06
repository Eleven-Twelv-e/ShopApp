package com.nicatmagerramov;

import com.nicatmagerramov.exceptions.BasketException;
import com.nicatmagerramov.products.Product;
import org.junit.Before;
import org.junit.Test;

import java.text.DecimalFormat;

import static org.junit.Assert.assertTrue;

public class BasketTest {
    private Basket basket;

    @Before
    public void setUp() {
        basket = new Basket();
    }

    @Test
    public void testBasketConstructor() {
        assertTrue(basket instanceof Basket);
    }

    @Test
    public void testAddOneProduct() throws BasketException {
        final Product test = new Product("Apple", 0.150, 0.8);
        basket.addProducts(test, 1);

        assertTrue(basket.getProducts().containsKey(test));
        assertTrue(basket.getProducts().get(test) == 1);
    }

    @Test
    public void testAddTwoSameProducts() throws BasketException {
        final Product test = new Product("Apple", 0.150, 0.8);
        basket.addProducts(test, 2);
        basket.addProducts(test, 2);

        assertTrue(basket.getProducts().containsKey(test));
        assertTrue(basket.getProducts().get(test) == 4);
    }

    @Test
    public void testAddMultipleProducts() throws BasketException {
        final Product test = new Product("Apple", 0.150, 0.8);
        final Product test1 = new Product("Chicken", 1, 2.3);
        final Product test2 = new Product("Beef", 0.5, 3.25);
        basket.addProducts(test, 2);
        basket.addProducts(test1, 3);
        basket.addProducts(test2, 4);

        assertTrue(basket.getProducts().containsKey(test));
        assertTrue(basket.getProducts().containsKey(test1));
        assertTrue(basket.getProducts().containsKey(test2));
        assertTrue(basket.getProducts().get(test) == 2);
        assertTrue(basket.getProducts().get(test1) == 3);
        assertTrue(basket.getProducts().get(test2) == 4);
    }

    @Test(expected = BasketException.class)
    public void testAddNullProduct() throws BasketException {
        basket.addProducts(null, 1);
    }

    @Test
    public void testRemoveOneProduct() throws BasketException {
        final Product test = new Product("Apple", 0.150, 0.8);
        basket.addProducts(test, 2);
        basket.removeProducts(test, 1);

        assertTrue(basket.getProducts().get(test) == 1);
    }

    @Test
    public void testRemoveProductCompletely() throws BasketException {
        final Product test = new Product("Apple", 0.150, 0.8);
        basket.addProducts(test, 2);
        basket.removeProducts(test, 2);

        assertTrue(basket.getProducts().get(test) == 0);
    }

    @Test(expected = BasketException.class)
    public void testRemoveProductMoreThanExistsInBasket() throws BasketException {
        final Product test = new Product("Apple", 0.150, 0.8);
        basket.addProducts(test, 2);
        basket.removeProducts(test, 3);
    }

    @Test
    public void testCalculateTotal() throws BasketException {
        final Product test = new Product("Apple", 0.150, 0.8);
        final Product test1 = new Product("Chicken", 1, 2.3);
        basket.addProducts(test, 2);
        basket.addProducts(test1, 2);
        final DecimalFormat total = new DecimalFormat("####0.0");

        assertTrue(total.format(basket.calculateTotal()).equals("6.2"));
    }

    @Test
    public void testSetStringFormatter() {
        basket.setStringFormatter(() -> "New formatter");

        assertTrue(basket.toString().equals("New formatter"));
    }

    @Test
    public void testToStringOneProduct() throws BasketException {
        final Product test = new Product("Apple", 0.150, 0.8);
        basket.addProducts(test, 2);

        assertTrue(basket.toString().equals("Basket has 1 product."));
    }

    @Test
    public void testToStringMultipleProducts() throws BasketException {
        final Product test = new Product("Apple", 0.150, 0.8);
        final Product test1 = new Product("Chicken", 1, 2.3);
        basket.addProducts(test, 2);
        basket.addProducts(test1, 2);

        assertTrue(basket.toString().equals("Basket has 2 products."));
    }
}
