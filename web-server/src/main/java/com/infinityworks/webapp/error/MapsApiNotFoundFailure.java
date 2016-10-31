package com.infinityworks.webapp.error;

public class MapsApiNotFoundFailure extends CanvassError {
    public MapsApiNotFoundFailure(String message) {
        super(message);
    }

    public MapsApiNotFoundFailure(String message, String custom) {
        super(message, custom);
    }
}
