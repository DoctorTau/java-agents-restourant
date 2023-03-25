package com.agents;

public enum MessageType {
    MenuRequest, // request for menu (from customer to admin and from admin to storage)
    MenuResponse, // response for menu (from storage to admin and from admin to customer)
    OrderAdd,
    OrderRequest,
    OrderRespond,
    ProcessRequest,
    ProcessRespond,
    WorkRequest,
    WorkRespond,
    DevicesRequest,
    ProductRequest,
    ProductResponse
}
