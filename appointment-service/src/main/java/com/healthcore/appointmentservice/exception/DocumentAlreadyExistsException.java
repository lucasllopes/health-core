package com.healthcore.appointmentservice.exception;

public class DocumentAlreadyExistsException extends RuntimeException {
    public DocumentAlreadyExistsException(String message) {
        super(message);
    }
}
