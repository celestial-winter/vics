package com.infinityworks.webapp.error;

class ErrorEntity {
    private final String type;
    private final String message;
    private final String custom;

    ErrorEntity(String type, String message, String custom) {
        this.type = type;
        this.message = message;
        this.custom = custom;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getCustom() {
        return custom;
    }
}
