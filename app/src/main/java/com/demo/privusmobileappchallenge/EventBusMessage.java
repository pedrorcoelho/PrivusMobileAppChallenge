package com.demo.privusmobileappchallenge;

public class EventBusMessage {

    static final String COMMAND_REFRESH_HISTORY = "COMMAND_REFRESH_HISTORY";
    static final String COMMAND_REFRESH_CONTACTS = "COMMAND_REFRESH_CONTACTS";

    private final String message;

    public EventBusMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}