package com.gdjfx.app;

import javafx.scene.text.TextFlow;

import java.io.FileNotFoundException;

// All JavaFX-based Gambler's Delight modes implement these essential game-management methods. This is a tweaking/extension of ModeConsole's interface.
public interface ModeScene {
    void initializeRoot() throws FileNotFoundException, InterruptedException;
    void updateOutputText(TextFlow textflow, String... addendums);
    void rollCycle() throws InterruptedException, FileNotFoundException;
}
