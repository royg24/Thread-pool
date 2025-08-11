package tests_utils;

public enum Colors {
    RESET ("\u001B[0m"),
    RED ("\u001B[31m"),
    GREEN ("\u001B[32m"),
    YELLOW ("\u001B[33m"),
    BLUE ("\u001B[34m"),
    PURPLE ("\u001B[35m"),
    CYAN ("\u001B[36m");

    public final String value;
    Colors(String value) {
        this.value = value;
    }
}

