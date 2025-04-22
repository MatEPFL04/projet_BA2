package ch.epfl.rechor.gui;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.journey.Journey;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
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

        // 2. Étapes du voyage

        GridPane gridPane = new GridPane();
        List <Journey.Leg> listOfLegs = observableValue.getValue().legs();

        int rowIndex = 0;
        for (Journey.Leg leg : listOfLegs) {
            switch  (leg) {
                case Journey.Leg.Foot foot -> {
                    gridPane.add(new Label(FormatterFr.formatLeg(foot)), 2,rowIndex, 2, 1);
                    rowIndex++;
                }
                case Journey.Leg.Transport transport -> {

                    gridPane.addRow(rowIndex, new Label(FormatterFr.formatTime(transport.depTime())),
                            new Circle(3),
                            new Label(transport.depStop().name()),
                            new Label(FormatterFr.formatPlatformName(transport.depStop())));
                    rowIndex++;

                    gridPane.add(new ImageView((VehicleIcons.iconFor(transport.vehicle()))),
                            2, rowIndex, 2, 1);
                    rowIndex++;

                    if(!(transport.intermediateStops().isEmpty())){
                        String titleOfMenuDeroulant = transport.intermediateStops().size()
                                + " arrêts,  " + FormatterFr.formatDuration(transport.duration());
                        StringBuilder intermediateStopsString = new StringBuilder();
                        transport.intermediateStops().forEach(intermediateStop  -> {
                            intermediateStopsString.append(FormatterFr.formatTime(intermediateStop.arrTime()))
                                    .append(" ")
                                    .append(FormatterFr.formatTime(intermediateStop.depTime()))
                                    .append(" ")
                                    .append(intermediateStop.stop().name())
                                    .append("\n");
                        });
                        TitledPane pane = new TitledPane();
                        pane.setText(titleOfMenuDeroulant);
                        pane.setContent(new Label(intermediateStopsString.toString()));

                        gridPane.add(new Accordion(pane), 2, rowIndex, 2, 1);
                        rowIndex++;
                    }

                    gridPane.addRow(rowIndex, new Label(FormatterFr.formatTime(transport.arrTime())),
                            new Circle(3),
                            new Label(transport.arrStop().name()),
                            new Label(FormatterFr.formatPlatformName(transport.arrStop())));
                    rowIndex++;
                }
            }
        };


        StackPane stackPane2 = new StackPane(gridPane);
        VBox box = new VBox(stackPane2);
        StackPane stackPane = new StackPane(box);
        ScrollPane scrollPane = new ScrollPane(stackPane);

        return new DetailUI(scrollPane);
    }
}