package com.gdjfx.app;

import javafx.scene.Group;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.gdjfx.app.CSSManager.*;
import static com.gdjfx.app.ProgramApplet.scene;

// A JavaFX custom node that is like the native Slider, but sleeker, more dynamic, and ACTUALLY not ugly.
// This exists because JavaFX for some reason doesn't fill positive values on the slider. Sigh.
public class FilledSlider extends Group implements CustomNode {
    Slider slider = new Slider();
    Rectangle sliderProgress = new Rectangle();
    Rectangle sliderProgressBorder = new Rectangle();
    Color[] palette;


    public FilledSlider() {
        this(Color.web("#969696"));
    }

    public FilledSlider(Color filledColor)  {
        scene.getStylesheets().add("com/gdjfx/app/main.css");
        setPalette(filledColor);

        slider.setId("filled-slider"); // ** Requires #color-slider .track { -fx-background-color: transparent; } in main.css
        sliderProgress.heightProperty().bind(slider.heightProperty().subtract(8));
        sliderProgress.widthProperty().bind(slider.widthProperty());
        sliderProgress.setFill(filledColor);

        // Dynamic slider thumb faint focus color (#filled-slider .thumb)
        addStyle(slider, "-dynamic-faint-focus-color: " +  stringifyOpaqueColor(palette[4]) + "A0");

        sliderProgressBorder.heightProperty().bind(slider.heightProperty().subtract(5));
        sliderProgressBorder.widthProperty().bind(slider.widthProperty().add(3));
        sliderProgressBorder.setFill(palette[2]);

        sliderProgress.setArcHeight(15);
        sliderProgress.setArcWidth(10);
        sliderProgressBorder.setArcHeight(15);
        sliderProgressBorder.setArcWidth(10);


        // Update progress/progress border based on default volume value.
        addStyle(sliderProgress, String.format("-fx-fill: linear-gradient(to right, " + stringifyOpaqueColor(palette[0]) + " %d%%, " + stringifyOpaqueColor(palette[1]) + " %d%%);",
                (int) slider.getValue(), (int) slider.getValue()));

        addStyle(sliderProgressBorder, String.format("-fx-fill: linear-gradient(to right, " + stringifyOpaqueColor(palette[2]) + " %d%%, " + stringifyOpaqueColor(palette[3]) + " %d%%);",
                (int) slider.getValue(), (int) slider.getValue()));

        // Update progress/progress border on change.
        slider.valueProperty().addListener((observableValue, oldVal, newVal) -> {
            tweakStyle(sliderProgress, "-fx-fill", String.format("linear-gradient(to right, " + stringifyOpaqueColor(palette[0]) + " %d%%, " + stringifyOpaqueColor(palette[1]) + " %d%%);",
                    (int) slider.getValue(), (int) slider.getValue()));

            tweakStyle(sliderProgressBorder, "-fx-fill", String.format("linear-gradient(to right, " + stringifyOpaqueColor(palette[2]) + " %d%%, " + stringifyOpaqueColor(palette[3]) + " %d%%);",
                    (int) slider.getValue(), (int) slider.getValue()));
        });

        this.getChildren().addAll(sliderProgressBorder, sliderProgress, slider);
    }

    // Required by CustomNode. Set the layout of the Group with its components relatively placed properly.
    // @param layoutX - x-position of FilledSlider, oriented around the Slider component itself
    // @param layoutY - y-position of FilledSlider
    // @return N/A
    @Override
    public void setLayout(double layoutX, double layoutY) { // Call this in place of the general setLayout method.
        slider.setLayoutX(layoutX);
        sliderProgress.setLayoutX(layoutX);
        sliderProgressBorder.setLayoutX(layoutX - 1.6);
        slider.setLayoutY(layoutY);
        sliderProgress.setLayoutY(layoutY + 3.5);
        sliderProgressBorder.setLayoutY(layoutY + 2.2);
    }


    // Required by CustomNode. Generates the palette for the node, which is dynamically set based on a main color.
    // @param color - main color. For this node it is the color of the slider progress bar.
    // @return N/A
    @Override
    public void setPalette(Color color) {
        Color filledBgColor = color;
        Color unfilledBgColor = color.deriveColor(1,0.3, 1.3, 1);
        Color filledBorderColor = filledBgColor.deriveColor(1,1,0.6,1);
        Color unfilledBorderColor = unfilledBgColor.deriveColor(1,1,0.6,1);
        Color thumbFocusColor = color.deriveColor(1,0.8,0.7,1);

        palette = new Color[]{filledBgColor, unfilledBgColor, filledBorderColor, unfilledBorderColor, thumbFocusColor};
    }
}
