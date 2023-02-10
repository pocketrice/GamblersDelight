package com.gdjfx.app;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Due to limitations FocusableGroup can only accept custom nodes. Find a way to let native nodes (which I can't really edit to implement Focusable) work?
public class FocusableGroup extends Group implements CustomGroup {
    private List<Focusable> focusableList; // todo: ensure these are both Nodes (to be addable to group) AND implement Focusable.
    private int defaultIndex, nodeIndex, prevIndex;
    static Pane root;

    public FocusableGroup(Pane root) {
        focusableList = new ArrayList<>();
        nodeIndex = defaultIndex = prevIndex = 0;
        FocusableGroup.root = root;
        attachInputHandler();
    }

    public FocusableGroup(Pane root, Focusable... focusables) {
        focusableList = List.of(focusables);
        nodeIndex = defaultIndex = prevIndex = 0;
        FocusableGroup.root = root;
        this.getChildren().addAll(Arrays.stream(focusables).map(f -> (Node) f).toList());
        attachInputHandler();
    }

    public FocusableGroup(Pane root, int index, Focusable... focusables) {
        focusableList = List.of(focusables);
        nodeIndex = defaultIndex = prevIndex = index;
        this.root = root;
        this.getChildren().addAll(Arrays.stream(focusables).map(f -> (Node) f).toList());
        attachInputHandler();
    }



    public void attachInputHandler() {
        this.focusedProperty().addListener((observable, oldVal, isFocused) -> {
            root.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<>() {
                public void handle(KeyEvent ke) {
                    switch (ke.getCode()) {
                        case LEFT -> {
                            if (isFocused) {
                                prevIndex = nodeIndex;
                                nodeIndex = (nodeIndex - 1 < 0) ? focusableList.size() - 1 : nodeIndex - 1;
                                updateFocus();
                            }

                        }

                        case RIGHT -> {
                            if (isFocused) {
                                prevIndex = nodeIndex;
                                nodeIndex = (nodeIndex + 1 > focusableList.size()) ? 0 : nodeIndex + 1;
                                updateFocus();
                            }
                        }
                    }
                }
            });

            if (isFocused) {
                nodeIndex = defaultIndex;
            }
        });
    }
    public void updateFocus() {
       focusableList.get(prevIndex).handleFocus(false);
       focusableList.get(nodeIndex).handleFocus(true);
    }

    @Override
    public void setLayout(double layoutX, double layoutY) {
        this.setLayoutX(layoutX);
        this.setLayoutY(layoutY);
    }
}
