package com.gdjfx.app;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    private List<Focusable> focusableList;
    private int nodeIndex, prevIndex;
    static Pane root;

    final ChangeListener<Boolean> focusHandler = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldVal, Boolean isChanged) {
            root.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<>() {
                public void handle(KeyEvent ke) {
                    if (isChanged) {
                        switch (ke.getCode()) {
                            case LEFT -> {
                                prevIndex = nodeIndex;
                                nodeIndex = (nodeIndex - 1 < 0) ? focusableList.size() - 1 : nodeIndex - 1;
                                updateFocus();

                            }

                            case RIGHT, TAB -> {
                                prevIndex = nodeIndex;
                                nodeIndex = (nodeIndex + 1 > focusableList.size() - 1) ? 0 : nodeIndex + 1;
                                updateFocus();
                            }
                        }
                    }
                }
            });
        }
    };

    public FocusableGroup(Pane root) {
        focusableList = new ArrayList<>();
        nodeIndex = prevIndex = 0;
        FocusableGroup.root = root;
        attachInputHandler();
    }

    public FocusableGroup(Pane root, Focusable... focusables) {
        focusableList = List.of(focusables);
        nodeIndex = prevIndex = 0;
        FocusableGroup.root = root;
        this.getChildren().addAll(Arrays.stream(focusables).map(f -> (Node) f).toList());
        attachInputHandler();
    }

    public FocusableGroup(Pane root, int index, Focusable... focusables) {
        focusableList = List.of(focusables);
        nodeIndex = prevIndex = index;
        this.root = root;
        this.getChildren().addAll(Arrays.stream(focusables).map(f -> (Node) f).toList());
        attachInputHandler();
    }


    // Attaches the focus handler to the FocusableGroup's focused property. Might be able to just inline handler creation, or use current setup to add/remove the handler as needed.
    // @param N/A
    // @return N/A
    public void attachInputHandler() {
        this.focusedProperty().addListener(focusHandler);
    }

    // Updates the focus status for components in the FocusableGroup. The previously focused component is unfocused and vice versa.
    // @param N/A
    // @return N/A
    public void updateFocus() {
       focusableList.get(prevIndex).handleFocus(false);
       focusableList.get(nodeIndex).handleFocus(true);
    }

    // Return accessible list of all components in group. Useful for reading information from the group.
    // @param N/A
    // @return list of all components in list (of type Focusable)
    public List<Focusable> getFocusableList() {
        return focusableList;
    }

    // Sets whether or not the FocusableGroup is focused (mainly for use outside of class). May need to implement "native JavaFX focus locking" to avoid unintentional focusing when you're pseudofocused on this group.
    // @param isFocused - is the focusable group focused?
    // @return N/A
    public void setGroupFocus(boolean isFocused) {
        if (isFocused) {
            this.requestFocus();
            for (Focusable focusableNode : focusableList) {
                focusableNode.handleFocus((focusableList.indexOf(focusableNode) == nodeIndex));
            }
        }
        else {
            for (Focusable focusableNode : focusableList) {
                focusableNode.handleFocus(false);
            }
        }
    }

    // Required by CustomGroup. Sets the layout position of the group.
    // @param layoutX - x-position of FocusableGroup
    // @param layoutY - y-position of FocusableGroup
    // @return N/A
    @Override
    public void setLayout(double layoutX, double layoutY) {
        this.setLayoutX(layoutX);
        this.setLayoutY(layoutY);
    }
}
