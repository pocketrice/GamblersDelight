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
    // Due to how JavaFX Colors toString (#FFFEEE -> "0xfffeeeff"; removes #, 0x added to denote hexa, and alpha is explicitly denoted), it is not optimal for things like setStyle which manually edit the style property tag.
    // This method, along with its alpha counterpart stringifyAlphaColor, returns the color in hexadecimal, more-readable form.
    // @param color - Color object (this means it doesn't have to be already in hexa form -- what you'd have to do if inputting a String!)
    // @return more friendly/readable hexadecimal String w/out alpha suffix
    public static String stringifyOpaqueColor(Color color) {
        return "#" + color.toString().substring(2, color.toString().length()-2);
    }

    // See above desc. for stringifyOpaqueColor.
    // @param color
    // @return more friendly/readable hexadecimal String w/ alpha suffix
    public static String stringifyAlphaColor(Color color) {
        return "#" + color.toString().substring(2);
    }


    // Note: this only returns one lerped value on the entire "gradient continuum" (ooh, fancy!). This is to generify/allow for more use-cases. See generateColorTransition for an implementation of the full gradient.
    // @param startColor
    // @param endColor
    // @param value - must be normalized (0-1)
    // @return lerped color between two colors at given value
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

        * This requirement will be mitigated once the CSSTransition class is done (which can handle attaching property listeners!)
     */

    // With the implementation of allowing several nodes and styles, it's important to make sure all nodes/styles are valid.
    // todo: consolidate all "x.focusedProperty.addListener(x.setRate... etc." here later. Do this after Transitions is properly done.
    // @param startColor
    // @param endColor
    // @param cssProperties - color-only CSS properties to apply lerped color to
    // @param duration - time in seconds for transition
    // @param nodes - any amount of nodes to apply transition to
    // @return reversible smooth color transition
    public static Timeline generateColorTransition(Color startColor, Color endColor, List<String> cssProperties, double duration, Node... nodes) {
        Timeline colorAnim = new Timeline();

        for (double i = 0; i <= 1.0; i += 0.001) {
            double finalI = i; // Copy i to "effectively final" variable to comply for lambda
            colorAnim.getKeyFrames().add(new KeyFrame(Duration.seconds(duration * i), e -> {
                    for (Node n : nodes) {
                        for (String property : cssProperties) {
                            tweakStyle(n, property, stringifyAlphaColor(lerpColor(startColor, endColor, finalI)));
                        }
                    }
            }));
        }

        return colorAnim;
    }

    // Again, make sure all nodes/styles are valid. Only use properties that accept numerics and the given suffix!
    // @param startVal
    // @param endVal
    // @param cssProperties - numeric-only CSS properties to apply lerped value to
    // @param suffix - numeric unit (e.g. px, em, %)
    // @param duration
    // @param nodes
    // @return reversible smooth value transition
    public static Timeline generateNumericRuleTransition(double startVal, double endVal, List<String> cssProperties, String suffix, double duration, Node... nodes) {
        Timeline numAnim = new Timeline();

        for (double i = 0; i <= 1.0; i += 0.001) {
            double finalI = i; // Copy i to "effectively final" variable to comply for lambda
            numAnim.getKeyFrames().add(new KeyFrame(Duration.seconds(duration * i), e -> {
                for (Node n : nodes) {
                    for (String property : cssProperties) {
                        tweakStyle(n, property, truncate(startVal + (endVal * finalI), 3) + suffix);
                    }
                }
            }));
            //System.out.println("Time: " + duration * i + "// Style: " +  cssStyle + ": " +  truncate(startVal + (endVal * finalI), 3) + suffix);
        }

        return numAnim;
    }

    // JavaFX's setStyle method works the same as the 'style' tag property in HTML and overwrites whatever is style is set; thus to add a style a workaround is needed.
    // Do note that any styles added will always be appended with a semicolon (;). This doesn't affect functionality.
    // @param node - node to apply style to
    // @param style - CSS style (both property and value) to add to node's style
    // @return N/A
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
    // @param node
    // @param property - CSS property (no value) to remove from node's style
    // @return N/A
    public static void removeStyle(Node node, String property) {
        String currentStyles = node.getStyle();
        StringBuilder tweakedStyles = new StringBuilder(currentStyles);
        tweakedStyles.replace(currentStyles.indexOf(property), currentStyles.substring(currentStyles.indexOf(property)).indexOf(";") + currentStyles.indexOf(property), "");

        node.setStyle(tweakedStyles.toString());
    }

    // See removeStyle note. This method also will handle situations when the property is not defined yet.
    // This is generally preferred over addStyle for situations where you both add/edit or are not sure. Use addStyle when you know you explciitly will ONLY be adding a style and not applying any edits.
    // @param node
    // @param property - CSS property (no value) of node to tweak
    // @param newVal
    // @return N/A
    public static void tweakStyle (Node node, String property, String newVal) {
        String currentStyles = node.getStyle();
        String tweakedStyles;
        boolean doesNodeContainSeveralProperties = currentStyles.contains(";");

        if (!regexedContains(currentStyles, Pattern.compile(property.replaceAll("-", "\\-")))) {
            addStyle(node, property + ": " + newVal);
            currentStyles = node.getStyle(); // Updates currentStyles after edit -- fixes the [-1, x] bug
        }

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
    // todo: check if css can be validated (use in add/remove/tweakStyle too) -- make this a separate validateCss method!
    // @param node
    // @return N/A
    public static void debugCss(Node node) {
        System.out.println(ANSI_BLUE + "CSS Debug Report");
        System.out.println("=======================================================================\n");
        System.out.println("Node: " + node.toString());
        System.out.println("Styles: " + node.getStyle());
        System.out.println("Classes/IDs: " + node.getStyleClass() + " / " + node.getId());
        System.out.println("Focus traversable? " + ("" + node.isFocusTraversable()).toUpperCase());
        System.out.println("Focused? " + ("" + node.isFocused()).toUpperCase());
        System.out.println("\n=======================================================================\n\n\n\n" + ANSI_RESET);
    }
}
