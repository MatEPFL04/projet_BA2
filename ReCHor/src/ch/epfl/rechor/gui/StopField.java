package ch.epfl.rechor.gui;

import ch.epfl.rechor.StopIndex;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
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
import java.util.concurrent.Flow;


public record StopField(TextField textField, ObservableValue<String> stop0) {

    private final static int NUMBER_OF_PRESENTED_RESULTS = 30;


    public static StopField create(StopIndex stopIndex) {


        ObjectProperty<String> observableStop = new SimpleObjectProperty<>();

        TextField textField = new TextField();

        Popup popup = new Popup();

        ListView <String> queryResults = new ListView<>();
        queryResults.setFocusTraversable(false);
        queryResults.setMaxHeight(240);

        popup.getContent().add(queryResults);
        popup.setHideOnEscape(false);

        ArrayList <Subscription>  subscriptions = new ArrayList<>();


        textField.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.UP ) {
                queryResults.getSelectionModel().selectPrevious();
                event.consume();
            }
            else if (event.getCode() == KeyCode.DOWN) {
                queryResults.getSelectionModel().selectNext();
                event.consume();
            }

        });

        textField.focusedProperty().subscribe( focusProperty -> {

            if (focusProperty) {
                popup.show(queryResults, popup.getAnchorX(), popup.getAnchorY());

                subscriptions.add( textField.textProperty().subscribe( ((oldText, newText) -> {
                    List<String> foundResults = stopIndex.stopsMatching(newText, NUMBER_OF_PRESENTED_RESULTS);
                    queryResults.getItems().setAll(foundResults);

                })));

                subscriptions.add(textField.boundsInLocalProperty().subscribe( ((oldBounds, newBounds) -> {

                    // nouvelle bound calculée en fonction du système de coordonnées de l'écran et ancrer le popup
                    Bounds newBound = textField.localToScreen(newBounds);
                    popup.setAnchorX(newBound.getMinX());
                    popup.setAnchorX(newBound.getMaxY());


                })));


            }
            else {
                // propriétés ne sont plus observées
                subscriptions.forEach(Subscription::unsubscribe);
                subscriptions.clear();

                //fenêtre des résultats cachés
                popup.hide();

                if (queryResults.getItems().isEmpty()) {
                    observableStop.set("");
                    textField.clear();
                }

                // le nom de l'arrêt actuellement sélectionné est copié dans le champ textuel et dans la propriété stop
                else {
                    String selectedStopInListView = queryResults.getSelectionModel().getSelectedItem();
                    //setTo(selectedStopInListView);
                }
            }
        });

        return new StopField(textField, observableStop);





    }

    public void setTo(String stopName) {

        if (stop0 instanceof ObjectProperty stop0) {
            textField.textProperty().set(stopName);
            stop0.set(stopName);
        }


    }
}


