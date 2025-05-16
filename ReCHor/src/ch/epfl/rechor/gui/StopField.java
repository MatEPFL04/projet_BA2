package ch.epfl.rechor.gui;

import ch.epfl.rechor.StopIndex;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Popup;
import javafx.scene.input.KeyEvent;
import javafx.util.Subscription;

import java.util.ArrayList;
import java.util.List;

/**
 * A UI component that combines a text field with an auto-completion popup listing stop names.
 *
 * @param textField the editable text field visible to the user
 * @param stop0     an observable that always holds the currently selected stop
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public record StopField(TextField textField, ObservableValue<String> stop0) {

    private final static int NUMBER_OF_PRESENTED_RESULTS = 30;

    /**
     * Method creates the text field and the popup window.
     *
     * @param stopIndex the index of the stop
     * @return an instance of StopField
     */
    public static StopField create(StopIndex stopIndex) {

        ObjectProperty<String> observableStop = new SimpleObjectProperty<>();

        TextField textField = new TextField();

        Popup popup = new Popup();
        popup.hide();
        popup.setAnchorX(0);
        popup.setAnchorY(0);

        ListView <String> queryResults = new ListView<>();
        queryResults.setFocusTraversable(false);
        queryResults.setMaxHeight(240);
        popup.getContent().add(queryResults);
        popup.setHideOnEscape(false);

        textField.addEventHandler(KeyEvent.KEY_PRESSED, event -> {

            if (event.getCode() == KeyCode.UP ) {
                queryResults.getSelectionModel().selectPrevious();
                event.consume();
            }

            if (event.getCode() == KeyCode.DOWN) {
                queryResults.getSelectionModel().selectNext();
                event.consume();
            }
        });

        ArrayList <Subscription>  subscriptions = new ArrayList<>();

        textField.focusedProperty().subscribe( focusProperty -> {

            if (focusProperty) {

                popup.show(textField, popup.getAnchorX(), popup.getAnchorY());

                subscriptions.add(textField.textProperty().subscribe(
                        ((newText) -> {
                    List<String> foundResults = stopIndex.stopsMatching(newText,
                            NUMBER_OF_PRESENTED_RESULTS);
                    queryResults.getItems().setAll(foundResults);
                    queryResults.getSelectionModel().selectFirst();
                })));

                subscriptions.add(textField.boundsInLocalProperty().subscribe(
                        (newBounds) ->
                {
                    Bounds newBound = textField.localToScreen(newBounds);
                    popup.setAnchorX(newBound.getMinX());
                    popup.setAnchorY(newBound.getMaxY());
                }));
            }

            else {
                subscriptions.forEach(Subscription::unsubscribe);
                subscriptions.clear();
                popup.hide();
                // Observable value containing the name of the stop
                if (queryResults.getItems().isEmpty()) {
                    observableStop.set("");
                    textField.clear();
                }

                else {
                    String selectedStopInListView = queryResults.getSelectionModel().getSelectedItem();
                    textField.textProperty().set(selectedStopInListView);
                    observableStop.set(selectedStopInListView);
                }
            }
        });
        return new StopField(textField, observableStop);
    }

    /**
     * Assigns the name of the stop to the field.
     *
     * @param stopName name of the stop
     */
    public void setTo(String stopName) {

        if (stop0 instanceof ObjectProperty stop0) {
            textField.textProperty().set(stopName);
            stop0.set(stopName);
        }
    }
}