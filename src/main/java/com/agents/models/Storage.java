package com.agents.models;

import com.agents.*;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Storage extends Client {
    private static final Logger logger = MyLogger.getLogger();
    private final ArrayList<Product> products;
    private final Menu fullMenu;

    public Storage(String clientName, int port, ArrayList<Product> products, Menu fullMenu) {
        super(clientName, port);
        this.products = products;
        this.fullMenu = fullMenu;

        try {
            logger.log(Level.INFO, "Storage created. With menu: " + fullMenu.toJson());
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Error while parsing json", e);
        }
    }

    public Storage(Socket socket, String clientName, ArrayList<Product> products, Menu fullMenu) {
        super(socket, clientName);

        this.products = products;
        this.fullMenu = fullMenu;
    }

    @Override
    protected void handleMessage(Message message) {
        if (!message.getDestination().equals(this.clientName)) {
            return;
        }
        try {
            switch (message.getType()) {
                case MenuRequest:
                    sendCurrentMenu(message);
                    break;
                case OrderRequest:
                    reserveProductsForMenu(message);
                    break;
                case ProductRequest:
                    sendProduct(message);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while handling message", e);
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
        logger.log(Level.INFO, "Sent menu to the admin.");
        sendMessage(menuMessage);
    }

    /**
     * Fills the menu with dishes that are possible to cook with the available
     * 
     * @param currentMenu - menu to be filled
     */
    private void fillMenu(Menu currentMenu) {
        for (Dish dish : fullMenu.getDishes()) {
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
            logger.log(Level.INFO, "Reserved products for the order");
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Error while parsing json", e);
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
                logger.log(Level.INFO, "Reserved product with id " + product.getId());
                return;
            }
        }
        logger.log(Level.WARNING, "Unable to reserve product with id " + product.getId());
    }

    /**
     * Sends a product to the client.
     * 
     * @param message - message with the product id
     */
    private void sendProduct(Message message) {
        String productId = message.getData();
        for (Product product : products) {
            if (product.getId().equals(productId)) {
                Message productMessage = new Message(message.getSource(), AgentNames.STORAGE,
                        MessageType.ProductRespond, product.getId());
                sendMessage(productMessage);
                removeProduct(product);
                return;
            }
        }

        logger.log(Level.WARNING, "Product with ID " + productId + " not found or not reserved");
    }

    /**
     * Removes a product from the storage.
     * 
     * @param product - product to be removed
     */
    private void removeProduct(Product product) {
        products.remove(product);
        logger.log(Level.INFO, "Product with ID " + product.getId() + " has been removed from storage");
    }
}
