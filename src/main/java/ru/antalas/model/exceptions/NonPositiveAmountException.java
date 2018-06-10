package ru.antalas.model.exceptions;

public class NonPositiveAmountException extends ModelException {
    public NonPositiveAmountException(String message) {
        super(message);
    }
}
