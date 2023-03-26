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
            switch (message.getType()) {
                case MenuRequest:
                    sendCurrentMenu(message);
                    break;
                case OrderRequest:
                    reserveProductsForMenu(message);
                    break;
                case ProcessRequest:
                    sendProduct(message);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Provides menu to the client.
     * 
     * @param message - message with the client name
     * @throws JsonProcessingException - if there is an error while parsing json
     */
    private void sendCurrentMenu(Message message) throws JsonProcessingException {
        Menu currentMenu = new Menu();
        fillMenu(currentMenu);
        VisitorMenu visitorMenu = new VisitorMenu(message.getData(), currentMenu);
        Message menuMessage = new Message(AgentNames.ADMIN, AgentNames.STORAGE, MessageType.MenuRespond,
                visitorMenu.toJson());
        sendMessage(menuMessage);
    }

    /**
     * Fills the menu with dishes that are possible to cook with the available
     * 
     * @param currentMenu - menu to be filled
     */
    private void fillMenu(Menu currentMenu) {
        for (Dish dish : full_menu.getDishes()) {
            if (dish.isPossible(getAvailableProducts())) {
                currentMenu.addDish(dish);
            }
        }
    }

    /**
     * @return list of free products.
     */
    private ArrayList<Product> getAvailableProducts() {
        ArrayList<Product> available = new ArrayList<Product>();
        for (Product product : products) {
            if (product.getStatus() == Product.ProductStatus.FREE) {
                available.add(product);
            }
        }
        return available;
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

    /**
     * Sends a product to the client.
     * 
     * @param message - message with the product id
     */
    private void sendProduct(Message message) {
        String productId = message.getData();
        for (Product product : products) {
            if (product.getId().equals(productId) && product.getStatus() == Product.ProductStatus.RESERVED) {
                Message productMessage = new Message(message.getSource(), AgentNames.STORAGE,
                        MessageType.ProductRespond,
                        product.getId());
                sendMessage(productMessage);
                removeProduct(product);
            }
        }

        // TODO: send message to logger that there is no such product
    }

    /**
     * Removes a product from the storage.
     * 
     * @param product - product to be removed
     */
    private void removeProduct(Product product) {
        products.remove(product);
    }
}
