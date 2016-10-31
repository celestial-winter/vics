package com.infinityworks.webapp.error;

public class VotersNotFoundFailure extends CanvassError {
    public VotersNotFoundFailure(String message) {
        super(message);
    }

    public VotersNotFoundFailure(String message, String custom) {
        super(message, custom);
    }
}
