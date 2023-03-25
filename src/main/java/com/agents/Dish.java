package com.agents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

public class Dish {
    private String name;
    private ArrayList<Product> products;
    private int time;

    public Dish(String name, ArrayList<Product> products, int time) {
        this.name = name;
        this.products = products;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    public static Dish fromJson(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Dish.class);
    }
}
