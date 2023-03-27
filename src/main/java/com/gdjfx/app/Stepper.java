package com.gdjfx.app;

import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.List;

import static com.gdjfx.app.CSSManager.*;

// A custom JavaFX node for more visual, keyboard-friendly digit inputs.
public class Stepper extends Group implements CustomNode, Focusable {
    private final Rectangle digitBG;
    private final int increment;
    private final Text digit;
    private final int[] bounds;
    private final Button btnIncr, btnDecr;
    private final Color paletteColor;

    // Modified var -- not final due to modification outside of Builder
    private int value;

    // Internal var -- no need for Builder to build this.
    private Color[] palette;
    private Timeline[] colorTransitions;
    static Pane root;
    static final Font igiari = Font.loadFont("file:src/main/java/com/gdjfx/app/assets/igiari.ttf", 12);
    //static final AudioClip sfxDollop = new AudioClip(new File("src/main/java/com/gdjfx/app/assets/audio/sfx/dollop.wav").toURI().toString());


    final EventHandler<KeyEvent> stepperInputHandler = ke -> {
        switch (ke.getCode()) {
            case UP -> {
                incrementValue();
                //playVolumedAudio(sfxDollop, sfxVolume); // Garbage fix -- replace later
            }

            case DOWN -> {
                decrementValue();
                //playVolumedAudio(sfxDollop, sfxVolume);
            }
        }
    };


    // ** Effective Java #2: use the "builder pattern" if you have lots of constructor parameters. **
    public static class Builder {
        // Required params
        private final int[] bounds;
        private final Pane root;

        // Optional params - initialized to defaults
        private Rectangle digitBG = new Rectangle(30, 70);
        private int value = 0;
        private int increment = 1;
        private Button btnIncr = new Button("+");
        private Button btnDecr = new Button("-");
        private Color paletteColor = Color.web("#9e8fb5");

        // Non-settable variables
        private Text digit = new Text("" + value);


        // Required params constructor
        public Builder(int[] bounds, Pane root) {
            this.bounds = bounds;
            this.root = root;
        }

        // Opt'l params constructors
        public Builder digitBG(double width, double height) {
            digitBG = new Rectangle(width, height);
            return this;
        }

        public Builder value(int v) {
            value = v;
            digit = new Text("" + value); // Update digit value
            return this;
        }

        public Builder increment(int v) {
            increment = v;
            return this;
        }

        public Builder btnIncr(double width, double height, String v) {
            btnIncr = new Button("" + v);
            btnIncr.setPrefSize(width, height);
            return this;
        }

        public Builder btnDecr(double width, double height, String v) {
            btnDecr = new Button("" + v);
            btnDecr.setPrefSize(width, height);
            return this;
        }

        public Builder btnIncr(String v) {
            btnIncr = new Button("" + v);
            btnIncr.setPrefSize(30, 8);
            return this;
        }

        public Builder btnDecr(String v) {
            btnDecr = new Button("" + v);
            btnDecr.setPrefSize(30, 8);
            return this;
        }

        public Builder paletteColor(Color color) {
            paletteColor = color;
            return this;
        }

        // Uses the private Stepper constructor below to return the finished Stepper.
        public Stepper build() {
            return new Stepper(this);
        }
    }

