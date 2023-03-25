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

    private void sendProduct(Message message) {
        String productId = message.getData();
        for (Product product : products) {
            if (product.getId().equals(productId) && product.getStatus() == Product.ProductStatus.RESERVED) {
                Message productMessage = new Message(message.getSource(), AgentNames.STORAGE,
                        MessageType.ProductResponse,
                        product.getId());
                removeProduct(product);
            }
        }

        // TODO: send message to logger that there is no such product
    }

    private void removeProduct(Product product) {
        products.remove(product);
    }
}
