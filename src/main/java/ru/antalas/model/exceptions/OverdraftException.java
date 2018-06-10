package ru.antalas.model.exceptions;

public class OverdraftException extends RuntimeException {
    public OverdraftException(String message) {
        super(message);
    }
}
