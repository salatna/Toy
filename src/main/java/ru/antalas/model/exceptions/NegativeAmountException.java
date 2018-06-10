package ru.antalas.model.exceptions;

public class NegativeAmountException extends ModelException {
    public NegativeAmountException(String message) {
        super(message);
    }
}
