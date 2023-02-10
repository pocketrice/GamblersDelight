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
import static com.gdjfx.app.CSSManager.generateColorTransition;

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
    static Color[] palette;
    static Pane root;
    static Timeline digitBgColorTransition, btnBgColorTransition, btnTextColorTransition, digitColorTransition;
    static Timeline[] colorTransitions;
    static final Font igiari = Font.loadFont("file:src/com/gdjfx/app/assets/igiari.ttf", 12);


    // ** Effective Java #2: use the "builder pattern" if you have lots of constructor parameters. **
    public static class Builder {
        // Required params
        private final int[] bounds;
        private Pane root;

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
        digitBG.setFill(palette[0]);
        value = builder.value;
        increment = builder.increment;

        digit = builder.digit;
        tweakStyle(digit, "-fx-font-size", "30");
        digit.setFill(palette[3]);
        digit.setFont(igiari);

        bounds = builder.bounds;
        root = builder.root;

        btnIncr = builder.btnIncr;
        btnIncr.setFocusTraversable(false);
        btnIncr.setStyle("-fx-background-color: " + stringifyAlphaColor(palette[1]));
        btnIncr.setTextFill(palette[2]);
        btnIncr.setFont(igiari);

        btnDecr = builder.btnDecr;
        btnDecr.setFocusTraversable(false);
        btnDecr.setStyle("-fx-background-color: " + stringifyAlphaColor(palette[1]));
        btnDecr.setTextFill(palette[2]);
        btnDecr.setFont(igiari);

        // Apply palette
        digitBG.setFill(palette[0]);
        tweakStyle(btnIncr, "-fx-background-color", stringifyAlphaColor(palette[1]));
        tweakStyle(btnDecr, "-fx-background-color", stringifyAlphaColor(palette[1]));
        btnIncr.setTextFill(palette[2]);
        btnDecr.setTextFill(palette[2]);
        digit.setFill(palette[3]);

        // Attach color transitions (for use with handleFocus)
        digitBgColorTransition = generateColorTransition(palette[0], palette[0].deriveColor(1,1,1.3,1), List.of("-fx-fill"), 1, digitBG);
        btnBgColorTransition = generateColorTransition(palette[1], palette[1].deriveColor(1,1,1.3,1), List.of("-fx-fill"), 1, btnDecr, btnIncr);
        btnTextColorTransition = generateColorTransition(palette[2], palette[2].deriveColor(1,1,1.3,1), List.of("-fx-text-fill"), 1, btnDecr, btnIncr);
        digitColorTransition = generateColorTransition(palette[3], palette[3].deriveColor(1,1,1.3,1), List.of("-fx-text-fill"), 1, digit);

        colorTransitions = new Timeline[]{digitBgColorTransition, btnBgColorTransition, btnTextColorTransition, digitColorTransition};

        this.getChildren().addAll(digitBG, digit, btnIncr, btnDecr);
    }

    public Button getIncr() {
        return btnIncr;
    }

    public Button getDecr() {
        return btnDecr;
    }

    public Color[] getPalette(){
        return palette;
    }

    public void incrementValue() {
        value = (value + increment > bounds[1]) ? bounds[0] : value + 1;
        digit.setText("" + value);
    }

    public void decrementValue() {
        value = (value - increment < bounds[0]) ? bounds[1] : value - 1;
        digit.setText("" + value);
    }

    public int getValue() {
        return value;
    }

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


    @Override
    public void setPalette(Color color) {
        // Param = main BG color.
        // Colors to calculate: digit, incr/decr bg, incr/decr text
        // {mainBg, btnBg, btnText, digit}
        Color mainBgColor = color;
        Color btnBgColor = color.deriveColor(1, 0.85, 1, 1.4);
        Color btnTextColor = color.deriveColor(1,1.2,0.3,1.3);
        Color digitTextColor = color.deriveColor(1,1,0.2,1.3);

        palette = new Color[]{mainBgColor, btnBgColor, btnTextColor, digitTextColor};
    }

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

        root.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<>() {
            public void handle(KeyEvent ke) {
                if (isFocused) {
                    switch (ke.getCode()) {
                        case UP -> {
                           incrementValue();
                           System.out.println("FIRED");
                        }

                        case DOWN -> {
                           decrementValue();
                           System.out.println("FIRED");
                        }
                    }
                }
            }
        });
    }
}
