package ru.antalas.front;

public enum Routes {
    ACCOUNT_CREATE("/accounts/{id}/{amount}"),
    ACCOUNT("/account/{id}"),
    TRANSFER("/transfer/{src}/{dst}/{amt}");

    public String getPath() {
        return path;
    }

    private final String path;

    Routes(String path) {
        this.path = path;
    }
}
