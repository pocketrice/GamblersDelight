package com.gdjfx.app;

// Custom variants of JavaFX Group (e.g. FocusableGroup) implement a more general required method for CustomNodes (setLayout).
public interface CustomGroup {
    void setLayout(double layoutX, double layoutY);
}
