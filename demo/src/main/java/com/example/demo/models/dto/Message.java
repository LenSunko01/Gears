package com.example.demo.models.dto;

public class Message {
    public enum MessageType { FIRSTTYPE, SECONDTYPE, THIRDTYPE, FOURTHTYPE }

    private MessageType message;

    public Message(MessageType message) {
        this.message = message;
    }

    public Message() { }

    public void setMessage(MessageType message) {
        this.message = message;
    }

    public MessageType getMessage() {
        return message;
    }
}
