package ru.antalas.model.exceptions;

public class ModelException extends RuntimeException {
    ModelException() {
    }

    ModelException(String message) {
        super(message);
    }
}
