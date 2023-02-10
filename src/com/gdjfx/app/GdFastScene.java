// Lucas Xie - P5 AP CSA - 1/26/23 - GDJFX

package com.gdjfx.app;

import com.gdjfx.Card;
import com.gdjfx.Dice;
import com.gdjfx.cli.GdFastConsole;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.gdjfx.app.CSSManager.*;
import static com.gdjfx.app.GdSlowScene.*;
import static com.gdjfx.app.ProgramApplet.*;

public class GdFastScene extends GdFastConsole implements ModeScene {
    Pane root;
    Rectangle obfPanel;
    TextFlow outputText;
    Group ynPromptGroup, trialPromptGroup, promptBoxGroup, pauseGroup;
    FocusableGroup trialPromptFocusables;
    Set<Node> roundAssets = new HashSet<>();
    boolean isPaused = false, isRunningAgain = true, isGameStarted = false, isPrompted = false, isStartLoading = false, isSceneLoaded = false;

    Font suburga = Font.loadFont("file:src/com/gdjfx/app/assets/suburga.otf", 20);
    Font attorneyButtons = Font.loadFont("file:src/com/gdjfx/app/assets/attorneybuttons.ttf", 20);
    Font igiari = Font.loadFont("file:src/com/gdjfx/app/assets/igiari.ttf", 12);

    Media bgmConfrontAllegro = new Media(new File("src/com/gdjfx/app/assets/audio/bgm/confrontation_allegro.mp3").toURI().toString());
    MediaPlayer bgmPlayer = buildAudio(bgmConfrontAllegro, bgmVolume, MediaPlayer.INDEFINITE);
    AudioClip sfxNewEvidence = new AudioClip(new File("src/com/gdjfx/app/assets/audio/sfx/new_evidence.wav").toURI().toString());
    AudioClip sfxSelectLong = new AudioClip(new File("src/com/gdjfx/app/assets/audio/sfx/select_long.wav").toURI().toString());
    AudioClip sfxCancel = new AudioClip(new File("src/com/gdjfx/app/assets/audio/sfx/cancel.wav").toURI().toString());
    AudioClip sfxDollop = new AudioClip(new File("src/com/gdjfx/app/assets/audio/sfx/dollop.wav").toURI().toString());
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


    private int thousandsValue = 5, hundredsValue = 0, tensValue = 0, onesValue = 0, rerunCount;
    private int currentRound, totalWins, totalLosses, doubleWins, quadWins, initLosses, doubleLosses, quadLosses;
    private long balance, netBalance, bet, trialCount;
    private double totalWinRate, totalWinLoseRatio, doubleWinRate, quadWinRate, expectedValue;
    private List<Card> cardHistory = new ArrayList<>();
    private List<Integer> diceHistory = new ArrayList<>();
    private List<Integer> optDiceHistory = new ArrayList<>();

