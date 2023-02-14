// Lucas Xie - P5 AP CSA - 1/26/23 - GDJFX

package com.gdjfx.app;

import com.gdjfx.Card;
import com.gdjfx.Dice;
import com.gdjfx.cli.GdSlowConsole;
import eu.iamgio.animated.Animated;
import eu.iamgio.animated.AnimatedMulti;
import eu.iamgio.animated.Curve;
import eu.iamgio.animated.property.PropertyWrapper;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import org.kordamp.ikonli.materialdesign2.MaterialDesignR;
import org.kordamp.ikonli.materialdesign2.MaterialDesignS;
import org.kordamp.ikonli.materialdesign2.MaterialDesignT;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.gdjfx.app.ProgramApplet.*;
import static com.gdjfx.app.CSSManager.*;

public class GdSlowScene extends GdSlowConsole implements ModeScene {
    Pane root;
    Group ynPrompt, betPrompt, promptBox, pauseGroup;
    Rectangle obfPanel;
    TextFlow outputText;
    Set<Node> roundAssets = new HashSet<>();
    boolean isPaused = false, isAttemptingQuadRound = false, isGameStarted = false, isPrompted = false, isStartLoading = false, isSceneLoaded = false;

    Font suburga = Font.loadFont("file:src/com/gdjfx/app/assets/suburga.otf", 20);
    Font attorneyButtons = Font.loadFont("file:src/com/gdjfx/app/assets/attorneybuttons.ttf", 20);
    Font igiari = Font.loadFont("file:src/com/gdjfx/app/assets/igiari.ttf", 12);

    static double bgmVolume = 0, sfxVolume = 0.5; // <- Default volume values.
    Media bgmConfrontModerato = new Media(new File("src/com/gdjfx/app/assets/audio/bgm/confrontation_moderato.mp3").toURI().toString());
    MediaPlayer bgmPlayer = buildAudio(bgmConfrontModerato, bgmVolume, MediaPlayer.INDEFINITE);
    AudioClip sfxNewEvidence = new AudioClip(new File("src/com/gdjfx/app/assets/audio/sfx/new_evidence.wav").toURI().toString());
    AudioClip sfxSelectShort = new AudioClip(new File("src/com/gdjfx/app/assets/audio/sfx/select_short.wav").toURI().toString());
    AudioClip sfxSelectLong = new AudioClip(new File("src/com/gdjfx/app/assets/audio/sfx/select_long.wav").toURI().toString());
    AudioClip sfxCancel = new AudioClip(new File("src/com/gdjfx/app/assets/audio/sfx/cancel.wav").toURI().toString());
    AudioClip sfxCard = new AudioClip(new File("src/com/gdjfx/app/assets/audio/sfx/card.wav").toURI().toString());
    AudioClip sfxDollop = new AudioClip(new File("src/com/gdjfx/app/assets/audio/sfx/dollop.wav").toURI().toString());
    AudioClip sfxFocus = new AudioClip(new File("src/com/gdjfx/app/assets/audio/sfx/focus.wav").toURI().toString());
    AudioClip sfxDiceroll = new AudioClip(new File("src/com/gdjfx/app/assets/audio/sfx/diceroll.mp3").toURI().toString());
    AudioClip sfxPeepHigh = new AudioClip(new File("src/com/gdjfx/app/assets/audio/sfx/peep_high.wav").toURI().toString());
    AudioClip sfxPeepLow = new AudioClip(new File("src/com/gdjfx/app/assets/audio/sfx/peep_low.wav").toURI().toString());

    Color GD_BLUE = Color.valueOf("#a3caed"), GD_DEW = Color.valueOf("#a0f6be"), GD_CYAN = Color.valueOf("#a3edd7"), GD_PURPLE = Color.valueOf("#aba3ed"), GD_GREEN = Color.valueOf("#aaeda3"), GD_RED = Color.valueOf("#f3746a"), GD_ICEBERG = Color.valueOf("#cff0f3"), GD_CRIMSON = Color.valueOf("#f7aaba"), GD_YELLOW = Color.valueOf("#f7e8aa");
    Map<String, Color> GD_PRESETS = new HashMap<>();



    // Credit to https://stackoverflow.com/questions/46369046/how-to-wait-for-user-input-on-javafx-application-thread-without-using-showandwai
    private final Object PAUSE_KEY = new Object();

    private void pause() {
        Platform.enterNestedEventLoop(PAUSE_KEY);
    }

    private void resume() {
        Platform.exitNestedEventLoop(PAUSE_KEY, null);
    }

    // End of credit

    private int currentRound, totalWins, totalLosses, doubleWins, quadWins, initLosses, doubleLosses, quadLosses, tensValueBet = 1, onesValueBet = 0;
    private long balance, netBalance, bet;

    private final List<Card> cardHistory = new ArrayList<>();
    private final List<Integer> diceHistory = new ArrayList<>();
    private final List<Integer> optDiceHistory = new ArrayList<>();


    public GdSlowScene() {
        currentRound = 0;
        bet = 10;
        balance = 100;
        netBalance = 0;
    }



    // Load color presets into designated map.
    // @param N/A
    // @return N/A
    public void loadColorPresets() {
        GD_PRESETS.put("GD_BLUE", GD_BLUE);
        GD_PRESETS.put("GD_DEW", GD_DEW);
        GD_PRESETS.put("GD_CYAN", GD_CYAN);
        GD_PRESETS.put("GD_PURPLE", GD_PURPLE);
        GD_PRESETS.put("GD_GREEN", GD_GREEN);
        GD_PRESETS.put("GD_RED", GD_RED);
        GD_PRESETS.put("GD_ICEBERG", GD_ICEBERG);
        GD_PRESETS.put("GD_CRIMSON", GD_CRIMSON);
        GD_PRESETS.put("GD_YELLOW", GD_YELLOW);
    }


    // Toggles the obfuscation panel (dark overlay for pause screen, startup, etc). Necessary as it is difficult to both animate opacity and hide object's visibility without many lines.
    // @param isVisible - whether to show or hide the panel
    // @return N/A
    public void toggleObfPanel(boolean isVisible) {
        Animated<Double> obfAnimator = new Animated<>(obfPanel, PropertyWrapper.of(obfPanel.opacityProperty())).custom(settings -> settings.withDuration(Duration.seconds(4)));
        root.getChildren().add(obfAnimator);

        Timeline obfHandler = new Timeline(
                new KeyFrame(Duration.seconds(4), e -> {
                    obfPanel.setVisible(false);
                    obfAnimator.setActive(false);
                })
        );
        obfHandler.setCycleCount(1);

        if (isVisible) {
            obfPanel.setOpacity(1);
            obfPanel.setVisible(true);
        }
        else {
            obfPanel.setOpacity(0);
            obfHandler.playFromStart();
        }
    }


