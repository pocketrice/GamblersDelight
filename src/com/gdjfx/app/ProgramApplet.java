package com.gdjfx.app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ProgramApplet extends Application { // javafx app gdjfx.

        public static void main(String[] args) {
                launch(args);
        }

        @Override
        public void start(Stage primaryStage) throws FileNotFoundException {
                Button btn = new Button("Tap Me");
                Image imgPond = new Image(new FileInputStream("src/com/gdjfx/app/assets/textures/pond.jpg"));
                ImageView imgvPond = new ImageView(imgPond);
                imgvPond.setStyle("-fx-padding: 10px");
                imgvPond.setStyle("-fx-border-insets: 5px");
                imgvPond.setStyle("-fx-background-insets: 5px");
                imgvPond.setPreserveRatio(true);
                imgvPond.setFitHeight(250);

                Image imgPixTest = new Image(new FileInputStream("src/com/gdjfx/app/assets/textures/dewott.png")); // Use small res image to test filters (preferred bilinear)??
                ImageView imgvPixTest = new ImageView(imgPixTest);
                imgvPixTest.setPreserveRatio(true);
                imgvPixTest.setFitHeight(100);

                ColorPicker cp = new ColorPicker();
                FlowPane root = new FlowPane();
                root.setPadding(new Insets(10,10,10,10));
                root.getChildren().add(btn);
                root.getChildren().add(cp);
                root.getChildren().add(imgvPond);
                root.getChildren().add(imgvPixTest);
                Scene scene = new Scene(root);
                scene.getStylesheets().add("com/gdjfx/main.css");


                //JMetro jMetro = new JMetro(Style.LIGHT);
                /*jMetro*/primaryStage.setScene(scene);
                primaryStage.getIcons().add(new Image("com/gdjfx/app/assets/textures/pond.jpg"));
                primaryStage.setTitle("GDJFX");
                primaryStage.setHeight(500.0);
                primaryStage.setWidth(650.0);
                primaryStage.show();
        }

        @Override // JavaFX method
        public void stop() {

        }

        @Override // JavaFX method
        public void init() {

        }

        public static ImageView prepareImage(String filepath, double height, boolean shouldPreserveRatio) {
                // Generate ImageView from image, basically prepare the imageview without copy-pasting so many things.
                return new ImageView();
        }

/*

          ==========================================================================================
          actually realistic goals

       ## 3D THINGS ##
          - generate 3d models n' stuff
          - texture on model testing
          - import 3d model test (from blender)
          - actually done 3d model (ENV AND DICE/CARD: FORGET ABOUT MODELS FROM CODE!!!!) (from blender)
          - animations (physics for dice, manual for cards)

       ## CONSOLE -> APP ##
          - UI implementation of console gdjfx
          - do things with cli output (e.g. out.print)
          - competent redesign (jmetro or custom)
          - animation (css anim or transitions or something)

       ## AUDIO ##
          - audio testing
          - advance button? (aa-style crunchiness)

       ## LOGISTICAL STUFF ##
          - title screen
          - UI controls (volume, exit to title, pause)

       ## OH, FAST MODE EXISTS ##
          - scrolling/color-changing background (mkds-style)
          - fast mode ui

         */


        
        /*
        Implement:
        - (METHOD) buildDice (builds the Dice 3d model; uses the # of sides and passed-in textures.)
        - (METHOD) buildCard (builds a card model; uses back color, value).
        - (METHOD) buildX (create separate methods that builds the 3d models for parts -- I can think of dice, cards, and room.)
        - (CLASS) com.gdjfx.Model (if needed, an object that stores the 3d model of an object? For use as priv variable)
        - (CLASS) Card (playing card. Allows for the setting of back color (see A.J. for use-case) and value.) Don't allow for any passing in of textures as they'll just be predefined.
        - (CLASS) CardDeck (deck of cards. Stores # of suits/cards available. Also binds cards to a "group" (might come in handy).
        - (CLASS) Dice (dice. Can set number of sides (find a way to generate a normal 3d prism with N sides), values (perhaps also generify so you can pass in any PNG as the "dots" texture).
        - (CLASS) ImageFilter (if there's no way to apply filter to image, program some way to manually process images?)
        - (CLASS) DisplayFilter (might need a Display class to represent a certain "thing" to apply filter to. Think of it as a piece of tissue paper on top of your computer screen, if you will. Use this to apply pixel effects? (or just see OpenEmu filters for exemplars)

        The plan is static but sleek GUI on side (use SMOOTH ANIMS!!), and center is 3d rendered scene (see pool table from Wii Play). Hit button and the dice is actually thrown, card is placed, etc. Think Idle Dice for music; smooth jazz.
        If time permits, maybe even a little bottom screen with statistics and such (DS-style), and you pick options from there. Use the AA SFX (**button press**), UI style for buttons, or some fancy little "velvet pool table carpet" surface where a triumphant anim plays that flips the card over (think drum roll or something).
        Maybe slap a slightly pixelated filter on it too (I'm thinking DS-era fidelity).

        No 3d model for fast mode; instead use MKDS-style scrolling background. fast mode processes faster / looks different / etc.

         - The basics (bare-bones, console based)
         - apply jmetro
         - mockup
         - Fix Info.plist (get Taskbar icon working)
         - Test out audio controls
         - Test out smooth UI things (anims, fonts, etc. -- CSS)
         - Test out getting 3d models into scene
         - 3d model display + UI things overlay (the technical bits of slapping both together)
         - Lighting (work on environment scene)
         - Try to import self-made models
         - Try to store com.gdjfx.Model objs (whether built-in or have to write myself)
     */
}