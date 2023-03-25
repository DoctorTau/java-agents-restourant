package com.agents.models;

import com.agents.AgentNames;
import com.agents.Client;
import com.agents.Dish;
import com.agents.Menu;
import com.agents.Message;
import com.agents.MessageType;
import com.agents.Product;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.Socket;
import java.util.ArrayList;

public class Storage extends Client {
    ArrayList<Product> products;
    private Menu full_menu = new Menu();

    public void setFull_menu(Menu menu) {
        this.full_menu = menu;
    }

    public Storage(Socket socket, String clientName) {
        super(socket, clientName);
    }

    @Override
    protected void handleMessage(Message message) {
        try {
            switch (message.getType()) {// TODO
                case MenuRequest:
                    Menu currentMenu = new Menu();
                    fillMenu(currentMenu);
                    VisitorMenu visitorMenu = new VisitorMenu(message.getData(), currentMenu);
                    Message menuMessage = new Message(AgentNames.ADMIN, AgentNames.STORAGE, MessageType.MenuResponse,
                            visitorMenu.toJson());
                    sendMessage(menuMessage);
                    break;
                case OrderRequest:
                    reserveProductsForMenu(message);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillMenu(Menu currentMenu) {
        for (Dish dish : full_menu.getDishes()) {
            if (dish.isPossible(products)) {
                currentMenu.addDish(dish);
            }
        }
    }

    /**
     * @param message - message with the order
     *                Reserves products for the order.
     */
    private void reserveProductsForMenu(Message message) {
        try {
            Menu menu = Menu.fromJson(message.getData());
            ArrayList<Dish> dishes = menu.getDishes();
            for (Dish dish : dishes) {
                for (Product product : dish.getProducts()) {
                    makeReservationForProduct(product);
                }
            }
        } catch (JsonProcessingException e) {
            System.out.println("Error while parsing json");
            e.printStackTrace();
        }
    }

    /**
     * @param product - product to be reserved
     *                Reserves a product in the storage.
     */
    private void makeReservationForProduct(Product product) {
        for (Product storageProduct : products) {
            if (product.getId().equals(storageProduct.getId())
                    && product.getStatus() == Product.ProductStatus.FREE) {
                product.setStatus(Product.ProductStatus.RESERVED);
            }
        }
    }

    private void removeAProduct(Message message) {
        // TODO: gets a requisted product id from the message, removes it from the list
    }
}