    // Initialize the root pane.
    // @param N/A
    // @return N/A
    public void initializeRoot() throws FileNotFoundException {
        isSceneLoaded = true; // Used to catch situations where the scene is unloaded, but Timelines are still running (SFX, flags, etc.)
        loadColorPresets();

        obfPanel = new Rectangle(750, 500, Color.valueOf("#1a171eC0"));
        Button btnStart = new Button();
        btnStart.setGraphic(new FontIcon(MaterialDesignP.PLAY));
        btnStart.setId("btnStart");
        btnStart.setStyle("-fx-background-color: #cff0f3C0");
        btnStart.setPrefHeight(100);
        btnStart.setPrefWidth(300);
        setLayout(btnStart, 210, 200);
        btnStart.setFont(suburga);
        btnStart.setOnAction(actionEvent -> {
            isStartLoading = true;
            playVolumedAudio(sfxNewEvidence, sfxVolume);
            bgmPlayer.play();

            toggleObfPanel(false);
            Timeline activateKeybindsAnim = new Timeline(
                    new KeyFrame(Duration.seconds(4), e -> {
                        isGameStarted = true;
                        root.requestFocus();
                    })
            );
            activateKeybindsAnim.play();

            btnStart.setVisible(false);
            try {
                rollCycle();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        });
        btnStart.pressedProperty().addListener((observable, oldVal, isPressed) -> {
            if (isPressed) {
                btnStart.setStyle("-fx-background-color: derive(#cff0f3C0, -15%)");
            }
            else {
                btnStart.setStyle("-fx-background-color: #cff0f3C0");
            }
        });


        Button btnQuit = new Button("Quit Game");
        btnQuit.setFont(suburga);
        btnQuit.setPrefSize(200, 40);
        btnQuit.setTextFill(Color.valueOf("#4d3061"));
        addStyle(btnQuit, "-fx-border-color: #707cc5");
        addStyle(btnQuit, "-fx-border-radius: 5px");
        addStyle(btnQuit, "-fx-border-style: solid");
        addStyle(btnQuit, "-fx-background-color: #b2b8daD0");
        addStyle(btnQuit, "-fx-border-width: 0px");
        setLayout(btnQuit, 250, 290);
        btnQuit.setVisible(false);
        btnQuit.setOnAction(actionEvent -> {
            playVolumedAudio(sfxSelectLong, sfxVolume);
            bgmPlayer.stop();
            isSceneLoaded = false;
            changeRoot(ProgramApplet.root);
            resetMarquee();
            activeBtnIndex = -1;
            updateSelections();
        });
        Timeline btnQuitColorTransition = generateColorTransition(Color.web("#b2b8daD0"), Color.web("#afc5ffC0"), List.of("-fx-background-color"), 0.3, btnQuit);
        Timeline btnQuitTextColorTransition = generateColorTransition(Color.web("#4d3061"), Color.web("#303a79"), List.of("-fx-text-fill"), 0.3, btnQuit);
        Timeline btnQuitBorderTransition = generateNumericRuleTransition(0, 2, List.of("-fx-border-width"), "px", 0.2, btnQuit);

        btnQuit.pressedProperty().addListener((observable, oldVal, isPressed) -> {
            if (isPressed) {
                btnQuit.setStyle("-fx-background-color: derive(#b2b8daD0, -20%)");
            }
            else {
                btnQuit.setStyle("-fx-background-color: #b2b8daD0");
            }
        });
        btnQuit.focusedProperty().addListener((observable, oldVal, isFocused) -> {
            if (isFocused) {
                btnQuitColorTransition.setRate(1);
                btnQuitTextColorTransition.setRate(1);
                btnQuitBorderTransition.setRate(1);

            } else {
                btnQuitColorTransition.setRate(-1);
                btnQuitTextColorTransition.setRate(-1);
                btnQuitBorderTransition.setRate(-1);
            }
            btnQuitColorTransition.play();
            btnQuitTextColorTransition.play();
            btnQuitBorderTransition.play();
        });

        Text bgmSlCaption = new Text("Background Music");
        bgmSlCaption.setFont(igiari);
        bgmSlCaption.setStyle("-fx-font-size:14");
        bgmSlCaption.setFill(Color.valueOf("#c2d9d6"));

        FilledSlider bgmSlider = new FilledSlider(Color.web("#709e98"));
        bgmSlider.slider.setValue((bgmVolume * 100) / 0.15);
        Text bgmSlVolNum = new Text((int) bgmSlider.slider.getValue() + "%");
        bgmSlider.slider.valueProperty().addListener((obs, oldValue, newValue) -> {
            bgmSlVolNum.setText((int) bgmSlider.slider.getValue() + "%");
            bgmVolume = 0.15 * (bgmSlider.slider.getValue() / 100);
            bgmPlayer.setVolume(bgmVolume); // Max volume is 0.15; thus rescale value around that max.
        });
        bgmSlider.slider.setOnMouseReleased(e -> playVolumedAudio(sfxPeepHigh, bgmVolume));

        bgmSlVolNum.setFont(igiari);
        bgmSlVolNum.setFill(Color.valueOf("#92b0ac"));
        FontIcon bgmSlIcon = new FontIcon(MaterialDesignS.SPEAKER_WIRELESS);
        bgmSlIcon.setIconSize(20);
        bgmSlIcon.setFill(Color.valueOf("#ccf0ea"));
        Group bgmSettings = new Group(bgmSlider, bgmSlCaption, bgmSlIcon, bgmSlVolNum);
        bgmSettings.setVisible(false);
        setLayout(bgmSlCaption, 265, 184);
        setLayout(bgmSlIcon, 240, 186);
        bgmSlider.setLayout(280, 190);
        setLayout(bgmSlVolNum, 400, 188);


        FilledSlider sfxSlider = new FilledSlider(Color.web("#76709e"));
        sfxSlider.slider.setValue((sfxVolume * 100) / 0.8);
        Text sfxSlVolNum = new Text((int) sfxSlider.slider.getValue() + "%");
        sfxSlVolNum.setFont(igiari);
        sfxSlVolNum.setFill(Color.valueOf("#9292b0"));
        sfxSlider.slider.valueProperty().addListener((obs, oldValue, newValue) -> {
            sfxSlVolNum.setText((int) sfxSlider.slider.getValue() + "%");
            sfxVolume = 0.8 * (sfxSlider.slider.getValue() / 100);
        });

        sfxSlider.slider.setOnMouseReleased(e -> {
            playVolumedAudio(sfxPeepLow, sfxVolume); // if check here!!
            System.out.println("Mouse-release SFX played.");
        });

        Text sfxSlCaption = new Text("SFX");
        sfxSlCaption.setFont(igiari);
        sfxSlCaption.setStyle("-fx-font-size:14");
        sfxSlCaption.setFill(Color.valueOf("#c2c5d9"));
        FontIcon sfxSlIcon = new FontIcon(MaterialDesignR.RADIO_TOWER);
        sfxSlIcon.setIconSize(20);
        sfxSlIcon.setFill(Color.valueOf("#d2ccf0"));
        Group sfxSettings = new Group(sfxSlider, sfxSlCaption, sfxSlIcon, sfxSlVolNum);
        sfxSettings.setVisible(false);
        setLayout(sfxSlCaption, 265, 234);
        setLayout(sfxSlIcon, 240, 236);
        sfxSlider.setLayout(280, 240);
        setLayout(sfxSlVolNum, 400, 238);

        ScrollPane outputScroll = new ScrollPane();
        outputText = new TextFlow();
        outputScroll.setContent(outputText);
        outputScroll.setPrefSize(370, 450);
        outputScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        outputScroll.setBackground(buildSolidColorBackground(Color.TRANSPARENT));
        outputScroll.vvalueProperty().bind(outputText.heightProperty()); // Credit to https://stackoverflow.com/questions/13156896/javafx-auto-scroll-down-scrollpane; auto-moves scrollbar when textflow increases


        Text pauseText = new Text("PAUSED");
        pauseText.setFont(suburga);
        pauseText.setStyle("-fx-font-size: 50");
        pauseText.setFill(Color.valueOf("#c2c5d9"));
        setLayout(pauseText, 260, 60);
        Timeline pauseTextAnim = new Timeline(
                new KeyFrame(Duration.ZERO, e -> pauseText.setVisible(true)),
                new KeyFrame(Duration.seconds(0.6), e -> pauseText.setVisible(false)),
                new KeyFrame(Duration.seconds(1.2), e -> pauseText.setVisible(true)));
        pauseTextAnim.setCycleCount(Animation.INDEFINITE);
        pauseTextAnim.playFromStart();
        pauseText.setOpacity(0);

        Group pauseMenu = new Group(pauseText, btnQuit, bgmSettings, sfxSettings);

        // MISPLACED - NOT OPTIMAL
        Button btnSubmitBet = new Button("Submit");
        btnSubmitBet.setFont(igiari);
        btnSubmitBet.setPrefSize(60, 20);
        btnSubmitBet.setStyle("-fx-background-color: #958cadD0");
        btnSubmitBet.setOnAction(actionEvent -> {
            if (isPrompted) {
                playVolumedAudio(sfxCancel, sfxVolume);
                btnSubmitBet.setStyle("-fx-background-color: derive(#958cadD0, -15%)");
                resume();
                isPrompted = false;
            }
        });

        Button btnPause = new Button();
        btnPause.setFocusTraversable(false);
        btnPause.setId("btnPause");
        btnPause.setGraphic(new FontIcon(MaterialDesignP.PAUSE));
        btnPause.setTextFill(GD_ICEBERG);
        btnPause.setOnAction(actionEvent -> {
            playVolumedAudio(sfxDollop, sfxVolume);
            if (!isPaused) {
                bgmPlayer.pause();
                isPaused = true;
                pauseText.setOpacity(1);
                btnQuit.setVisible(true);
                bgmSettings.setVisible(true);
                sfxSettings.setVisible(true);
                btnPause.setGraphic(new FontIcon(MaterialDesignP.PLAY));
                obfPanel.setOpacity(1);
                obfPanel.setVisible(true);

                obfPanel.toFront();
                btnPause.toFront();
                pauseMenu.toFront();
                bgmSlider.slider.requestFocus();
                btnSubmitBet.setFocusTraversable(false);
            }
            else {
                bgmPlayer.play();
                isPaused = false;
                pauseText.setOpacity(0);
                btnQuit.setVisible(false);
                bgmSettings.setVisible(false);
                sfxSettings.setVisible(false);
                btnPause.setGraphic(new FontIcon(MaterialDesignP.PAUSE));
                obfPanel.setVisible(false);
                btnSubmitBet.setFocusTraversable(true);
            }
        });
        btnPause.pressedProperty().addListener((observable, oldVal, isPressed) -> {
            if (isPressed) {
                btnPause.setStyle("-fx-background-color: derive(#958cad70, -15%)");
            }
            else {
                btnPause.setStyle("-fx-background-color: #958cad70");
            }
        });


        pauseGroup = new Group(obfPanel, pauseMenu, btnPause);


        Rectangle outputPanel = new Rectangle(370, 0, 400, 765);
        outputPanel.setFill(Color.valueOf("#2a2537C0"));


        Rectangle promptBoxBG = new Rectangle(300, 300, Color.valueOf("#6a4881A4"));
        Rectangle promptBarBG = new Rectangle(300, 25, Color.valueOf("#4d3061B0"));

        Text promptBarText = new Text("Select your choice");
        promptBarText.setFont(igiari);
        promptBarText.setFill(Color.valueOf("#cecaef"));
        FontIcon promptBarArrow1 = new FontIcon(MaterialDesignT.TRIANGLE);
        promptBarArrow1.setFill(Color.valueOf("#d5e0ff"));
        promptBarArrow1.setRotate(180);
        promptBarArrow1.setIconSize(11);
        playPromptBarArrowAnim(promptBarArrow1);

        FontIcon promptBarArrow2 = new FontIcon(MaterialDesignT.TRIANGLE);
        promptBarArrow2.setFill(Color.valueOf("#d5e0ff"));
        promptBarArrow2.setRotate(180);
        promptBarArrow2.setIconSize(11);
        playPromptBarArrowAnim(promptBarArrow2);


        Button btnYes = new Button("Yes");
        btnYes.setFont(igiari);
        btnYes.setStyle("-fx-font-size: 24");
        btnYes.setStyle("-fx-background-color: #958cadD0");
        btnYes.setPrefHeight(20);
        btnYes.setPrefWidth(90);
        setLayout(btnYes, 430, 350);
        btnYes.setOnAction(actionEvent -> {
            if (isPrompted) {
                btnYes.setStyle("-fx-background-color: derive(#cec4ed, 20%)");
                playVolumedAudio(sfxCancel, sfxVolume);
                isAttemptingQuadRound = true;
                resume();
                isPrompted = false;
            }
        });

        Timeline btnYesColorTransition = generateColorTransition(Color.web("#958cadD0"), Color.web("#cec4edD0"), List.of("-fx-background-color"), 0.7, btnYes);

        btnYes.hoverProperty().addListener((observable, oldVal, isHovered) -> {
            root.requestFocus();
            if (isHovered) {
                btnYesColorTransition.setRate(1);
                btnYesColorTransition.play();
            } else {
                btnYesColorTransition.setRate(-1);
                btnYesColorTransition.play();
            }
        });

        btnYes.focusedProperty().addListener((observable, oldVal, isFocused) -> {
            if (isFocused) {
                btnYesColorTransition.setRate(1);
                btnYesColorTransition.play();
            } else {
                btnYesColorTransition.setRate(-1);
                btnYesColorTransition.play();
            }
        });



        Button btnNo = new Button("No");
        btnNo.setFont(igiari);
        btnNo.setStyle("-fx-font-size: 24");
        btnNo.setStyle("-fx-background-color: #958cadD0");
        btnNo.setPrefHeight(20);
        btnNo.setPrefWidth(90);
        setLayout(btnNo, 560, 350);
        btnNo.setOnAction(actionEvent -> {
            if (isPrompted) {
                playVolumedAudio(sfxCancel, sfxVolume);
                isAttemptingQuadRound = false;
                resume();
                isPrompted = false;
            }
        });

        Timeline btnNoColorTransition = generateColorTransition(Color.web("#958cadD0"), Color.web("#cec4edD0"), List.of("-fx-background-color"), 0.7, btnNo);

        btnNo.hoverProperty().addListener((observable, oldVal, isHovered) -> {
            root.requestFocus();
            if (isHovered) {
                btnNoColorTransition.setRate(1);
                btnNoColorTransition.play();
            } else {
                btnNoColorTransition.setRate(-1);
                btnNoColorTransition.play();
            }
        });

        btnNo.focusedProperty().addListener((observable, oldVal, isFocused) -> {
            if (isFocused) {
                btnNoColorTransition.setRate(1);
                btnNoColorTransition.play();
            } else {
                btnNoColorTransition.setRate(-1);
                btnNoColorTransition.play();
            }
        });


        ynPrompt = new Group(btnYes, btnNo);


        Rectangle tensDigitBG = new Rectangle(30, 70, Color.valueOf("#9e8fb590"));
        Rectangle onesDigitBG = new Rectangle(30, 70, Color.valueOf("#9e8fb590"));

        Text tensDigit = new Text("" + tensValueBet);
        tensDigit.setFont(igiari);
        tensDigit.setStyle("-fx-font-size: 30");
        tensDigit.setFill(Color.valueOf("#392e44"));
        Text onesDigit = new Text("" + onesValueBet);
        onesDigit.setFont(igiari);
        onesDigit.setStyle("-fx-font-size: 30");
        onesDigit.setFill(Color.valueOf("#392e44"));

        Button btnTensIncr = new Button("+");
        btnTensIncr.setFocusTraversable(false);
        btnTensIncr.setFont(igiari);
        btnTensIncr.setPrefSize(30, 8);
        btnTensIncr.setStyle("-fx-background-color: #b5aac6C0");
        btnTensIncr.setOnAction(actionEvent -> {
            if (isPrompted) {
                playVolumedAudio(sfxDollop, sfxVolume);
                tensValueBet = (tensValueBet + 1 > 5) ? 1 : tensValueBet + 1;
                tensDigit.setText("" + tensValueBet);
            }
        });
        btnTensIncr.pressedProperty().addListener((observable, oldVal, isPressed) -> {
            if (isPressed) {
                btnTensIncr.setStyle("-fx-background-color: derive(#b5aac6C0, -15%)");
            }
            else {
                btnTensIncr.setStyle("-fx-background-color: #b5aac6C0");
            }
        });


        Button btnTensDecr = new Button("-");
        btnTensDecr.setFocusTraversable(false);
        btnTensDecr.setFont(igiari);
        btnTensDecr.setPrefSize(30, 8);
        btnTensDecr.setStyle("-fx-background-color: #b5aac6C0");

        btnTensDecr.setOnAction(actionEvent -> {
            if (isPrompted) {
                playVolumedAudio(sfxDollop, sfxVolume);
                tensValueBet = (tensValueBet - 1 < 1) ? 5 : tensValueBet - 1;
                tensDigit.setText("" + tensValueBet);
            }
        });
        btnTensDecr.pressedProperty().addListener((observable, oldVal, isPressed) -> {
            if (isPressed) {
                btnTensDecr.setStyle("-fx-background-color: derive(#b5aac6C0, -15%)");
            }
            else {
                btnTensDecr.setStyle("-fx-background-color: #b5aac6C0");
            }
        });



        Button btnOnesIncr = new Button("+");
        btnOnesIncr.setFocusTraversable(false);
        btnOnesIncr.setFont(igiari);
        btnOnesIncr.setPrefSize(30, 8);
        btnOnesIncr.setStyle("-fx-background-color: #958cadD0");
        btnOnesIncr.setOnAction(actionEvent -> {
            if (isPrompted) {
                playVolumedAudio(sfxFocus, sfxVolume);
            }
        });
        Button btnOnesDecr = new Button("-");
        btnOnesDecr.setFocusTraversable(false);
        btnOnesDecr.setFont(igiari);
        btnOnesDecr.setPrefSize(30, 8);
        btnOnesDecr.setStyle("-fx-background-color: #958cadD0");
        btnOnesDecr.setOnAction(actionEvent -> {
            if (isPrompted) {
                playVolumedAudio(sfxFocus, sfxVolume);
            }
        });


        betPrompt = new Group(tensDigitBG, onesDigitBG, tensDigit, onesDigit, btnTensIncr, btnTensDecr, btnOnesIncr, btnOnesDecr, btnSubmitBet);
        setLayout(tensDigitBG, 470, 325);
        setLayout(onesDigitBG, 510, 325);
        setLayout(tensDigit, 480, 378);
        setLayout(onesDigit, 520, 378);
        setLayout(btnTensIncr, 470, 325);
        setLayout(btnTensDecr, 470, 385);
        setLayout(btnOnesIncr, 510, 325);
        setLayout(btnOnesDecr, 510, 385);
        setLayout(btnSubmitBet, 580, 360);


        promptBox = new Group(promptBoxBG, promptBarBG, promptBarText, promptBarArrow1, promptBarArrow2, ynPrompt, betPrompt);
        setLayout(promptBox, 0, 200);
        setLayout(promptBoxBG, 400, 300);
        setLayout(promptBarBG, 400, 300);
        setLayout(promptBarText, 490, 317);
        setLayout(promptBarArrow1, 440, 317);
        setLayout(promptBarArrow2, 630, 317);
        Animated<Double> pbAnimator = new Animated<>(promptBox, PropertyWrapper.of(promptBox.layoutYProperty())).custom(settings -> settings.withDuration(Duration.seconds(3)).withCurve(Curve.EASE_IN_OUT));

        promptBox.layoutYProperty().addListener((observable, oldVal, isLayoutYChanged) -> { // Reset submit button color (for bet prompt) / focus (for yn prompt) when prompted again
            if (isPrompted) {
                btnSubmitBet.setStyle("-fx-background-color: #958cadD0");
                root.requestFocus();
            }
        });

        Image imgVelvet = retrieveImage("src/com/gdjfx/app/assets/velvettable.png");
        ImageView imgvVelvet = buildImageView(imgVelvet, 340, 500, false);
        DropShadow velvetShadow = new DropShadow(BlurType.GAUSSIAN, Color.valueOf("#262221D0"), 10, 0, 4, 4);
        imgvVelvet.setEffect(velvetShadow);

        root = new Pane();
        root.setBackground(new Background(new BackgroundImage(retrieveImage("src/com/gdjfx/app/assets/fmbackdrop_bl.jpg"), BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        root.getChildren().add(imgvVelvet);
        setLayout(imgvVelvet, -80, 120);
        root.getChildren().add(outputPanel);
        root.getChildren().add(outputScroll);
        setLayout(outputScroll, 380, 0);
        root.getChildren().add(promptBox);
        root.getChildren().add(pbAnimator);
        root.getChildren().add(pauseMenu);
        root.getChildren().add(btnPause);
        root.getChildren().add(obfPanel);
        root.getChildren().add(btnStart);


        root.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<>() {
            int ynIndex = 0;
            public void handle(KeyEvent ke) {
                if (isGameStarted || ke.getCode() == KeyCode.ENTER) {
                    switch (ke.getCode()) {
                        case UP -> { // betPrompt (increment focused digit)
                            if (!isPaused && isPrompted && betPrompt.isVisible() && !outputScroll.isFocused()) {
                                btnTensIncr.fire();
                            }
                            System.out.println("CURRENT FOCUS: " + scene.focusOwnerProperty().get());
                        }

                        case DOWN -> { // betPrompt (decrement focused digit)
                            if (!isPaused && isPrompted && betPrompt.isVisible() && !outputScroll.isFocused()) {
                                btnTensDecr.fire();
                            }
                            System.out.println("CURRENT FOCUS: " + scene.focusOwnerProperty().get());
                        }

                        case LEFT -> { // both prompts (move focus leftward)
                            if (isPaused && bgmSlider.slider.isFocused()) {
                                playVolumedAudio(sfxPeepHigh, bgmVolume);
                            }

                            else if (isPaused && sfxSlider.slider.isFocused()) {
                                playVolumedAudio(sfxPeepLow, sfxVolume);
                            }

                            else if (isPrompted && ynPrompt.isVisible()) {
                                btnYes.requestFocus();
                                ynIndex = 0;
                            }

                        }

                        case RIGHT -> { // both prompts (move focus rightward)
                            if (isPaused && bgmSlider.slider.isFocused()) {
                                playVolumedAudio(sfxPeepHigh, bgmVolume);
                            }

                            else if (isPaused && sfxSlider.slider.isFocused()) {
                                playVolumedAudio(sfxPeepLow, sfxVolume);
                            }

                            else if (isPrompted && ynPrompt.isVisible()) {
                                btnNo.requestFocus();
                                ynIndex = 1;

                            }
                        }

                        case ESCAPE -> btnPause.fire();

                        case Z -> { // ynPrompt - selects no (or focuses no?)
                           root.requestFocus();
                        }

                        case X -> { // ynPrompt (select focused option), betPrompt (auto-move (end auto-move when focused submit) / submit on focused submit)
                            if (!isPaused && isPrompted && ynPrompt.isVisible()) {
                                switch (ynIndex) {
                                    case 0 -> {
                                        btnYes.fire();
                                    }

                                    case 1 -> {
                                        btnNo.fire();
                                    }
                                }
                            }
                            else if (isPaused && btnQuit.isFocused()) btnQuit.fire();
                        }

                        case ENTER -> { // ynPrompt (select focused option), betPrompt (submit)
                            if (!isPaused && isPrompted && betPrompt.isVisible()) {
                                btnSubmitBet.fire();
                            }
                            else if (!isGameStarted && !isStartLoading) {
                                btnStart.fire();
                            }
                            else if (!isPaused && isPrompted && ynPrompt.isVisible()) {
                                switch (ynIndex) {
                                    case 0 -> {
                                        btnYes.fire();
                                    }

                                    case 1 -> {
                                        btnNo.fire();
                                    }
                                }
                            }
                            else if (isPaused && btnQuit.isFocused()) btnQuit.fire();
                        }
                    }
                }
            }
        });
    }


    // Update UI textflow with given strings (JFX UI equivalent of System.out.print).
    // @param textflow - target textflow
    // @param addendums - strings to append (separated as each string only supports one color b/c they are parsed as Text objects)
    // @return N/A
    public void updateOutputText (TextFlow textflow, String... addendums) {
        // An addendum string can be parsed to have colors. This is done by typing {CONST_NAME} or {any hex color} prior to any text.
        for (String addendum : addendums) {
            Color addendumColor = Color.valueOf("#FFFFFF");

            if (bulkContains(addendum, Pattern.compile("\\{.*?}"))) {
                String addendumColorTag = addendum.substring(addendum.indexOf("{")+1, addendum.indexOf("}")); // assumes the addendum won't have any other curly braces
                addendum = addendum.replaceAll("\\{.*?}", "");

                if (addendumColorTag.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) addendumColor = Color.valueOf(addendumColorTag);
                for (String colorPreset : GD_PRESETS.keySet()) {
                    if (addendumColorTag.matches(colorPreset)) addendumColor = GD_PRESETS.get(colorPreset);
                }
            }

            Text addendumText = new Text(addendum);
            addendumText.setFill(addendumColor);
            addendumText.setFont(igiari);
            textflow.getChildren().add(addendumText);
        }
    }

    // Compare a string against several patterns and return true only if all patterns match (strict matching).
    // @param string
    // @param patterns - regex patterns to test
    // @return whether or not string matched ALL patterns
    public static boolean bulkContains(String string, Pattern... patterns) { // <+> APM - too lazy to write one for just one pattern, so here ya go
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(string);

            if (!matcher.find()) {
                return false; // Not in string = false. There is no middle-ground!
            }
        }

        return true; // All patterns were matched.
    }

    // Specific animation for prompt info bar's two "attention-directing" arrows; downward-pointing gesture. Inspired by Ace Attorney.
    // @param promptBarArrow - target arrow
    // @return N/A
    public static void playPromptBarArrowAnim(FontIcon promptBarArrow) {
        Timeline promptBarArrowAnim = new Timeline(
                new KeyFrame(Duration.ZERO, e -> promptBarArrow.setLayoutY(313.2)),
                new KeyFrame(Duration.seconds(0.11), e -> promptBarArrow.setLayoutY(314.2)),
                new KeyFrame(Duration.seconds(0.22), e -> promptBarArrow.setLayoutY(315.2)),
                new KeyFrame(Duration.seconds(0.33), e -> promptBarArrow.setLayoutY(316.2)),
                new KeyFrame(Duration.seconds(0.44), e -> promptBarArrow.setLayoutY(317.2)),
                new KeyFrame(Duration.seconds(0.55), e -> promptBarArrow.setLayoutY(318.2)),
                new KeyFrame(Duration.seconds(0.66), e -> promptBarArrow.setLayoutY(319.2)));

        promptBarArrowAnim.setCycleCount(Animation.INDEFINITE);
        promptBarArrowAnim.play();
    }

    // Non-generic prompting method to ask the user if they want to risk the quad round.
    // @param balance
    // @param bet
    // @return whether or not user chose to try the quad round
    public boolean ynPrompt (double balance, double bet) {
        isPrompted = true;
        updateOutputText(outputText, "{GD_YELLOW}* You currently qualify for a 2x win ", "(" + gdMonetaryParse(balance, false, false, false) + " -> " + gdMonetaryParse((balance + bet * 2), false, false, false) + ")", "{GD_YELLOW}.\nTry for a 4x win by trying to roll a pair ", "(" + gdMonetaryParse(balance, false, false, false) + " -> " + gdMonetaryParse((balance + bet * 4), false, false, false) + ")", "{GD_YELLOW}? \nYou will lose ALL profits if you lose.\n\n");
        ynPrompt.setVisible(true);
        betPrompt.setVisible(false);
        promptBox.setLayoutY(50);
        pause();

        promptBox.setLayoutY(180);
        return isAttemptingQuadRound;
    }

    // Non-generic prompting method to ask the user for a bet to deposit ($10-$50, increments of 10)
    // @param message - message to push to textflow
    // @return bet amount
    public long betPrompt (String message) {
        isPrompted = true;
        updateOutputText(outputText, message);
        betPrompt.setVisible(true);
        ynPrompt.setVisible(false);
        promptBox.setLayoutY(50);
        pause();

        promptBox.setLayoutY(180);
        return tensValueBet * 10L;
    }


    // JFX / Gambler's Delight-specific monetaryParse method that uses color presets in place of ansi colors (which don't work in GUI).
    // @param num
    // @param includeDecimal
    // @param includeExplicitSign
    // @param includeCOlor
    // @return monetary-parsed string
    public static String gdMonetaryParse (double num, boolean includeDecimal, boolean includeExplicitSign, boolean includeColor) { // BUG: for some reason this doesn't work 100% (see 'expected value' on occassions)
        String[] monetaryColors = (includeColor) ? new String[]{"{GD_DEW}", "{GD_BLUE}", "{GD_CRIMSON}"} : new String[]{"","",""};

        String properTruncString;
        if (includeDecimal) {
            String rawTruncString = String.valueOf(truncate(Math.abs(num), 2)); // For non-decimals, this may produce only 1 decimal place (e.g. 120.0)
            properTruncString = (rawTruncString.substring(rawTruncString.indexOf('.') + 1).length() < 2) ? rawTruncString + "0" : rawTruncString; // Adds an extra 0 if necessary to string.
        }
        else {
            properTruncString = String.valueOf(Math.abs((int)num));
        }

        if (includeExplicitSign) {
            return (num > 0) ? monetaryColors[0] + "+$" + properTruncString : ((num == 0) ? "$" + monetaryColors[1] + properTruncString : monetaryColors[2] + "-$" + properTruncString);
        }
        else {
            return (num > 0) ? monetaryColors[0] + "$" + properTruncString : ((num == 0) ? "$" + monetaryColors[1] + properTruncString : monetaryColors[2] + "-$" + properTruncString);
        }
    }

    // JFX / Gambler's Delight-specific fancyDelay method using Ikonli icons in place of chars and \b (which was a little too funky for GUI)
    // @param output - target textflow
    // @param loadMessage
    // @return N/A
    public void gdFancyDelay(TextFlow output, String loadMessage) { // assumes that method is ONLY used for actions needing user input (e.g. roll dice) and then stop.
        Text message = new Text(loadMessage + " ");
        Text loadingIcon = new Text("/");
        message.setFill(GD_CYAN);
        loadingIcon.setFill(GD_CYAN);
        message.setFont(igiari);
        loadingIcon.setFont(igiari);
        output.getChildren().add(message);
        output.getChildren().add(loadingIcon);

        Timeline fancySpinAnim = new Timeline(
                new KeyFrame(Duration.seconds(0.05), e -> loadingIcon.setRotate(20)),
                new KeyFrame(Duration.seconds(0.1), e -> loadingIcon.setRotate(40)),
                new KeyFrame(Duration.seconds(0.15), e -> loadingIcon.setRotate(60)),
                new KeyFrame(Duration.seconds(0.2), e -> loadingIcon.setRotate(80)),
                new KeyFrame(Duration.seconds(0.25), e -> loadingIcon.setRotate(100)),
                new KeyFrame(Duration.seconds(0.3), e -> loadingIcon.setRotate(120)),
                new KeyFrame(Duration.seconds(0.35), e -> loadingIcon.setRotate(140)),
                new KeyFrame(Duration.seconds(0.4), e -> loadingIcon.setRotate(160)),
                new KeyFrame(Duration.seconds(0.45), e -> loadingIcon.setRotate(180)),
                new KeyFrame(Duration.seconds(0.5), e -> loadingIcon.setRotate(200)),
                new KeyFrame(Duration.seconds(0.55), e -> loadingIcon.setRotate(220)),
                new KeyFrame(Duration.seconds(0.6), e -> loadingIcon.setRotate(240)),
                new KeyFrame(Duration.seconds(0.65), e -> loadingIcon.setRotate(260)),
                new KeyFrame(Duration.seconds(0.7), e -> loadingIcon.setRotate(280)),
                new KeyFrame(Duration.seconds(0.75), e -> loadingIcon.setRotate(300)),
                new KeyFrame(Duration.seconds(0.8), e -> loadingIcon.setRotate(320)),
                new KeyFrame(Duration.seconds(0.85), e -> loadingIcon.setRotate(340)),
                new KeyFrame(Duration.seconds(0.9), e -> loadingIcon.setRotate(360)));

        fancySpinAnim.setCycleCount(Animation.INDEFINITE);
        fancySpinAnim.playFromStart();
    }

    // Create and display a visual representation of a generated dice roll. Final positions are slightly randomized and rotation/position has attached transitions.
    // @param diceA
    // @param diceB
    // @param root - root pane
    // @param roundAssets - any assets (only visualized dice and cards) used within one single round. This grouping is used later.
    // @return N/A
    public void visualizeDice(Dice diceA, Dice diceB, Set<Node> roundAssets) throws FileNotFoundException {
        Image imgDiceI = retrieveImage("src/com/gdjfx/app/assets/dicei.png");
        Image imgDiceII = retrieveImage("src/com/gdjfx/app/assets/diceii.png");
        Image imgDiceIII = retrieveImage("src/com/gdjfx/app/assets/diceiii.png");
        Image imgDiceIV = retrieveImage("src/com/gdjfx/app/assets/diceiv.png");
        Image imgDiceV = retrieveImage("src/com/gdjfx/app/assets/dicev.png");
        Image imgDiceVI = retrieveImage("src/com/gdjfx/app/assets/dicevi.png");

        Image[] imgDice = new Image[]{imgDiceI, imgDiceII, imgDiceIII, imgDiceIV, imgDiceV, imgDiceVI};

        ImageView imgvDiceA = buildImageView(imgDice[diceA.selectedValue-1], 100, 0, true);
        ImageView imgvDiceB = buildImageView(imgDice[diceB.selectedValue-1], 100, 0, true);
        AnimatedMulti diceAAnimator = new AnimatedMulti(imgvDiceA,
                PropertyWrapper.of(imgvDiceA.layoutXProperty()),
                PropertyWrapper.of(imgvDiceA.layoutYProperty()),
                PropertyWrapper.of(imgvDiceA.opacityProperty()),
                PropertyWrapper.of(imgvDiceA.rotateProperty()));

        AnimatedMulti diceBAnimator = new AnimatedMulti(imgvDiceB,
                PropertyWrapper.of(imgvDiceB.layoutXProperty()),
                PropertyWrapper.of(imgvDiceB.layoutYProperty()),
                PropertyWrapper.of(imgvDiceB.opacityProperty()),
                PropertyWrapper.of(imgvDiceB.rotateProperty()));

        setLayout(imgvDiceA, proximityRandom(-120, 15,15), proximityRandom(200,15,15));
        imgvDiceA.setRotate(proximityRandom(10,10,10));
        setLayout(imgvDiceB, proximityRandom(-110, 15, 15), proximityRandom(230,15,15));
        imgvDiceB.setRotate(proximityRandom(-15,15,15));
        bulkAdd(roundAssets, imgvDiceA, imgvDiceB, diceAAnimator, diceBAnimator);
        root.getChildren().addAll(imgvDiceA, imgvDiceB, diceAAnimator, diceBAnimator);

        Timeline visualizeDiceAnim = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> {
                    if (isSceneLoaded) {
                        setLayout(imgvDiceA, proximityRandom(30, 30, 30), proximityRandom(260, 20, 20));
                        imgvDiceA.setRotate(proximityRandom(50, 30, 30));
                    }
                }),
                new KeyFrame(Duration.seconds(6), e -> {
                    if (isSceneLoaded) {
                        setLayout(imgvDiceB, proximityRandom(120, 30, 30), proximityRandom(190, 30, 30));
                        imgvDiceB.setRotate(proximityRandom(-40, 30, 30));
                    }
                }),
                new KeyFrame(Duration.seconds(6.2), e -> {
                    if (isSceneLoaded) playVolumedAudio(sfxDiceroll, sfxVolume);
                }),

                new KeyFrame(Duration.seconds(8), e -> { if (isSceneLoaded) resume();
                })
        );

        root.getChildren().remove(pauseGroup);
        root.getChildren().add(pauseGroup);
        visualizeDiceAnim.play();
    }

