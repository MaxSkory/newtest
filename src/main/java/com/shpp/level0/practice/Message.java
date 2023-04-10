package com.shpp.level0.practice;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("Greetings")
public class Message {

    private final String message;

    public Message(String message) {
        this.message = message;
    }
    @JsonGetter("message")
    public String getMessage() {
        return message;
    }
}
