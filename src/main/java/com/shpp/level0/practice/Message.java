package com.shpp.level0.practice;

public class Message {
    private static final String GREETINGS = "Привіт";
    private static final String EXCLAMATION_POINT = "!";
    private final String message;

    public Message(String name) {
        StringBuilder builder = new StringBuilder();
        builder.append(name).append(GREETINGS).append(EXCLAMATION_POINT);
        this.message = builder.toString();
    }

    public String getMessage() {
        return message;
    }
}
