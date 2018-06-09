package ru.antalas;

public enum Routes {
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