    public GdFastScene() {
        balance = 100;
        trialCount = 5000;
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
    // @param obfPanel - obfuscation panel
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
        isSceneLoaded = true;
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
            } catch (FileNotFoundException e) {
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

        FilledSlider bgmSlider = new FilledSlider(Color.web("#709e98"), Color.web("#b4d2c9"));
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


        FilledSlider sfxSlider = new FilledSlider(Color.web("#76709e"), Color.web("#b7b4d2"));
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


        Rectangle outputPanel = new Rectangle(370,0,400,765);
        outputPanel.setFill(Color.valueOf("#2a2537C0"));

        Rectangle promptBoxBG = new Rectangle(300,300, Color.valueOf("#6a4881A4"));
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
                rerunCount++;
                isRunningAgain = true;
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
                bgmPlayer.stop();
                changeRoot(ProgramApplet.root);
                resetMarquee();
                activeBtnIndex = -1;
                updateSelections();
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


        ynPromptGroup = new Group(btnYes, btnNo);

        Stepper thousandsStepper = new Stepper.Builder(new int[]{0,9}, root).value(5).increment(1).btnIncr("+").btnDecr("-").paletteColor(Color.web("#709e9890")).build();
        Stepper hundredsStepper = new Stepper.Builder(new int[]{0,9}, root).value(0).increment(1).btnIncr("+").btnDecr("-").paletteColor(Color.web("#9e8fb590")).build();
        Stepper tensStepper = new Stepper.Builder(new int[]{0,9}, root).value(0).increment(1).btnIncr("+").btnDecr("-").paletteColor(Color.web("#9e8fb590")).build();
        Stepper onesStepper = new Stepper.Builder(new int[]{0,9}, root).value(0).increment(1).btnIncr("+").btnDecr("-").paletteColor(Color.web("#9e8fb590")).build();
        trialPromptFocusables = new FocusableGroup(root, thousandsStepper, hundredsStepper, tensStepper, onesStepper);
        trialPromptGroup = new Group(trialPromptFocusables, btnSubmitBet);
        thousandsStepper.setLayout(420, 330);
        hundredsStepper.setLayout(455, 330);
        tensStepper.setLayout(490, 330);
        onesStepper.setLayout(525, 330);
        setLayout(btnSubmitBet, 590,360);

        trialPromptFocusables.getChildren().stream().map(n -> (Stepper) n).forEach(s -> {
            s.getIncr().setOnAction(actionEvent -> {
                if (isPrompted) {
                    playVolumedAudio(sfxDollop, sfxVolume);
                    s.incrementValue();
                }
            });

            s.getIncr().pressedProperty().addListener((observable, oldVal, isPressed) -> {
                if (isPressed) {
                    s.getIncr().setStyle("-fx-background-color: derive(" + stringifyAlphaColor(s.getPalette()[1]) + ", -15%)");
                }
                else {
                    s.getIncr().setStyle("-fx-background-color: " + stringifyAlphaColor(s.getPalette()[1]));
                }
            });

            s.getDecr().setOnAction(actionEvent -> {
                if (isPrompted) {
                    playVolumedAudio(sfxDollop, sfxVolume);
                    s.decrementValue();
                }
            });

            s.getDecr().pressedProperty().addListener((observable, oldVal, isPressed) -> {
                if (isPressed) {
                    s.getDecr().setStyle("-fx-background-color: derive(" + stringifyAlphaColor(s.getPalette()[1]) + ", -15%)");
                }
                else {
                    s.getDecr().setStyle("-fx-background-color: " + stringifyAlphaColor(s.getPalette()[1]));
                }
            });
        });
/*
        Rectangle thousandsDigitBG = new Rectangle(30,70,Color.valueOf("#9e8fb590"));
        Rectangle hundredsDigitBG = new Rectangle(30,70,Color.valueOf("#9e8fb590"));
        Rectangle tensDigitBG = new Rectangle(30,70,Color.valueOf("#9e8fb590"));
        Rectangle onesDigitBG = new Rectangle(30,70,Color.valueOf("#9e8fb590"));

        Text thousandsDigit = new Text("" + thousandsValue);
        thousandsDigit.setFont(igiari);
        thousandsDigit.setStyle("-fx-font-size: 30;");
        thousandsDigit.setFill(Color.valueOf("#392e44"));
        Text hundredsDigit = new Text("" + hundredsValue);
        hundredsDigit.setFont(igiari);
        hundredsDigit.setStyle("-fx-font-size: 30;");
        hundredsDigit.setFill(Color.valueOf("#392e44"));
        Text tensDigit = new Text("" + tensValue);
        tensDigit.setFont(igiari);
        tensDigit.setStyle("-fx-font-size: 30;");
        tensDigit.setFill(Color.valueOf("#392e44"));
        Text onesDigit = new Text("" + onesValue);
        onesDigit.setFont(igiari);
        onesDigit.setStyle("-fx-font-size: 30;");
        onesDigit.setFill(Color.valueOf("#392e44"));

        Button btnThousandsIncr = new Button("+");
        btnThousandsIncr.setFocusTraversable(false);
        btnThousandsIncr.setFont(igiari);
        btnThousandsIncr.setPrefSize(30, 8);
        btnThousandsIncr.setStyle("-fx-background-color: #b5aac6C0");
        btnThousandsIncr.setOnAction(actionEvent -> {
            if (isPrompted) {
                playVolumedAudio(sfxDollop, sfxVolume);
                thousandsValue = (thousandsValue + 1 > 9) ? 0 : thousandsValue + 1;
                thousandsDigit.setText("" + thousandsValue);
            }
        });

        Button btnThousandsDecr = new Button("-");
        btnThousandsDecr.setFocusTraversable(false);
        btnThousandsDecr.setFont(igiari);
        btnThousandsDecr.setPrefSize(30, 8);
        btnThousandsDecr.setStyle("-fx-background-color: #b5aac6C0");

        btnThousandsDecr.setOnAction(actionEvent -> {
            if (isPrompted) {
                playVolumedAudio(sfxDollop, sfxVolume);
                thousandsValue = (thousandsValue - 1 < 0) ? 9 : thousandsValue - 1;
                thousandsDigit.setText("" + thousandsValue);
            }
        });



        Button btnHundredsIncr = new Button("+");
        btnHundredsIncr.setFocusTraversable(false);
        btnHundredsIncr.setFont(igiari);
        btnHundredsIncr.setPrefSize(30, 8);
        btnHundredsIncr.setStyle("-fx-background-color: #b5aac6C0");
        btnHundredsIncr.setOnAction(actionEvent -> {
            if (isPrompted) {
                playVolumedAudio(sfxDollop, sfxVolume);
                hundredsValue = (hundredsValue + 1 > 9) ? 0 : hundredsValue + 1;
                hundredsDigit.setText("" + hundredsValue);
            }
        });

        Button btnHundredsDecr = new Button("-");
        btnHundredsDecr.setFocusTraversable(false);
        btnHundredsDecr.setFont(igiari);
        btnHundredsDecr.setPrefSize(30, 8);
        btnHundredsDecr.setStyle("-fx-background-color: #b5aac6C0");

        btnHundredsDecr.setOnAction(actionEvent -> {
            if (isPrompted) {
                playVolumedAudio(sfxDollop, sfxVolume);
                hundredsValue = (hundredsValue - 1 < 0) ? 9 : hundredsValue - 1;
                hundredsDigit.setText("" + hundredsValue);
            }
        });


        Button btnTensIncr = new Button("+");
        btnTensIncr.setFocusTraversable(false);
        btnTensIncr.setFont(igiari);
        btnTensIncr.setPrefSize(30, 8);
        btnTensIncr.setStyle("-fx-background-color: #b5aac6C0");
        btnTensIncr.setOnAction(actionEvent -> {
            if (isPrompted) {
                playVolumedAudio(sfxDollop, sfxVolume);
                tensValue = (tensValue + 1 > 9) ? 0 : tensValue + 1;
                tensDigit.setText("" + tensValue);
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
                tensValue = (tensValue - 1 < 0) ? 9 : tensValue - 1;
                tensDigit.setText("" + tensValue);
            }
        });

        Button btnOnesIncr = new Button("+");
        btnOnesIncr.setFocusTraversable(false);
        btnOnesIncr.setFont(igiari);
        btnOnesIncr.setPrefSize(30, 8);
        btnOnesIncr.setStyle("-fx-background-color: #b5aac6C0");
        btnOnesIncr.setOnAction(actionEvent -> {
            if (isPrompted) {
                playVolumedAudio(sfxDollop, sfxVolume);
                onesValue = (onesValue + 1 > 9) ? 0 : onesValue + 1;
                onesDigit.setText("" + onesValue);
            }
        });

        Button btnOnesDecr = new Button("-");
        btnOnesDecr.setFocusTraversable(false);
        btnOnesDecr.setFont(igiari);
        btnOnesDecr.setPrefSize(30, 8);
        btnOnesDecr.setStyle("-fx-background-color: #b5aac6C0");

        btnOnesDecr.setOnAction(actionEvent -> {
            if (isPrompted) {
                playVolumedAudio(sfxDollop, sfxVolume);
                onesValue = (onesValue - 1 < 0) ? 9 : onesValue - 1;
                onesDigit.setText("" + onesValue);
            }
        });


        trialPrompt = new Group(thousandsDigitBG, hundredsDigitBG, tensDigitBG, onesDigitBG, thousandsDigit, hundredsDigit, tensDigit, onesDigit, btnThousandsIncr, btnThousandsDecr, btnHundredsIncr, btnHundredsDecr, btnTensIncr, btnTensDecr, btnOnesIncr, btnOnesDecr, btnSubmitBet);
        setLayout(thousandsDigitBG, 420, 325);
        setLayout(hundredsDigitBG,455, 325);
        setLayout(tensDigitBG, 490, 325);
        setLayout(onesDigitBG,525, 325);

        setLayout(thousandsDigit,430,378);
        setLayout(hundredsDigit,465,378);
        setLayout(tensDigit,500,378);
        setLayout(onesDigit,535,378);

        setLayout(btnThousandsIncr,420,325);
        setLayout(btnThousandsDecr, 420, 385);
        setLayout(btnHundredsIncr, 455,325);
        setLayout(btnHundredsDecr,455,385);
        setLayout(btnTensIncr,490,325);
        setLayout(btnTensDecr, 490, 385);
        setLayout(btnOnesIncr, 525,325);
        setLayout(btnOnesDecr,525,385);
        setLayout(btnSubmitBet, 590,360);*/



        promptBoxGroup = new Group(promptBoxBG, promptBarBG, promptBarText, promptBarArrow1, promptBarArrow2, trialPromptGroup, ynPromptGroup);
        setLayout(promptBoxGroup, 0, 200);
        setLayout(promptBoxBG, 400, 300);
        setLayout(promptBarBG, 400, 300);
        setLayout(promptBarText, 490, 317);
        setLayout(promptBarArrow1, 440, 317);
        setLayout(promptBarArrow2, 630, 317);
        Animated<Double> pbAnimator = new Animated<>(promptBoxGroup, PropertyWrapper.of(promptBoxGroup.layoutYProperty())).custom(settings -> settings.withDuration(Duration.seconds(3)).withCurve(Curve.EASE_IN_OUT));

        promptBoxGroup.layoutYProperty().addListener((observable, oldVal, isLayoutYChanged) -> { // Reset submit button color (for bet prompt) / focus (for yn prompt) when prompted again
            if (isPrompted) {
                btnSubmitBet.setStyle("-fx-background-color: #958cadD0");
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
        root.getChildren().add(promptBoxGroup);
        root.getChildren().add(pbAnimator);
        root.getChildren().add(pauseMenu);
        root.getChildren().add(btnPause);
        root.getChildren().add(obfPanel);
        root.getChildren().add(btnStart);

        root.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<>() {
            int ynIndex = 0, digitIndex = 0;
            public void handle(KeyEvent ke) {
                if (isGameStarted || ke.getCode() == KeyCode.ENTER) {
                    switch (ke.getCode()) {
                        /*case UP -> { // todo: increment selected button
                            if (!isPaused && isPrompted && trialPrompt.isVisible() && !outputScroll.isFocused()) {
                                btnTensIncr.fire();
                            }
                            System.out.println("CURRENT FOCUS: " + scene.focusOwnerProperty().get());
                        }

                        case DOWN -> { // todo increment sel button
                            if (!isPaused && isPrompted && trialPrompt.isVisible() && !outputScroll.isFocused()) {
                                btnTensDecr.fire();
                            }
                            System.out.println("CURRENT FOCUS: " + scene.focusOwnerProperty().get());
                        }*/

                        case LEFT -> { // both prompts (move focus leftward)
                            if (isPaused && bgmSlider.slider.isFocused()) {
                                playVolumedAudio(sfxPeepHigh, bgmVolume);
                            }

                            else if (isPaused && sfxSlider.slider.isFocused()) {
                                playVolumedAudio(sfxPeepLow, sfxVolume);
                            }

                            else if (isPrompted && ynPromptGroup.isVisible()) {
                                btnYes.requestFocus();
                                ynIndex = 0;
                            }

                            else if (isPrompted && trialPromptGroup.isVisible()) {
                                digitIndex = (digitIndex-1 < 0) ? 3 : digitIndex-1;
                            }

                            System.out.println("CURRENT FOCUS: " + scene.focusOwnerProperty().get());
                        }

                        case RIGHT -> { // both prompts (move focus rightward)
                            if (isPaused && bgmSlider.slider.isFocused()) {
                                playVolumedAudio(sfxPeepHigh, bgmVolume);
                            }

                            else if (isPaused && sfxSlider.slider.isFocused()) {
                                playVolumedAudio(sfxPeepLow, sfxVolume);
                            }

                            else if (isPrompted && ynPromptGroup.isVisible()) {
                                btnNo.requestFocus();
                                ynIndex = 1;
                            }

                            else if (isPrompted && trialPromptGroup.isVisible()) {
                                digitIndex = (digitIndex+1 > 3) ? 0 : digitIndex+1;
                            }

                            System.out.println("CURRENT FOCUS: " + scene.focusOwnerProperty().get());
                        }

                        case ESCAPE -> btnPause.fire();

                        case Z -> { // ynPrompt - selects no (or focuses no?)
                            root.requestFocus();
                        }

                        case X -> { // ynPrompt (select focused option), betPrompt (auto-move (end auto-move when focused submit) / submit on focused submit)
                            if (!isPaused && isPrompted && ynPromptGroup.isVisible()) {
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
                            if (!isPaused && isPrompted && trialPromptGroup.isVisible()) {
                                btnSubmitBet.fire();
                            }
                            else if (!isGameStarted && !isStartLoading) {
                                btnStart.fire();
                            }
                            else if (!isPaused && isPrompted && ynPromptGroup.isVisible()) {
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


    // Create and display a visual representation of a generated dice roll. Final positions are slightly randomized and rotation/position has attached transitions.
    // @param diceA
    // @param diceB
    // @param root - root pane
    // @param roundAssets - any assets (only visualized dice and cards) used within one single round. This grouping is used later.
    // @return N/A
    public void visualizeDice(Dice diceA, Dice diceB, Set<Node> roundAssets) throws FileNotFoundException {
        diceA.roll();
        diceB.roll();

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
                })
        );

        visualizeDiceAnim.play();
    }

    // The card equivalent of visualizeDice.
    // @param card
    // @param root
    // @param roundAssets
    // @return N/A
    public void visualizeCard(Card card, Set<Node> roundAssets) throws FileNotFoundException {
        Image cardCover = ((int)(Math.random()*2) == 0) ? retrieveImage("src/com/gdjfx/app/assets/redCardCover.png") : retrieveImage("src/com/gdjfx/app/assets/blueCardCover.png");

        ImageView imgvCard = buildImageView(cardCover, 120, 0, true);

        AnimatedMulti cardAnimator = new AnimatedMulti(imgvCard,
                PropertyWrapper.of(imgvCard.layoutXProperty()),
                PropertyWrapper.of(imgvCard.layoutYProperty()),
                PropertyWrapper.of(imgvCard.opacityProperty()),
                PropertyWrapper.of(imgvCard.rotateProperty()));

        setLayout(imgvCard, proximityRandom(-100,20,20), proximityRandom(300,30,30));
        imgvCard.setRotate(proximityRandom(10,20,20));
        bulkAdd(roundAssets, imgvCard, cardAnimator);
        root.getChildren().addAll(imgvCard, cardAnimator);

        Timeline visualizeCardAnim = new Timeline(
                new KeyFrame(Duration.seconds(4), e -> {
                    if (isSceneLoaded) {
                        setLayout(imgvCard, proximityRandom(80, 30, 30), proximityRandom(230, 30, 30));
                        imgvCard.setRotate(proximityRandom(25, 20, 20));
                    }
                })
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

        optDiceHistory.add(diceA.selectedValue);
        optDiceHistory.add(diceB.selectedValue);

        return (diceA.selectedValue == diceB.selectedValue);
    }


    // Non-generic prompting method to ask the user for # of trials
    // @param message - message to display
    // @return # of trials to run per cycle
    public long trialPrompt(String message) {
        isPrompted = true;
        updateOutputText(outputText, message);
        ynPromptGroup.setVisible(false);
        trialPromptGroup.setVisible(true);
        promptBoxGroup.setLayoutY(50);
        trialPromptFocusables.requestFocus();
        pause();

        promptBoxGroup.setLayoutY(180);
        return thousandsValue * 1000L + hundredsValue * 100L + tensValue * 10L + onesValue;
    }





    // Non-generic prompting method to ask the user if they want to run another cycle
    // @param N/A
    // @return whether or not to run another cycle
    public boolean ynPrompt() {
        isPrompted = true;
        updateOutputText(outputText, "{GD_PURPLE}* Cycle complete. Would you like to continue rolling trials?\n\n\n\n\n\n\n\n\n\n\n");
        ynPromptGroup.setVisible(true);
        trialPromptGroup.setVisible(false);
        promptBoxGroup.setLayoutY(50);
        pause();

        promptBoxGroup.setLayoutY(180);
        return isRunningAgain;
    }

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

    // Rolls a full cycle. Not many changes from console version apart from JFX-compatibility changes and some magic to allow JFX to wait for user input (otherwise it would compile the pane fully).
    // @param N/A
    // @return N/A
    public void rollCycle() throws FileNotFoundException {
        long initialBal = balance;
        long netBets = 0;

        trialCount = trialPrompt("\n\n\n{GD_BLUE}* How many trials will you run?");
        balance -= 10;
        updateOutputText(outputText, "\n\n\nPreliminary cost was pocketed. You now have $" + balance + ".00. ", "{GD_RED}(-$10.00)\n\n");

        while (isRunningAgain) {
            long pretime = getMillisecSinceUnixEpoch();
            gdFancyDelay(outputText, "* Processing trials...");
            updateOutputText(outputText, "{GD_PURPLE}\n---------------------------------------------------------\n");

            for (int i = 0; i < trialCount; i++) {
                if (i < 70 && rerunCount == 0) {
                    visualizeCard(new Card(), roundAssets);
                    visualizeDice(new Dice(), new Dice(), roundAssets);
                }
                else if (i < 30 && rerunCount < 5) {
                    visualizeCard(new Card(), roundAssets);
                    visualizeDice(new Dice(), new Dice(), roundAssets);
                }
                else if (i < 5){
                    visualizeCard(new Card(), roundAssets);
                    visualizeDice(new Dice(), new Dice(), roundAssets);
                }

                boolean hasLostRound = false;
                boolean hasEndedRound = false;
                long winAmount = 0;
                currentRound++;

                bet = 10;
                balance -= bet;
                netBets += bet;


                while (!hasLostRound && !hasEndedRound) { // WHILE NOT EVALUATING PROPERLY? FIXME
                    if (!rollInitRound()) {
                        initLosses++;
                        hasLostRound = true;
                    } else {
                        // 2ND TEST
                        if (!rollDoubleRound()) {
                            doubleLosses++;
                            hasLostRound = true;
                        } else {
                            // 3RD TEST
                            doubleWins++;
                            switch ((int) (Math.random() * 2)) {
                                case 0 -> {
                                    if (!rollQuadRound()) {
                                        quadLosses++;
                                        hasLostRound = true;
                                    } else {
                                        quadWins++;
                                        winAmount += bet * 4;
                                    }
                                }

                                case 1 -> {
                                    winAmount += bet * 2;
                                }
                            }
                        }
                    }

                    hasEndedRound = true; // End round.
                }


                if (hasLostRound) {
                    totalLosses++;
                } else {
                    totalWins++;
                }

                balance += winAmount;
                netBalance = balance - initialBal;
                expectedValue = truncate((double) (netBalance - netBets) / currentRound, 2);

                // Takes into account div by zero problem
                totalWinRate = (currentRound != 0) ? (double) totalWins / currentRound : (double) totalWins;
                totalWinLoseRatio = (totalLosses != 0) ? (double) totalWins / totalLosses : 0;
                doubleWinRate = (doubleLosses + doubleWins != 0) ? (double) doubleWins / (doubleWins + doubleLosses) : (double) doubleWins;
                quadWinRate = (quadLosses + quadWins != 0) ? (double) quadWins / (quadWins + quadLosses) : (double) quadWins;
            }
            long posttime = getMillisecSinceUnixEpoch();

            // ROUND STATISTICS
            // BALANCE, NET BALANCE
            // CHANCE OF GIVEN PATH
            // EXPECTED VALUE (CUMULATIVE)
            // W/L RATIOS (ALL OF THEM)
            updateOutputText(outputText, "> Balance: " + gdMonetaryParse(initialBal, false, false, false) + " -> ", "{GD_BLUE}" + gdMonetaryParse(balance, false, false, false) + "\n");
            updateOutputText(outputText, "  Net gain/loss: " + gdMonetaryParse(netBalance, true, true, true) + "\n");
            updateOutputText(outputText, "  Expected value: " + gdMonetaryParse(expectedValue, true, true, true) + "\n");

            updateOutputText(outputText, "\n> Win rate (total): " + totalWins + " / " + currentRound + " = " + truncate(totalWinRate * 100, 2) + "%\n");
            updateOutputText(outputText, "   W/L ratio (total): " + totalWins + " / " + totalLosses + " = " + truncate(totalWinLoseRatio, 2) + "\n");
            updateOutputText(outputText, "   Win rate (2x stage): " + doubleWins + " / " + currentRound + " = " + truncate(doubleWinRate * 100, 2) + "%\n");
            updateOutputText(outputText, "   Win rate (4x stage): " + quadWins + " / " + currentRound + " = " + truncate(quadWinRate * 100, 2) + "%\n\n\n\n\n\n\n\n\n\n\n\n");
            updateOutputText(outputText, "\n>  Trials run: " + trialCount + " trials\n");
            updateOutputText(outputText, "   Operation time: " + (posttime - pretime) + " ms\n\n\n\n\n");
            fxDelay(8000);
            ynPrompt();
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
}
