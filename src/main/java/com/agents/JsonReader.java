package com.agents;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A utility class for reading JSON files and deserializing them into Java objects.
 */
public class JsonReader {
    private final ObjectMapper objectMapper;

    /**
     * Creates a new instance of the JsonReader class.
     */
    public JsonReader() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Reads a JSON file containing a list of visitor names and returns it as an ArrayList.
     *
     * @param path the path to the JSON file to read.
     * @return an ArrayList containing the visitor names.
     * @throws IOException if there is an error reading the file or deserializing its contents.
     */
    public ArrayList<String> readVisitors(String path) throws IOException {
        File file = new File(path);
        return objectMapper.readValue(file, new TypeReference<ArrayList<String>>() {});
    }

    /**
     * Reads a JSON file containing a menu object and returns it as a Menu object.
     *
     * @param path the path to the JSON file to read.
     * @return a Menu object containing the menu information.
     * @throws IOException if there is an error reading the file or deserializing its contents.
     */
    public Menu readMenu(String path) throws IOException {
        File file = new File(path);
        return objectMapper.readValue(file, Menu.class);
    }

    /**
     * Reads a JSON file containing a list of product objects and returns it as an ArrayList.
     *
     * @param path the path to the JSON file to read.
     * @return an ArrayList containing the product objects.
     * @throws IOException if there is an error reading the file or deserializing its contents.
     */
    public ArrayList<Product> readProducts(String path) throws IOException {
        File file = new File(path);
        return objectMapper.readValue(file, new TypeReference<ArrayList<Product>>() {});
    }

    /**
     * Reads a JSON file containing a list of instrument objects and returns it as an ArrayList.
     *
     * @param path the path to the JSON file to read.
     * @return an ArrayList containing the instrument objects.
     * @throws IOException if there is an error reading the file or deserializing its contents.
     */
    public ArrayList<InstrumentObject> readInstrumentObjects(String path) throws IOException {
        File file = new File(path);
        return objectMapper.readValue(file, new TypeReference<ArrayList<InstrumentObject>>() {});
    }

    /**
     * Reads a JSON file containing a list of cooker names and returns it as an ArrayList.
     *
     * @param path the path to the JSON file to read.
     * @return an ArrayList containing the cooker names.
     * @throws IOException if there is an error reading the file or deserializing its contents.
     */
    public ArrayList<String> readCookers(String path) throws IOException {
        File file = new File(path);
        return objectMapper.readValue(file, new TypeReference<ArrayList<String>>() {});
    }
}
