package com.infinityworks.pafclient.error;

public class PafApiNotFoundFailure extends PafFailure {
    public PafApiNotFoundFailure(String message) {
        super(message);
    }

    public PafApiNotFoundFailure(String message, String custom) {
        super(message, custom);
    }
}
