package com.gdjfx;

public class Initializer {
    // Thank you to https://stackoverflow.com/questions/52569724/javafx-11-create-a-jar-file-with-gradle for saving me from 4+ hours of dev hell!! :)
    // JavaFX doesn't exactly play well with shadow/fat jarring, and this workaround lets JavaFX actually work (for some reason).
    // Probably same reason why Replit does something similar. Blame Java.
    public static void main(String[] args) {
        com.gdjfx.app.ProgramApplet.main(args);
    }
}
