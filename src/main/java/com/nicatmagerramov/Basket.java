package com.nicatmagerramov;

import com.nicatmagerramov.exceptions.BasketException;
import com.nicatmagerramov.interfaces.ProductStorage;
import com.nicatmagerramov.interfaces.StringFormatter;
import com.nicatmagerramov.products.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.nicatmagerramov.extras.Utils.iterateSimultaneously;

public class Basket implements ProductStorage {
    private HashMap<Product, Integer> products;
    private StringFormatter stringFormatter;

    public Basket() {
        this.products = new HashMap<>();
        this.stringFormatter = () -> {
            final int basketSize = this.products.size();
            final String itemString = basketSize > 1 ? basketSize + " products." : basketSize + " product.";
            return String.format("Basket has %s", itemString);
        };
    }

    public void addProducts(final Product product, final int amount) throws BasketException {
        if (product != null) {
            final int currentAmount = this.products.getOrDefault(product, 0);
            this.products.put(product, currentAmount + amount);
        } else {
            throw new BasketException("You cannot add Null objects to Basket!");
        }
    }

    public void removeProducts(final Product product, final int amount) throws BasketException {
        if (this.products.get(product) > amount) {
            this.products.replace(product, this.products.get(product) - amount);
        } else if (this.products.get(product) == amount) {
            this.products.replace(product, 0);
        } else {
            throw new BasketException(String.format("Cannot remove %d instances of product as there are only %d instances!",
                    amount, this.products.get(product)));
        }
    }

    public HashMap<Product, Integer> getProducts() {
        return products;
    }

    public double calculateTotal() {
        return this.products.entrySet().
                parallelStream().
                mapToDouble(product -> product.getKey().getPrice() * product.getValue()).
                sum();
    }

    public void setStringFormatter(final StringFormatter stringFormatter) {
        this.stringFormatter = stringFormatter;
    }

    public ArrayList<String> toDBFormat() {
        /*
            On bigger sets this implementation could be a bottleneck. Creation of 'names' and 'amounts' strings could be
            improved by either using threads or combine those two together
         */
        final ArrayList<String> result = new ArrayList<>();
        final String names = this.products.entrySet().
                parallelStream().
                map(p -> p.getKey().getName()).
                collect(Collectors.joining(","));
        final String amounts = this.products.entrySet().
                parallelStream().
                map(p -> p.getValue().toString()).
                collect(Collectors.joining(","));
        result.add(names);
        result.add(amounts);

        return result;
    }

    @Deprecated
    public void restoreFromDB(final String productsName, final String productsAmount) {
        final List<String> names = Arrays.asList(productsName.split(","));
        final List<String> amounts = Arrays.asList(productsAmount.split(","));

        iterateSimultaneously(names, amounts, (String name, String amount) -> {
            try {
                addProducts(new Product(name, 0.150, 0.8), Integer.parseInt(amount));
            } catch (BasketException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public String toString() {
        return stringFormatter.formatToString();
    }
}