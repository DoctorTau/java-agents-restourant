package com.agents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Message {
    private String destination;
    private String source;
    private String data;
    private MessageType type;

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Message() {
        this.destination = "";
        this.source = "";
        this.data = "";
    }

    /**
     * @param destination destination agent name
     * @param source      source agent name
     * @param type        message type
     * @param data        message data
     */
    public Message(String destination, String source, MessageType type, String data) {
        this.destination = destination;
        this.source = source;
        this.data = data;
        this.type = type;
    }

    /**
     * @param destination destination agent name
     * @param source      source agent name
     * @param data        message data
     */
    public Message(String destination, String source, String data) {
        this.destination = destination;
        this.source = source;
        this.data = data;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    /**
     * @return json string
     * @throws JsonProcessingException if json processing fails
     */
    public String toJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    public static Message fromJson(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Message.class);
    }
}
