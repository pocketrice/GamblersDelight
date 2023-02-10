package com.gdjfx.app;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.List;
import java.util.regex.Pattern;

import static com.gdjfx.app.GdSlowScene.regexedContains;
import static com.gdjfx.cli.GdSlowConsole.truncate;
import static com.gdjfx.AnsiCode.*;

public class CSSManager {
    public static String stringifyOpaqueColor(Color color) {
        return "#" + color.toString().substring(2, color.toString().length()-2);
    }

    public static String stringifyAlphaColor(Color color) {
        return "#" + color.toString().substring(2);
    }


    // Note: this only returns one lerped value on the entire "gradient continuum" (ooh, fancy!). This is to generify/allow for more use-cases. See generateColorTransition for an implementation of the full gradient.
    public static Color lerpColor(Color startColor, Color endColor, double value) {
        double[] rgbSc = {startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getOpacity()};
        double[] rgbEc = {endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getOpacity()};
        return new Color(
                rgbSc[0] + (rgbEc[0] - rgbSc[0]) * value,
                rgbSc[1] + (rgbEc[1] - rgbSc[1]) * value,
                rgbSc[2] + (rgbEc[2] - rgbSc[2]) * value,
                rgbSc[3] + (rgbEc[3] - rgbSc[3]) * value);
    }

    /*
    ** Implement event handlers like so:

     node.handlerProperty().addListener((observable, oldVal, isPropertyTrue) -> {
            if (isPropertyTrue) {
                nodeColorTransition.setRate(1);
                nodeColorTransition.play();
            } else {
                nodeColorTransition.setRate(-1);
                nodeColorTransition.play();
            }
        });
     */

    // With the implementation of allowing several nodes and styles, it's important to make sure all nodes/styles are valid.
    public static Timeline generateColorTransition(Color startColor, Color endColor, List<String> cssStyles, double duration, Node... nodes) {
        Timeline colorAnim = new Timeline();

        for (double i = 0; i <= 1.0; i += 0.001) {
            double finalI = i; // Copy i to "effectively final" variable to comply for lambda
            colorAnim.getKeyFrames().add(new KeyFrame(Duration.seconds(duration * i), e -> {
                    for (Node n : nodes) {
                        for (String style : cssStyles) {
                            tweakStyle(n, style, stringifyAlphaColor(lerpColor(startColor, endColor, finalI)));
                        }
                    }
            }));
        }

        return colorAnim;
    }

    // Again, make sure all nodes/styles are valid. Only use properties that accept numerics and the given suffix!
    public static Timeline generateNumericRuleTransition(double startVal, double endVal, List<String> cssStyles, String suffix, double duration, Node... nodes) {
        Timeline numAnim = new Timeline();

        for (double i = 0; i <= 1.0; i += 0.001) {
            double finalI = i; // Copy i to "effectively final" variable to comply for lambda
            numAnim.getKeyFrames().add(new KeyFrame(Duration.seconds(duration * i), e -> {
                for (Node n : nodes) {
                    for (String style : cssStyles) {
                        tweakStyle(n, style, truncate(startVal + (endVal * finalI), 3) + suffix);
                    }
                }
            }));
            //System.out.println("Time: " + duration * i + "// Style: " +  cssStyle + ": " +  truncate(startVal + (endVal * finalI), 3) + suffix);
        }

        return numAnim;
    }

    // JavaFX's setStyle method works the same as the 'style' tag property in HTML and overwrites whatever is style is set; thus to add a style a workaround is needed.
    public static void addStyle(Node node, String style) {
        String currentStyles = node.getStyle();
        if (currentStyles.isBlank()) {
            node.setStyle(style + ";");
        }
        else {
            node.setStyle(currentStyles + style + ";");
        }

    }

    // Note that addStyle takes a 'style' as input (-fx-sample-property: 0px) while removeStyle takes a 'property' as input (-fx-sample-property).
    public static void removeStyle(Node node, String property) {
        String currentStyles = node.getStyle();
        StringBuilder tweakedStyles = new StringBuilder(currentStyles);
        tweakedStyles.replace(currentStyles.indexOf(property), currentStyles.substring(currentStyles.indexOf(property)).indexOf(";") + currentStyles.indexOf(property), "");

        node.setStyle(tweakedStyles.toString());
    }

    // See removeStyle note. This method also will handle situations when the property is not defined yet.
    public static void tweakStyle (Node node, String property, String newVal) {
        String currentStyles = node.getStyle();
        String tweakedStyles;
        boolean doesNodeContainSeveralProperties = currentStyles.contains(";");

        if (!regexedContains(currentStyles, Pattern.compile(property.replaceAll("-", "\\-")))) {
            addStyle(node, property + ": " + newVal);
        }

        // stupid [-1, 105] bug here fix it later please - also I'm bad at regex
        if (doesNodeContainSeveralProperties) {
            StringBuilder sb = new StringBuilder(currentStyles);
            sb.replace(currentStyles.indexOf(property), currentStyles.substring(currentStyles.indexOf(property)).indexOf(";") + currentStyles.indexOf(property), property + ": " + newVal); // Start/end: Index of property, index of first ; after property
            tweakedStyles = sb.toString();
        } else {
            tweakedStyles = property + ": " + newVal + ";";
        }

        node.setStyle(tweakedStyles);
    }

    // CSS checker function -- sometimes JavaFX won't spit out info about whether or not css was valid. Write method that prints all css properties for an object.
    public static void debugCss(Node node) {
        System.out.println(ANSI_BLUE + "CSS Debug Report for node " + node.toString());
        System.out.println("================================================\n\n");
        System.out.println("Styles: " + node.getStyle());
        System.out.println("\n\n================================================\n\n\n\n" + ANSI_RESET);
    }
}
