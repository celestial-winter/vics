package com.infinityworks.pafclient.error;

public class PafApiFailure extends PafFailure {
    public PafApiFailure(String message) {
        super(message);
    }
    public PafApiFailure(String message, Exception e) {
        super(message, e);
    }
}
