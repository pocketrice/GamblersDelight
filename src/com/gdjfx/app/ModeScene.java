package com.gdjfx.app;

import javafx.scene.text.TextFlow;

import java.io.FileNotFoundException;

public interface ModeScene {
    void initializeRoot() throws FileNotFoundException, InterruptedException;
    void updateOutputText(TextFlow textflow, String... addendums);
    void rollCycle() throws InterruptedException, FileNotFoundException;
}
