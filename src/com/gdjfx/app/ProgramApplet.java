package com.gdjfx.app;

import eu.iamgio.animated.Animated;
import eu.iamgio.animated.property.PropertyWrapper;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ProgramApplet extends Application { // javafx app gdjfx.
    static Stage stage;
    static Pane root;
    Scene scene;
    static int activeBtnIndex = -1; // Which button has been selected (via KB)?

    public static void main(String[] args) {
        launch(args);
    }

    // JavaFX method to start the application. Overwritten to setup/customize program.
    // @param primaryStage - JFX required element
    // @return N/A
    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        stage = primaryStage;

        if (!Platform.isSupported(ConditionalFeature.SCENE3D)) { // JFX Scene3D not supported
            System.err.println("Compatibility error: JavaFX Scene3D not supported by your platform.");
            System.exit(-1);
        }

        initializeRoot();

        stage.setScene(scene);
        stage.setTitle("Gambler's Delight");
        stage.setHeight(500);
        stage.setWidth(750);
        stage.setResizable(false);
        stage.show();
    }

    // Initialize the root pane for the default/title screen.
    // @param N/A
    // @return N/A
    public void initializeRoot() throws FileNotFoundException {
        boolean isStaticAnim = true;

        Font suburga = Font.loadFont("file:src/com/gdjfx/app/assets/suburga.otf", 20);
        Font attorneyButtons = Font.loadFont("file:src/com/gdjfx/app/assets/attorneybuttons.ttf", 20);
        Font igiari = Font.loadFont("file:src/com/gdjfx/app/assets/igiari.ttf", 18);
        Image imgFastMode = retrieveImage("src/com/gdjfx/app/assets/fastmode.png");
        Image imgSlowMode = retrieveImage("src/com/gdjfx/app/assets/slowmode.png");
        Image[] imgStatic = retrieveImagesFromRoot("src/com/gdjfx/app/assets/", "static1.png", "static2.png", "static3.png", "static4.png");

        ImageView imgvDest = buildImageView(imgStatic[0], 400, 0, true);

        Rectangle marquee = new Rectangle(0, 5,1000,50);
        marquee.setFill(Color.valueOf("#363533B0"));
        Text marqueeText = new Text("Gamble responsibly! Runs through 10x cycles nice and slowly, with manual controls available. Great for learning the rules behind this mean green gambling machine!");
        marqueeText.setFont(igiari);
        marqueeText.setFill(Color.valueOf("#dfe1db90"));
        Animated<Double> marqueeAnimator = new Animated<>(marqueeText, PropertyWrapper.of(marqueeText.layoutXProperty()).custom(settings -> settings.withDuration(Duration.seconds(30))));

        Button btnSlowMode = new Button("Slow Mode");

        Timeline staticAnim = new Timeline(
                new KeyFrame(Duration.ZERO, e -> imgvDest.setImage(imgStatic[0])),
                new KeyFrame(Duration.seconds(0.08), e -> imgvDest.setImage(imgStatic[1])),
                new KeyFrame(Duration.seconds(0.16), e -> imgvDest.setImage(imgStatic[2])),
                new KeyFrame(Duration.seconds(0.24), e -> imgvDest.setImage(imgStatic[3])));
        staticAnim.setCycleCount(Animation.INDEFINITE);
        staticAnim.playFromStart();

        btnSlowMode.setPrefHeight(50);
        btnSlowMode.setPrefWidth(230);
        btnSlowMode.setFont(suburga);

        Button btnFastMode = new Button("Fast Mode");
        btnFastMode.setFont(suburga);
        btnFastMode.setPrefHeight(50);
        btnFastMode.setPrefWidth(230);

        btnSlowMode.setOnAction(actionEvent -> {
            staticAnim.stop();
            imgvDest.setImage(imgSlowMode);
            try {
                playSlowModeBtnAnim(btnSlowMode);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            btnFastMode.getStyleClass().remove("active");

        });

        btnFastMode.setOnAction(actionEvent -> {
            staticAnim.stop();
            imgvDest.setImage(imgFastMode);
            activeBtnIndex = -1;
            try {
                playFastModeBtnAnim(btnFastMode);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            btnSlowMode.getStyleClass().remove("active");
        });



        root = new Pane();
        root.setBackground(new Background(new BackgroundImage(retrieveImage("src/com/gdjfx/app/assets/rainpark.jpg"), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        root.getChildren().add(imgvDest);
        setLayout(imgvDest, 0, 40);
        root.getChildren().add(btnSlowMode);
        setLayout(btnSlowMode, 420,170);
        root.getChildren().add(btnFastMode);
        setLayout(btnFastMode,420,270);
        root.getChildren().add(marquee);
        root.getChildren().add(marqueeText);
        root.getChildren().add(marqueeAnimator);
        setLayout(marqueeText,750,35);

        scene = new Scene(root /*new GdSlowScene().initializeRoot()*/, 1024, 768);
        scene.getStylesheets().add("com/gdjfx/app/main.css");
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.UP) {
                    activeBtnIndex = (activeBtnIndex + 1 > 1) ? 0 : activeBtnIndex + 1; // todo: replace "1" in ternary with root.buttonCount or smth like that
                    updateSelections(btnSlowMode, btnFastMode, imgvDest, staticAnim, new Image[]{imgSlowMode, imgFastMode}, marqueeText, marqueeAnimator);
                }

                if (ke.getCode() == KeyCode.DOWN) {
                    activeBtnIndex = (activeBtnIndex - 1 < 0) ? 1 : activeBtnIndex - 1; // todo: replace "1" in ternary with root.buttonCount or smth like that
                    updateSelections(btnSlowMode, btnFastMode, imgvDest, staticAnim, new Image[]{imgSlowMode, imgFastMode}, marqueeText, marqueeAnimator);
                }

                if (ke.getCode() == KeyCode.ESCAPE || ke.getCode() == KeyCode.Z) {
                    activeBtnIndex = -1;
                    updateSelections(btnSlowMode, btnFastMode, imgvDest, staticAnim, new Image[]{imgSlowMode, imgFastMode}, marqueeText, marqueeAnimator);
                }

                if (ke.getCode() == KeyCode.X || ke.getCode() == KeyCode.ENTER) {
                    if (activeBtnIndex == 0) {
                        btnSlowMode.fire();
                    }
                    else if (activeBtnIndex == 1) {
                        btnFastMode.fire();
                    }
                }
            }
        });
    }

    // JavaFX method. Overwritten for debug line.
    // @param N/A
    // @return N/A
    @Override
    public void stop() {
        System.out.println("Applet stopped.");
    }


    // JavaFX method. Overwritten for debug line.
    // @param N/A
    // @return N/A
    @Override
    public void init() {
        System.out.println("Applet initialized.");
    }

    // Returns a prepared image given a filepath (more readable/shorter implementation).
    // @param filepath - path from content root
    // @return prepared image
    public static Image retrieveImage(String filepath) throws FileNotFoundException {
        return new Image(new FileInputStream(filepath));
    }

    // Returns prepared images given a file root and filepaths from that root.
    // @param root - path to content root
    // @param rootpaths - paths from content root
    // @return prepared images
    public static Image[] retrieveImagesFromRoot (String root, String... rootpaths) {
        List<Image> images = new ArrayList<>();

        for (String rpath : rootpaths) {
            try {
                images.add(retrieveImage(root + rpath));
            }
            catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        return images.toArray(new Image[0]);
    }



    // Returns prepared images given filepaths.
    // @param filepaths - paths from content root to image. Doesn't have to be all from same root.
    // @return prepared images
    public static Image[] retrieveImages(String... filepaths) {
        List<Image> images = new ArrayList<>();

        for (String fpath : filepaths) {
            try {
                images.add(retrieveImage(fpath));
            }
            catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        return images.toArray(new Image[0]);
    }


    // Builds an ImageView with some useful settings already set-up.
    // @param image
    // @param height - height of ImageView. Prioritized if using preserve ratio.
    // @param width - width of ImageView
    // @param shouldPreserveRatio - should image ratio be retained? Prioritizes height for setup.
    public static ImageView buildImageView(Image image, double height, double width, boolean shouldPreserveRatio) {
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);
        imageView.setPreserveRatio(shouldPreserveRatio);

        return imageView;
    }


    // Helper method to set up both layoutX and layoutY for a Node. JavaFX for some reason doesn't have something like this.
    // @param node
    // @param xPos - layoutX value
    // @param yPos - layoutY value
    public static void setLayout(Node node, double xPos, double yPos) {
        node.setLayoutX(xPos);
        node.setLayoutY(yPos);
    }

    // Specific animation for use by the Slow Mode button. Plays a flashing effect and swaps to GdSlowScene.
    // @param btn - active button
    // @return N/A
    public void playSlowModeBtnAnim(Button btn) throws InterruptedException {
        GdSlowScene smScene = new GdSlowScene();
        Timeline selectedBtnAnim = new Timeline(
                new KeyFrame(Duration.ZERO, e -> btn.getStyleClass().add("selected0")),
                new KeyFrame(Duration.ZERO, e -> btn.getStyleClass().remove("selected1")),
                new KeyFrame(Duration.seconds(0.15), e -> btn.getStyleClass().add("selected1")),
                new KeyFrame(Duration.seconds(0.15), e -> btn.getStyleClass().remove("selected0")),
                new KeyFrame(Duration.seconds(0.3), e -> btn.getStyleClass().add("selected0")),
                new KeyFrame(Duration.seconds(0.3), e -> btn.getStyleClass().remove("selected1")),
                new KeyFrame(Duration.seconds(0.45), e -> btn.getStyleClass().add("selected1")),
                new KeyFrame(Duration.seconds(0.45), e -> btn.getStyleClass().remove("selected0")),
                new KeyFrame(Duration.seconds(0.6), e -> btn.getStyleClass().add("selected0")),
                new KeyFrame(Duration.seconds(0.6), e -> btn.getStyleClass().remove("selected1")),
                new KeyFrame(Duration.seconds(0.75), e -> btn.getStyleClass().add("selected1")),
                new KeyFrame(Duration.seconds(0.75), e -> btn.getStyleClass().remove("selected0")),
                new KeyFrame(Duration.seconds(0.9), e -> btn.getStyleClass().add("selected0")),
                new KeyFrame(Duration.seconds(0.9), e -> btn.getStyleClass().remove("selected1")),
                new KeyFrame(Duration.seconds(1.05), e -> btn.getStyleClass().add("selected1")),
                new KeyFrame(Duration.seconds(1.05), e -> btn.getStyleClass().remove("selected0")),
                new KeyFrame(Duration.seconds(1.2), e -> btn.getStyleClass().add("selected0")),
                new KeyFrame(Duration.seconds(1.2), e -> btn.getStyleClass().remove("selected1")),
                new KeyFrame(Duration.seconds(1.35), e -> btn.getStyleClass().add("selected1")),
                new KeyFrame(Duration.seconds(1.35), e -> btn.getStyleClass().remove("selected0")),
                new KeyFrame(Duration.seconds(1.6), e -> {
                    try {
                        smScene.initializeRoot();
                        changeRoot(smScene.root);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }));

        btn.getStyleClass().remove("active");
        selectedBtnAnim.play();
    }


    // Specific animation for use by the Fast Mode button. Plays a flashing effect and swaps to GdFastScene.
    // @param btn - active button
    // @return N/A
    public void playFastModeBtnAnim(Button btn) throws InterruptedException {
        GdFastScene fmScene = new GdFastScene();
        Timeline selectedBtnAnim = new Timeline(
                new KeyFrame(Duration.ZERO, e -> btn.getStyleClass().add("selected0")),
                new KeyFrame(Duration.ZERO, e -> btn.getStyleClass().remove("selected1")),
                new KeyFrame(Duration.seconds(0.15), e -> btn.getStyleClass().add("selected1")),
                new KeyFrame(Duration.seconds(0.15), e -> btn.getStyleClass().remove("selected0")),
                new KeyFrame(Duration.seconds(0.3), e -> btn.getStyleClass().add("selected0")),
                new KeyFrame(Duration.seconds(0.3), e -> btn.getStyleClass().remove("selected1")),
                new KeyFrame(Duration.seconds(0.45), e -> btn.getStyleClass().add("selected1")),
                new KeyFrame(Duration.seconds(0.45), e -> btn.getStyleClass().remove("selected0")),
                new KeyFrame(Duration.seconds(0.6), e -> btn.getStyleClass().add("selected0")),
                new KeyFrame(Duration.seconds(0.6), e -> btn.getStyleClass().remove("selected1")),
                new KeyFrame(Duration.seconds(0.75), e -> btn.getStyleClass().add("selected1")),
                new KeyFrame(Duration.seconds(0.75), e -> btn.getStyleClass().remove("selected0")),
                new KeyFrame(Duration.seconds(0.9), e -> btn.getStyleClass().add("selected0")),
                new KeyFrame(Duration.seconds(0.9), e -> btn.getStyleClass().remove("selected1")),
                new KeyFrame(Duration.seconds(1.05), e -> btn.getStyleClass().add("selected1")),
                new KeyFrame(Duration.seconds(1.05), e -> btn.getStyleClass().remove("selected0")),
                new KeyFrame(Duration.seconds(1.2), e -> btn.getStyleClass().add("selected0")),
                new KeyFrame(Duration.seconds(1.2), e -> btn.getStyleClass().remove("selected1")),
                new KeyFrame(Duration.seconds(1.35), e -> btn.getStyleClass().add("selected1")),
                new KeyFrame(Duration.seconds(1.35), e -> btn.getStyleClass().remove("selected0")),
                new KeyFrame(Duration.seconds(1.6), e -> {
                    try {
                        fmScene.initializeRoot();
                        changeRoot(fmScene.root);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }));

        btn.getStyleClass().remove("active");
        selectedBtnAnim.play();
    }


    // Updates UI according to which mode button is selected (not active). This includes the marquee, image display, and button colors. Possible to drastically cut down on # of parameters.
    // @param btn1 - slow mode button
    // @param btn2 - fast mode button
    // @param imgv - image display
    // @param unactiveAnim - animation timeline for when no button is selected (TV static)
    // @param activeImg - images representing either mode
    // @param marquee - top-of-screen marquee text
    // @param marqueeAnimator - Animated object handling position transitions for marquee
    public static void updateSelections(Button btn1, Button btn2, ImageView imgv, Timeline unactiveAnim, Image[] activeImg, Text marquee, Animated<Double> marqueeAnimator) { // Hardcoded; to generify allow for any # of activeBtnIndex & any # of buttons. Perhaps fix
        Timeline marqueeAnim = new Timeline(
                new KeyFrame(Duration.ZERO, e -> marquee.setLayoutX(-1700)),
                new KeyFrame(Duration.ZERO, e -> marqueeAnimator.setActive(false)),
                new KeyFrame(Duration.seconds(30), e -> marquee.setLayoutX(700)),
                new KeyFrame(Duration.seconds(30), e ->  marqueeAnimator.setActive(true)));
        marqueeAnim.setCycleCount(Animation.INDEFINITE);

        switch (activeBtnIndex) {
            case -1 -> {
                btn1.getStyleClass().remove("active");
                btn2.getStyleClass().remove("active");

                unactiveAnim.playFromStart();
                marquee.setVisible(false);
            }

            case 0 -> {
                marquee.setVisible(true);
                btn1.getStyleClass().add("active");
                btn2.getStyleClass().remove("active");
                marquee.setText("Gamble responsibly! Runs through 10x cycles nice and slowly, with manual controls available. Great for learning the rules behind this mean green gambling machine!");
                unactiveAnim.stop();
                imgv.setImage(activeImg[0]);
                marqueeAnim.play();
            }

            case 1 -> {
                marquee.setVisible(true);
                btn1.getStyleClass().remove("active");
                btn2.getStyleClass().add("active");
                marquee.setText("Gamble your heart out! Runs a bulk number of cycles providing quick statistics. Useful for probability analysis. Trial number defaults to 5000x, but it may freely be set.");
                unactiveAnim.stop();
                imgv.setImage(activeImg[1]);
                marqueeAnim.play();
            }
        }
    }

    // Changes the root (pane) of the applet.
    // @param pane - pane to be switched to
    // @return N/A
    public static void changeRoot(Parent pane) {
        stage.getScene().setRoot(pane);
    }
}