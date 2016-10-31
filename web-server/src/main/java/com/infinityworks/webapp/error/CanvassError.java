package com.infinityworks.webapp.error;

class CanvassError extends Exception {
    private final String custom;

    CanvassError(String message) {
        super(message);
        this.custom = "";
    }

    CanvassError(String message, String custom) {
        super(message);
        this.custom = custom;
    }

    CanvassError(String message, Exception e) {
        super(message, e);
        this.custom = "";
    }

    public String getCustom() {
        return custom;
    }
}
