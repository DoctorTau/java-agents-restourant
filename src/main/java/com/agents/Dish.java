package com.agents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

public class Dish {
    private String name;
    private ArrayList<Product> products;
    private ArrayList<String> instruments;
    private int time;

    public Dish() {
        name = "";
        products = new ArrayList<>();
        instruments = new ArrayList<>();
        time = 0;
    }
    public Dish(String name, ArrayList<Product> products, ArrayList<String> instruments, int time) {
        this.name = name;
        this.products = products;
        this.instruments = instruments;
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

    /**
     * Checks if all products are available
     * 
     * @param availoble_products list of available products
     * @return true if all products are available and false otherwise
     */
    public boolean isPossible(ArrayList<Product> availoble_products) {
        for (Product product : products) {
            if (!availoble_products.contains(product)) {
                return false;
            }
        }
        return true;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public ArrayList<String> getInstruments() {
        return instruments;
    }

    public void setInstruments(ArrayList<String> instruments) {
        this.instruments = instruments;
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
