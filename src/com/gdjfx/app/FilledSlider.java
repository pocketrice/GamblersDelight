package com.gdjfx.app;

import javafx.scene.Group;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.gdjfx.app.ProgramApplet.scene;
import static com.gdjfx.app.CSSManager.stringifyOpaqueColor;

public class FilledSlider extends Group implements CustomNode, Focusable {
    Slider slider = new Slider();
    Rectangle sliderProgress = new Rectangle();
    Rectangle sliderProgressBorder = new Rectangle();
    Color[] palette;


    public FilledSlider() {
        scene.getStylesheets().add("com/gdjfx/app/main.css");
        slider.setId("filled-slider"); // ** Requires #color-slider .track { -fx-background-color: transparent; } in main.css
        sliderProgress.heightProperty().bind(slider.heightProperty().subtract(8));
        sliderProgress.widthProperty().bind(slider.widthProperty());
        sliderProgress.setFill(Color.web("#969696"));

        // Dynamic slider thumb focus/faint focus colors (#filled-slider .thumb) - credit to https://stackoverflow.com/questions/50552728/dynamically-change-javafx-css-property
        slider.setStyle("-dynamic-focus-color: derive(#969696, -40%)");
        slider.setStyle("-dynamic-faint-focus-color: derive(#969696A0, -40%)");

        sliderProgressBorder.heightProperty().bind(slider.heightProperty().subtract(5));
        sliderProgressBorder.widthProperty().bind(slider.widthProperty().add(3));
        sliderProgressBorder.setFill(Color.web("#969696").deriveColor(1, 1, -40, 1));

        sliderProgress.setArcHeight(15);
        sliderProgress.setArcWidth(10);
        sliderProgressBorder.setArcHeight(15);
        sliderProgressBorder.setArcWidth(10);


        // Update progress/progress border based on default volume value.
        sliderProgress.setStyle(java.lang.String.format("-fx-fill: linear-gradient(to right, #2D819D %d%%, #969696 %d%%);",
                (int) slider.getValue(), (int) slider.getValue()));

        sliderProgressBorder.setStyle(java.lang.String.format("-fx-fill: linear-gradient(to right, derive(#2D819D, -40%%) %d%%, derive(#969696, -40%%) %d%%);",
                (int) slider.getValue(), (int) slider.getValue()));

        // Update progress/progress border on change.
        slider.valueProperty().addListener((observableValue, oldVal, newVal) -> {
            sliderProgress.setStyle(java.lang.String.format("-fx-fill: linear-gradient(to right, #2D819D %d%%, #969696 %d%%);",
                    newVal.intValue(), newVal.intValue()));

            sliderProgressBorder.setStyle(java.lang.String.format("-fx-fill: linear-gradient(to right, derive(#2D819D, -40%%) %d%%, derive(#969696, -40%%) %d%%);",
                    newVal.intValue(), newVal.intValue()));
        });

        this.getChildren().addAll(sliderProgressBorder, sliderProgress, slider);
    }


    public FilledSlider(Color filledColor, Color unfilledColor)  {
        scene.getStylesheets().add("com/gdjfx/app/main.css");
        slider.setId("filled-slider"); // ** Requires #color-slider .track { -fx-background-color: transparent; } in main.css
        sliderProgress.heightProperty().bind(slider.heightProperty().subtract(8));
        sliderProgress.widthProperty().bind(slider.widthProperty());
        sliderProgress.setFill(filledColor);

        // Dynamic slider thumb focus/faint focus colors (#filled-slider .thumb)
        slider.setStyle("-dynamic-focus-color: derive(" + stringifyOpaqueColor(filledColor) + ", -40%)");
        slider.setStyle("-dynamic-faint-focus-color: derive(" + stringifyOpaqueColor(filledColor) + "A0, -40%)");

        sliderProgressBorder.heightProperty().bind(slider.heightProperty().subtract(5));
        sliderProgressBorder.widthProperty().bind(slider.widthProperty().add(3));
        sliderProgressBorder.setFill(filledColor.deriveColor(1, 1, -40, 1));

        sliderProgress.setArcHeight(15);
        sliderProgress.setArcWidth(10);
        sliderProgressBorder.setArcHeight(15);
        sliderProgressBorder.setArcWidth(10);


        // Update progress/progress border based on default volume value.
        sliderProgress.setStyle(java.lang.String.format("-fx-fill: linear-gradient(to right, " + stringifyOpaqueColor(filledColor) + " %d%%, " + stringifyOpaqueColor(unfilledColor) + " %d%%);", // Remove "0x" from start of Color string.
                (int) slider.getValue(), (int) slider.getValue()));

        sliderProgressBorder.setStyle(java.lang.String.format("-fx-fill: linear-gradient(to right, derive(" + stringifyOpaqueColor(filledColor) + ", -40%%) %d%%, derive(" + stringifyOpaqueColor(unfilledColor) + ", -40%%) %d%%);", // Remove "0x" from start of Color string.
                (int) slider.getValue(), (int) slider.getValue()));

        // Update progress/progress border on change.
        slider.valueProperty().addListener((observableValue, oldVal, newVal) -> {
            sliderProgress.setStyle(java.lang.String.format("-fx-fill: linear-gradient(to right, " + stringifyOpaqueColor(filledColor) + " %d%%, " + stringifyOpaqueColor(unfilledColor) + " %d%%);", // Remove "0x" from start of Color string.
                    newVal.intValue(), newVal.intValue()));

            sliderProgressBorder.setStyle(java.lang.String.format("-fx-fill: linear-gradient(to right, derive(" + stringifyOpaqueColor(filledColor) + ", -40%%) %d%%, derive(" + stringifyOpaqueColor(unfilledColor) + ", -40%%) %d%%);", // Remove "0x" from start of Color string.
                    newVal.intValue(), newVal.intValue()));
        });

        this.getChildren().addAll(sliderProgressBorder, sliderProgress, slider);
    }

    @Override
    public void setLayout(double layoutX, double layoutY) { // Call this in place of the general setLayout method.
        slider.setLayoutX(layoutX);
        sliderProgress.setLayoutX(layoutX);
        sliderProgressBorder.setLayoutX(layoutX - 1.6);
        slider.setLayoutY(layoutY);
        sliderProgress.setLayoutY(layoutY + 3.5);
        sliderProgressBorder.setLayoutY(layoutY + 2.2);
    }

    @Override
    public void setPalette(Color color) {
        // Implement later. Currently not used for FilledSlider; I expect it to be used for background colors.
    }

    @Override
    public void handleFocus(boolean isFocused) {
        if (isFocused) {

        }
        else {

        }
    }

}
