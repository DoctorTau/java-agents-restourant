package com.agents;

import java.util.ArrayList;

public class Dish {
    private final ArrayList<Product> products;

    public Dish(ArrayList<Product> products) {
        this.products = products;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public boolean isPossible(ArrayList<Product> availovle_products) {
        for (Product product : products) {
            if (!availovle_products.contains(product)) {
                return false;
            }
        }
        return true;
    }
}
