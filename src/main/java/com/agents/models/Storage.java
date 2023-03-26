package com.agents.models;

import com.agents.*;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Storage extends Client {
    private static final Logger logger = Logger.getLogger(Storage.class.getName());
    private ArrayList<Product> products;
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
                    Menu currentMenu = new Menu();
                    fillMenu(currentMenu);
                    VisitorMenu visitorMenu = new VisitorMenu(message.getData(), currentMenu);
                    Message menuMessage = new Message(AgentNames.ADMIN, AgentNames.STORAGE, MessageType.MenuRespond,
                            visitorMenu.toJson());
                    sendMessage(menuMessage);
                    logger.log(Level.INFO, "Sent menu to the admin.");
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
            logger.log(Level.SEVERE, "Error while handling message", e);
        }
    }

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

    private void sendProduct(Message message) {
        String productId = message.getData();
        for (Product product : products) {
            if (product.getId().equals(productId) && product.getStatus() == Product.ProductStatus.RESERVED) {
                Message productMessage = new Message(message.getSource(), AgentNames.STORAGE,
                        MessageType.ProductRespond, product.getId());
                sendMessage(productMessage);
                removeProduct(product);
                logger.log(Level.INFO, "Product with ID " + productId + " has been sent and removed from storage");
                return;
            }
        }

        logger.log(Level.WARNING, "Product with ID " + productId + " not found or not reserved");
    }

    private void removeProduct(Product product) {
        products.remove(product);
        logger.log(Level.INFO, "Product with ID " + product.getId() + " has been removed from storage");
    }
}
