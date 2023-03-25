package com.agents;

import java.util.ArrayList;

public class Dish {
    private final ArrayList<Dish> products;

    Dish(ArrayList<Dish> products) {
        this.products = products;
    }

    ArrayList<Dish> getProducts() {
        return products;
    }
}
