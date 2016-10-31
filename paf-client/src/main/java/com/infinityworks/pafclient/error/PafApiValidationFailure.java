package com.infinityworks.pafclient.error;

public class PafApiValidationFailure extends PafFailure {
    public PafApiValidationFailure(String message) {
        super(message);
    }

    public PafApiValidationFailure(String message, String custom) {
        super(message, custom);
    }
}
