package com.gdjfx.app;

// A FocusableGroup can only contain Focusable nodes -- this "focus" is different from the native focus property from JavaFX.
public interface Focusable {
    void handleFocus(boolean isFocused);
}
