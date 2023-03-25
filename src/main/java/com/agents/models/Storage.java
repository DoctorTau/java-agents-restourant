package com.agents.models;

import com.agents.Client;
import com.agents.Dish;
import com.agents.Menu;
import com.agents.Message;
import com.agents.MessageType;
import com.agents.Product;

import java.net.Socket;
import java.util.ArrayList;

public class Storage extends Client {
    ArrayList<Product> products;
    private Menu menu = new Menu();

    public void setMenu(Menu menu) {
        this.menu = menu;
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
                    Message menuMessage = new Message("admin", "storage", MessageType.MenuResponse,
                            visitorMenu.toJson());
                    sendMessage(menuMessage);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillMenu(Menu currentMenu) {
        for (Dish dish : menu.getDishes()) {
            if (dish.isPossible(products)) {
                currentMenu.addDish(dish);
            }
        }
    }

    private void provideCurrentMenu(Message message) {
        // TODO: gets a menu from the message, checks if the dish creation is possible,
        // sends a list of possible dishes back to the message sender

    }

    private void removeAProduct(Message message) {
        // TODO: gets a requisted product id from the message, removes it from the list
    }
}
