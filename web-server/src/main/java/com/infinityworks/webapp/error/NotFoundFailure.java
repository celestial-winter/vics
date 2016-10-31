package com.infinityworks.webapp.error;

public class NotFoundFailure extends CanvassError {
    public NotFoundFailure(String message) {
        super(message);
    }

    public NotFoundFailure(String message, String custom) {
        super(message, custom);
    }
}
