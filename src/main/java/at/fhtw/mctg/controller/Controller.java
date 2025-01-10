package at.fhtw.mctg.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Controller {

    private final ObjectMapper objectMapper;

    public Controller() {
        this.objectMapper = new ObjectMapper();
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
