package com.gdjfx.app;

import javafx.scene.paint.Color;

// All CustomNodes must implement these general methods. Palettes are used to allow for dynamically setting colors. Custom Groups should instead use CustomGroup.
public interface CustomNode {
    void setLayout(double layoutX, double layoutY);
    void setPalette(Color color);
}
