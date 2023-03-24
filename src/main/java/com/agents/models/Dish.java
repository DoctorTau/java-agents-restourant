package com.agents.models;

import java.util.ArrayList;

public class Dish {
    private ArrayList<Product> products;

    Dish(String json) {
        // TODO: decode from json
    }

    ArrayList<Product> getProducts() {
        return products;
    }
}
