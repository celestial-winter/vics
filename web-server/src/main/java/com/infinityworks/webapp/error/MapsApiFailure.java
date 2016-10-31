package com.infinityworks.webapp.error;

public class MapsApiFailure extends CanvassError {
    public MapsApiFailure(String message) {
        super(message);
    }
    public MapsApiFailure(String message, Exception e) {
        super(message, e);
    }
}
