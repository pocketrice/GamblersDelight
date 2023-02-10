package com.gdjfx.cli;

import java.io.FileNotFoundException;

public interface ModeConsole {
    void rollCycle() throws InterruptedException, FileNotFoundException;
    boolean rollInitRound();
    boolean rollDoubleRound();
    boolean rollQuadRound();
}
