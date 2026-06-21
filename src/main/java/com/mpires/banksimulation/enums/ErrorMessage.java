package com.mpires.banksimulation.enums;

public enum ErrorMessage {
    INVALID_EVENT_TYPE("Invalid event type"),
    UNKNOWN_EVENT_TYPE("Unknown event type: "),
    INVALID_REQUEST_FORMAT("Invalid request format or data.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
