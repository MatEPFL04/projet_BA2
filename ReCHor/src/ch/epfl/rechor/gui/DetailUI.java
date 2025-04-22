package ch.epfl.rechor.gui;

import ch.epfl.rechor.journey.Journey;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Record representing the detail of a journey.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public record DetailUI(Node rootNode) {

    /**
     * @param observableValue
     * @return
     */
    public static DetailUI create(ObservableValue<Journey> observableValue) {

        // 1. Construction du graphe de scène principal

        /*
        if (observableValue == null || Objects.isNull(observableValue.getValue())) {

            Text text = new Text("Aucun voyage");
            VBox box = new VBox(text);
            box.setAlignment(Pos.CENTER);
            StackPane stackPane = new StackPane(box);
            stackPane.setAlignment(box, Pos.CENTER);
            ScrollPane scrollPane = new ScrollPane(stackPane);

            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);

            return new DetailUI(scrollPane);
        }
        else {
            Text text = new Text("Working");
            VBox box = new VBox(text);
            box.setAlignment(Pos.CENTER);
            StackPane stackPane = new StackPane(box);
            stackPane.setAlignment(box, Pos.CENTER);
            ScrollPane scrollPane = new ScrollPane(stackPane);

            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);

            return new DetailUI(scrollPane);
        }

         */

        // 2. Étapes du voyage (étape à pied, étape en transport)

        GridPane gridPane = new GridPane();

        gridPane.add(new Label("Heures D/D+A + icône    "), 0, 0);
        gridPane.add(new Label("Cercles    "),                1, 0);
        gridPane.add(new Label("Nom des gares   "),          2, 0);
        gridPane.add(new Label("Voies/Quais"),            3, 0);

        List <Journey.Leg> listOfLegs = observableValue.getValue().legs();


        listOfLegs.forEach(leg -> {

            switch  (leg) {
                case Journey.Leg.Foot foot -> {
                    int row = 1;
                    gridPane.add(new Label("07:00 → 07:10 🥾"),      0, row);
                    gridPane.add(new Label("●        ●"),            1, row);
                    gridPane.add(new Label("EPFL → Lausanne"),       2, row);
                    gridPane.add(new Label("—        —"),            3, row);
                }
                case Journey.Leg.Transport transport -> {
                    if(transport.intermediateStops().isEmpty()){
                        int row = 3;
                        gridPane.add(new Label("07:00 → 07:10 "),      0, 1);
                        gridPane.add(new Label("●        "),            1, 1);
                        gridPane.add(new Label("EPFL "),       2, 1);
                        gridPane.add(new Label("dgdgdg        —"),            3, 1);

                        gridPane.add(new Label("07:00 → 07:10 "),      0, 2);
                        gridPane.add(new Label("●        "),            1, 3);
                        gridPane.add(new Label("EPFL "),       2, 2);
                        gridPane.add(new Label("dgdgdg        —"),            3, 2);

                        gridPane.add(new Label("07:00 → 07:10 "),      0, row);
                        gridPane.add(new Label("●        "),            1, row);
                        gridPane.add(new Label("EPFL "),       2, row);
                        gridPane.add(new Label("dgdgdg        —"),            3, row);
                    }
                    else {
                        int row = 4;
                        gridPane.add(new Label("07:00 → 07:10 🥾"),      0, row);
                        gridPane.add(new Label("●        ●"),            1, row);
                        gridPane.add(new Label("EPFL → Lausanne"),       2, row);
                        gridPane.add(new Label("— sdgdsg       —"),            3, row);
                    }

                }
            }
        });

        StackPane stackPane2 = new StackPane(gridPane);
        VBox box = new VBox(stackPane2);
        StackPane stackPane = new StackPane(box);
        ScrollPane scrollPane = new ScrollPane(stackPane);

        return new DetailUI(scrollPane);

    }
}