    private Stepper(Builder builder) {
        paletteColor = builder.paletteColor;
        setPalette(paletteColor);

        digitBG = builder.digitBG;
        value = builder.value;
        increment = builder.increment;

        digit = builder.digit;
        addStyle(digit, "-fx-font-size: 30");
        digit.setFont(igiari);

        bounds = builder.bounds;
        root = builder.root;

        btnIncr = builder.btnIncr;
        btnIncr.setFocusTraversable(false);
        btnIncr.setFont(igiari);

        btnDecr = builder.btnDecr;
        btnDecr.setFocusTraversable(false);
        btnDecr.setFont(igiari);

        // Apply palette
        digitBG.setFill(palette[0]);
        addStyle(btnIncr, "-fx-background-color: " + stringifyAlphaColor(palette[1]));
        addStyle(btnDecr, "-fx-background-color: " + stringifyAlphaColor(palette[1]));
        btnIncr.setTextFill(palette[2]);
        btnDecr.setTextFill(palette[2]);
        digit.setFill(palette[3]);

        // Attach color transitions (for use with handleFocus)
        Timeline digitBgColorTransition = generateColorTransition(palette[0], palette[0].deriveColor(1, 0.7, 1.5, 0.8), List.of("-fx-fill"), 0.4, digitBG);
        Timeline btnBgColorTransition = generateColorTransition(palette[1], palette[1].deriveColor(1, 0.8, 1.1, 1.2), List.of("-fx-background-color"), 0.4, btnDecr, btnIncr);
        Timeline btnTextColorTransition = generateColorTransition(palette[2], palette[2].deriveColor(1, 0.9, 1.3, 0.8), List.of("-fx-text-fill"), 0.4, btnDecr, btnIncr);
        Timeline digitColorTransition = generateColorTransition(palette[3], palette[3].deriveColor(1, 2, 2.5, 1), List.of("-fx-fill"), 0.4, digit);

        colorTransitions = new Timeline[]{digitBgColorTransition, btnBgColorTransition, btnTextColorTransition, digitColorTransition};

        this.getChildren().addAll(digitBG, digit, btnIncr, btnDecr);
    }


    // Returns increment button, intended for applying listeners and the like.
    // @param N/A
    // @return increment button
    public Button getIncr() {
        return btnIncr;
    }

    // See getIncr description.
    // @param N/A
    // @return decrement button
    public Button getDecr() {
        return btnDecr;
    }

    // Returns the generated color palette for the stepper.
    // @param N/A
    // @return color palette
    public Color[] getPalette(){
        return palette;
    }


    // Increment the stepper's value, wrapping on bounds if needed.
    // @param N/A
    // @return N/A
    public void incrementValue() {
        value = (value + increment > bounds[1]) ? bounds[0] : value + 1;
        digit.setText("" + value);
    }

    // See incrementValue description.
    // @param N/A
    // @return N/A
    public void decrementValue() {
        value = (value - increment < bounds[0]) ? bounds[1] : value - 1;
        digit.setText("" + value);
    }


    // Returns stepper value.
    // @param N/A
    // @return stepper value
    public int getValue() {
        return value;
    }


    // Required by CustomNode. Set the layout of the group with its components relatively placed properly.
    // @param layoutX - x-position of Stepper, oriented around the increment button
    // @param layoutY - y-position of Stepper
    // @return N/A
    @Override
    public void setLayout(double layoutX, double layoutY) {
        btnIncr.setLayoutX(layoutX);
        btnDecr.setLayoutX(layoutX);
        digitBG.setLayoutX(layoutX);
        digit.setLayoutX(layoutX + 10);

        btnIncr.setLayoutY(layoutY);
        btnDecr.setLayoutY(layoutY + 60);
        digitBG.setLayoutY(layoutY + 7);
        digit.setLayoutY(layoutY + 53);
    }


    // Required by CustomNode. Generates the palette for the node, which is dynamically set based on a main color.
    // @param color - main color. For this node it is the color of the slider progress bar.
    // @return N/A
    @Override
    public void setPalette(Color color) {
        Color mainBgColor = color;
        Color btnBgColor = color.deriveColor(1, 0.85, 1, 1.4);
        Color btnTextColor = color.deriveColor(1,1.2,0.3,1.3);
        Color digitTextColor = color.deriveColor(1,1,0.2,1.3);

        palette = new Color[]{mainBgColor, btnBgColor, btnTextColor, digitTextColor};
    }


    // Required by Focusable. Updates the node on Focusable focus, preferably with clear visual distinction.
    // @param isFocused - is the node Focusable focused (pseudofocused)?
    // @return N/A
    @Override
    public void handleFocus(boolean isFocused) {
        for (Timeline transition : colorTransitions) {
            if (isFocused) {
                transition.setRate(1);
            }
            else {
                transition.setRate(-1);
            }

            transition.play();
        }

        if (isFocused) {
            root.addEventFilter(KeyEvent.KEY_PRESSED, stepperInputHandler);
        }
        else {
            root.removeEventFilter(KeyEvent.KEY_PRESSED, stepperInputHandler);
        }
    }
}
