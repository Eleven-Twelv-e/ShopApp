package com.nicatmagerramov.products;

import com.nicatmagerramov.interfaces.StringFormatter;

public class Product {
    private String name;
    private double weight;
    private double price;
    private StringFormatter stringFormatter;

    public Product(final String name, final double weight, final double price) {
        this.name = name;
        this.weight = weight;
        this.price = price;
        this.stringFormatter = () -> "Product: " + this.name + " has price " + this.price + ".";
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(final double price) {
        this.price = price;
    }

    public void setStringFormatter(final StringFormatter stringFormatter) {
        this.stringFormatter = stringFormatter;
    }

    @Override
    public String toString() {
        return stringFormatter.formatToString();
    }
}
