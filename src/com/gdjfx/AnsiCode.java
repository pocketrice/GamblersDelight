package com.gdjfx;

public enum AnsiCode {
    ANSI_BLACK(0, "\u001B[30m"),
    ANSI_RED(1, "\u001B[31m"),
    ANSI_GREEN(2, "\u001B[32m"),
    ANSI_YELLOW(3, "\u001B[33m"),
    ANSI_BLUE(4, "\u001B[34m"),
    ANSI_PURPLE(5, "\u001B[35m"),
    ANSI_CYAN(6, "\u001B[36m"),
    ANSI_WHITE(7, "\u001B[37m"),
    ANSI_RESET(8, "\u001B[0m");

    private final int value;
    private final String code;

    AnsiCode(int value, String code) {
        this.value = value;
        this.code = code;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return code;
    }
}
