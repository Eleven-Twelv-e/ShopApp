package com.nicatmagerramov;

import com.nicatmagerramov.exceptions.InventoryException;
import com.nicatmagerramov.interfaces.StringFormatter;
import com.nicatmagerramov.products.Product;

import java.text.DecimalFormat;

public class Shop {
    private Inventory inventory;
    private StringFormatter stringFormatter;

    public Shop() {
        this.inventory = new Inventory();
        this.stringFormatter = () -> {
            final int inventorySize = this.inventory.getProducts().size();
            final String itemString = inventorySize > 1 ? inventorySize + " products" : inventorySize + " product";
            final DecimalFormat total = new DecimalFormat("####0.0");
            return String.format("Shop has inventory with %s and total value of %s", itemString, total.format(inventory.calculateTotal()));
        };
    }

    public void addToInventory(final Product product, final int amount) throws InventoryException {
        this.inventory.addProducts(product, amount);
    }

    public void removeFromInventory(final Product product, final int amount) throws InventoryException {
        this.inventory.removeProducts(product, amount);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setStringFormatter(final StringFormatter stringFormatter) {
        this.stringFormatter = stringFormatter;
    }

    @Override
    public String toString() {
        return this.stringFormatter.formatToString();
    }
}
