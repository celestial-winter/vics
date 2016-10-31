package com.infinityworks.pafclient.error;

public class PafFailure extends Exception {
    private final String custom;

    PafFailure(String message) {
        super(message);
        this.custom = "";
    }

    PafFailure(String message, String custom) {
        super(message);
        this.custom = custom;
    }

    PafFailure(String message, Exception e) {
        super(message, e);
        this.custom = "";
    }

    public String getCustom() {
        return custom;
    }
}