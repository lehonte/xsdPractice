package org.example.exceptions;

public class UnknownStatusException extends RuntimeException {
    public UnknownStatusException(String message) {
        super(message);
    }
}
