package com.agents;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

public class Menu {
    private ArrayList<Dish> dishes;

    public Menu() {
        dishes = new ArrayList<>();
    }

    public void addDish(Dish dish) {
        dishes.add(dish);
    }

    public ArrayList<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(ArrayList<Dish> dishes) {
        this.dishes = dishes;
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    public static Menu fromJson(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Menu.class);
    }
}
