package com.infinityworks.webapp.error;

public class ServerFailure extends CanvassError {
    public ServerFailure(String message) {
        super(message);
    }

    public ServerFailure(String message, Exception ex) {
        super(message, ex);
    }
}