    // Generate a random number anchored on a base value with customizable skews to either side.
    // @param base - base value
    // @param lowerOffset - lowest possible # (higher = more skew leftward)
    // @param upperOffset - highest possible # (higher = more skew rightward)
    // @return random number based on set specs
    public static double proximityRandom(double base, double lowerOffset, double upperOffset) { // <+> APM
        return Math.random()*(lowerOffset + upperOffset) + base - lowerOffset;
    }

    // The card equivalent of visualizeDice.
    // @param card
    // @param root
    // @param roundAssets
    // @return N/A
    public void visualizeCard(Card card, Set<Node> roundAssets) throws FileNotFoundException {
        AtomicBoolean isCardReady = new AtomicBoolean(false);
        AtomicBoolean isCardFlipped = new AtomicBoolean(false);

        Image cardCover = ((int)(Math.random()*2) == 0) ? retrieveImage("src/com/gdjfx/app/assets/redCardCover.png") : retrieveImage("src/com/gdjfx/app/assets/blueCardCover.png");
        Image[] cardFaces = new Image[52];

        for (int i = 1; i <= 52; i++) {
            cardFaces[i-1] = retrieveImage("src/com/gdjfx/app/assets/cardfaces/card_" + i + ".png");
        }
        int faceIndex = ((card.cardSuit.getValue() * 13) + (card.cardRank.getValue())) - 1;


        ImageView imgvCard = buildImageView(cardCover, 120, 0, true);
        Timeline cardRevealAnim = new Timeline(
                new KeyFrame(Duration.seconds(2), e -> {
                    if (isSceneLoaded) resume();
                })
        );

        imgvCard.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (!isCardFlipped.get() && isCardReady.get()) {
                playVolumedAudio(sfxCard, sfxVolume);
                System.out.println("Card pressed.");
                imgvCard.setImage(cardFaces[faceIndex]);
                isCardFlipped.set(true);
                cardRevealAnim.playFromStart();
            }
        });

        root.addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
            if (ke.getCode() == KeyCode.SPACE && !isCardFlipped.get() && isCardReady.get()) {
                playVolumedAudio(sfxCard, sfxVolume);
                System.out.println("Card pressed.");
                imgvCard.setImage(cardFaces[faceIndex]);
                isCardFlipped.set(true);
                cardRevealAnim.playFromStart();
            }
        });

        AnimatedMulti cardAnimator = new AnimatedMulti(imgvCard,
                PropertyWrapper.of(imgvCard.layoutXProperty()),
                PropertyWrapper.of(imgvCard.layoutYProperty()),
                PropertyWrapper.of(imgvCard.opacityProperty()),
                PropertyWrapper.of(imgvCard.rotateProperty()));

        setLayout(imgvCard, proximityRandom(-100,20,20), proximityRandom(300,30,30));
        imgvCard.setRotate(proximityRandom(10,20,20));
        bulkAdd(roundAssets, imgvCard, cardAnimator);
        root.getChildren().addAll(imgvCard, cardAnimator);
        root.getChildren().remove(pauseGroup);
        root.getChildren().add(pauseGroup);


        Timeline visualizeCardAnim = new Timeline(
                new KeyFrame(Duration.seconds(4), e -> {
                    if (isSceneLoaded) {
                        setLayout(imgvCard, proximityRandom(80, 30, 30), proximityRandom(230, 30, 30));
                        imgvCard.setRotate(proximityRandom(25, 20, 20));
                    }
                }),
                new KeyFrame(Duration.seconds(5), e -> { if (isSceneLoaded) isCardReady.set(true); })
        );

        visualizeCardAnim.play();
    }

    // Rolls first round of cycle. Overwritten to include steps for visualization.
    // @param N/A
    // @return N/A
    @Override
    public boolean rollInitRound() {
        // Roll 2x dice; move on if either is prime.
        Dice diceA = new Dice();
        Dice diceB = new Dice();
        diceA.roll();
        diceB.roll();
        try {
            visualizeDice(diceA, diceB, roundAssets);
        }
        catch (Exception e) {
            System.err.println("Visualization error: " + e);
        }

        diceHistory.add(diceA.selectedValue);
        diceHistory.add(diceB.selectedValue);

        return (isPrime(diceA.selectedValue) || isPrime(diceB.selectedValue));
    }

    // Rolls second round of cycle. Overwritten to include steps for visualization.
    // @param N/A
    // @return N/A
    @Override
    public boolean rollDoubleRound() {
        // Pick a card; move on if diamond, spade, or JQK of heart
        Card card = new Card();

        cardHistory.add(card);
        try {
            visualizeCard(card, roundAssets);
        }
        catch (Exception e) {
            System.err.println("Visualization error: " + e);
        }

        return (card.cardSuit.equals(Card.Suit.DIAMOND) || card.cardSuit.equals(Card.Suit.SPADE) || (card.cardSuit.equals(Card.Suit.HEART) && card.cardRank.getValue() > 10));
    }


    // Rolls third round of cycle. Overwritten to include steps for visualization.
    // @param N/A
    // @return N/A
    @Override
    public boolean rollQuadRound() {
        // Roll 2x dice; win if both numbers are equal
        Dice diceA = new Dice();
        Dice diceB = new Dice();
        diceA.roll();
        diceB.roll();

        try {
            visualizeDice(diceA, diceB, roundAssets);
        }
        catch (Exception e) {
            System.err.println("Visualization error: " + e);
        }

        optDiceHistory.add(diceA.selectedValue);
        optDiceHistory.add(diceB.selectedValue);

        return (diceA.selectedValue == diceB.selectedValue);
    }



    // Rolls a full cycle. Not many changes from console version apart from JFX-compatibility changes and some magic to allow JFX to wait for user input (otherwise it would compile the pane fully).
    // @param N/A
    // @return N/A
    public void rollCycle() throws InterruptedException {
        final int TOTAL_ROUNDS = 10;
        long initialBal = balance;
        long netBets = 0;
        List<Double> outcomeChances = new ArrayList<>();

        balance -= 10;
        updateOutputText(outputText, "\n\n\nPreliminary cost was pocketed. You now have $" + balance + ".00. ", "{GD_RED}(-$10.00)\n");

        for (int i = 0; i < TOTAL_ROUNDS; i++) {
            boolean hasLostRound = false;
            boolean hasEndedRound = false;
            long winAmount = 0;
            String lossMessage = "";
            currentRound++;
            outcomeChances.clear(); // Clear outcome chances as a new round has started.
            Text checkmarkA = new Text("✓   ");
            checkmarkA.setFill(GD_DEW);
            Text checkmarkB = new Text("✓   ");
            checkmarkB.setFill(GD_DEW);
            Text checkmarkC = new Text("✓   ");
            checkmarkC.setFill(GD_DEW);

            updateOutputText(outputText, "{GD_BLUE}---------------------------------------------\n");
            updateOutputText(outputText, "{GD_ICEBERG}Round " + currentRound + " has started.\n\n\n");

            bet = betPrompt("{GD_ICEBERG}> Choose a bet amount ($10 —— $50).\n");//Long.parseLong(prompt(ANSI_CYAN + "➢ Choose a bet amount ($10 —— $50)." + ANSI_RESET, "Error: bet must be an increment of $10 within $10-$50.", new String[]{"10","20","30","40","50"}, false, false)); // BUG: you can (probably) select a bet that goes beyond your bal. Fix this later.
            balance -= bet;
            netBets += bet;
            updateOutputText(outputText, "Your bet has been withdrawn. You now have " + gdMonetaryParse(balance, true, false, false) + ". ", "{GD_RED}(-$" + bet + ".00)\n");


            while (!hasLostRound && !hasEndedRound) {
                gdFancyDelay(outputText, "\n> Rolling two fair dice...");
                boolean hasPassedInit = rollInitRound();
                pause();

                updateOutputText(outputText, "{GD_ICEBERG}You rolled a " + diceHistory.get(diceHistory.size() - 2) + " and a " + diceHistory.get(diceHistory.size() - 1) + ".\n");
                outputText.getChildren().set(outputText.getChildren().size()-2, checkmarkA);

                // 1ST TEST -- PRIME DICE ROLL
                if (!hasPassedInit) {
                    initLosses++;
                    outcomeChances.add(0.111);
                    lossMessage = "11.1% chance of losing at that stage.\n(neither number was prime)";
                    hasLostRound = true;
                } else {
                    outcomeChances.add(0.889);

                    // 2ND TEST -- LUCKY CARD
                    gdFancyDelay(outputText, "\n> Drawing a random card...");
                    boolean hasPassedDouble = rollDoubleRound();
                    pause();
                    updateOutputText(outputText, "{GD_ICEBERG}You drew a " + toCamelCase(cardHistory.get(cardHistory.size() - 1).cardRank.toString(), true) + " of " + toCamelCase(cardHistory.get(cardHistory.size() - 1).cardSuit.toString(), true) + "s.\n\n\n");
                    outputText.getChildren().set(outputText.getChildren().size()-2, checkmarkB);

                    if (!hasPassedDouble) {
                        outcomeChances.add(0.443);
                        doubleLosses++;
                        lossMessage = "44.3% chance of losing at that stage.\n(club or non-JQK heart)";
                        hasLostRound = true;
                    } else {
                        outcomeChances.add(0.557);
                        doubleWins++;

                        // 3RD TEST -- LUCKY CARD
                        if (ynPrompt(balance, bet)) {
                                updateOutputText(outputText, "{GD_GREEN}[!] Accepted 4x offer.\n");

                                gdFancyDelay(outputText, "\n> Rolling two fair dice...");
                                boolean hasPassedQuad = rollQuadRound();
                                pause();
                                updateOutputText(outputText, "{GD_ICEBERG}You rolled a " + optDiceHistory.get(optDiceHistory.size() - 2) + "...\n");
                                updateOutputText(outputText, "{GD_ICEBERG}...and a " + optDiceHistory.get(optDiceHistory.size() - 1) + ".\n");
                                outputText.getChildren().set(outputText.getChildren().size()-3, checkmarkC);

                                if (!hasPassedQuad) {
                                    outcomeChances.add(0.833);
                                    quadLosses++;
                                    lossMessage = "83.3% chance of losing at that stage.\n(rolled different #s)";
                                    hasLostRound = true;
                                } else {
                                    outcomeChances.add(0.167);
                                    quadWins++;
                                    winAmount += bet * 4;
                                }
                            }

                            else {
                                updateOutputText(outputText, "{GD_RED}[x] Declined 4x offer.\n");
                                winAmount += bet * 2;
                                hasEndedRound = true;
                            }
                        }
                    }


                for (Node ra : roundAssets) { // Handle round assets
                    ra.setOpacity(0);
                }
                roundAssets.clear();

                hasEndedRound = true; // End round.
            }


            if (hasLostRound) {
                updateOutputText(outputText, "{GD_PURPLE}\n\nLost the round! " + lossMessage + "\n");
                totalLosses++;
            } else {
                updateOutputText(outputText, "{GD_PURPLE}\n\nWon the round!\n");
                totalWins++;
            }
            updateOutputText(outputText, "{GD_PURPLE}=======================================\n");

            balance += winAmount;
            netBalance = balance - initialBal;
            double expectedValue = truncate((double) (netBalance - netBets) / currentRound, 2);

            // Takes into account div by zero problem
            double totalWinRate = (currentRound != 0) ? (double) totalWins / currentRound : (double) totalWins;
            double totalWinLoseRatio = (totalLosses != 0) ? (double) totalWins / totalLosses : 0;
            double doubleWinRate = (doubleLosses + doubleWins != 0) ? (double) doubleWins / (doubleWins + doubleLosses) : (double) doubleWins;
            double quadWinRate = (quadLosses + quadWins != 0) ? (double) quadWins / (quadWins + quadLosses) : (double) quadWins;

            // ROUND STATISTICS
            // BALANCE, NET BALANCE
            // CHANCE OF GIVEN PATH
            // EXPECTED VALUE (CUMULATIVE)
            // W/L RATIOS (ALL OF THEM)
            updateOutputText(outputText, "> Balance: " + gdMonetaryParse((balance + bet - winAmount), false, false, false) + " -> ", "{GD_BLUE}" + gdMonetaryParse(balance, false, false, false) + "\n");
            updateOutputText(outputText, "  Net gain/loss (cumulative): " + gdMonetaryParse(netBalance, true, true, true) + "\n");
            updateOutputText(outputText, "  Expected value (cumulative): " + gdMonetaryParse(expectedValue, true, true, true) + "\n");
            updateOutputText(outputText, "  Chance of outcome: " + collectionToConjoinedString(outcomeChances, " * ") + " = " + truncate(getCollectionProduct(outcomeChances) * 100, 2) + "%\n");

            updateOutputText(outputText, "\n> Win rate (total): " + totalWins + " / " + currentRound + " = " + truncate(totalWinRate * 100, 2) + "%\n");
            updateOutputText(outputText, "   W/L ratio (total): " + totalWins + " / " + totalLosses + " = " + truncate(totalWinLoseRatio, 2) + "\n");
            updateOutputText(outputText, "   Win rate (2x stage): " + doubleWins + " / " + (doubleWins + doubleLosses) + " = " + truncate(doubleWinRate * 100, 2) + "%\n");
            updateOutputText(outputText, "   Win rate (4x stage): " + quadWins + " / " + (quadWins + quadLosses) + " = " + truncate(quadWinRate * 100, 2) + "%\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
            fxDelay(8000);
        }
    }

    // JFX-specific delay (equivalent to Thread.sleep(ms)) by using some clever trickery.
    // @param ms - milliseconds to wait
    // @return N/A
    public void fxDelay(long ms) {
        Timeline fxDelayAnim = new Timeline(
                new KeyFrame(Duration.millis(ms), e -> resume())
        );
        fxDelayAnim.playFromStart();
        pause();
    }

    // Adds a bulk amount of items to a collection without needing to package into a collection first.
    // @param collection - any collection of objects of type E
    // @param items - objects of type E
    // @return N/A
    @SafeVarargs
    public static <T extends Collection<E>, E> void bulkAdd(T collection, E... items) { // <+> APM -- this may seem redundant (see List.addAll) but this might be useful when adding a bunch of individual items.
        collection.addAll(Arrays.asList(items));
    }

    public static void playVolumedAudio(AudioClip audioClip, double volume) {
        audioClip.setVolume(volume);
        audioClip.play();
    }

    public static boolean regexedContains(String string, Pattern pattern) {
        Matcher matcher = pattern.matcher(string);
        return matcher.find();
    }
}
