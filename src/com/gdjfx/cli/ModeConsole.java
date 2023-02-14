package com.gdjfx.cli;

import java.io.FileNotFoundException;

// All console-based Gambler's Delight modes implement these essential game-management methods.
public interface ModeConsole {
    void rollCycle() throws InterruptedException, FileNotFoundException;
    boolean rollInitRound();
    boolean rollDoubleRound();
    boolean rollQuadRound();
}
