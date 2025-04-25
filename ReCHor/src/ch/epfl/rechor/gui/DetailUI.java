package ch.epfl.rechor.gui;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.ParetoFront;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

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


        // 1. Aucun voyage
        Text text = new Text("Aucun voyage");
        VBox noJourneyVBox = new VBox(text);
        noJourneyVBox.setId("no-journey");

        // 2. Étapes du voyage

        GridPane gridPane = new GridPane();
        gridPane.setId("legs");
        List <Journey.Leg> listOfLegs = observableValue.getValue().legs();
        List <Circle> circles = new ArrayList<>();

        int rowIndex = 0;
        for (Journey.Leg leg : listOfLegs) {
            switch  (leg) {
                case Journey.Leg.Foot foot -> {
                    Label changeLabel = new Label(FormatterFr.formatLeg(foot));
                    GridPane.setHalignment(changeLabel, HPos.LEFT);
                    gridPane.add(changeLabel, 2,rowIndex, 2, 1);
                    rowIndex++;
                }
                case Journey.Leg.Transport transport -> {

                    Label transportdepTimeLabel = new Label(FormatterFr.formatTime(transport.depTime()));
                    transportdepTimeLabel.getStyleClass().add("departure");
                    GridPane.setHalignment(transportdepTimeLabel, HPos.RIGHT);

                    Circle circle = new Circle(3);
                    GridPane.setHalignment(circle, HPos.LEFT);
                    circles.add(circle);

                    Label depStopNameLabel = new Label(transport.depStop().name());
                    GridPane.setHalignment(depStopNameLabel, HPos.LEFT);

                    Label depPlatformNameLabel = new Label(FormatterFr.formatPlatformName(transport.depStop()));
                    depPlatformNameLabel.getStyleClass().add("departure");
                    GridPane.setHalignment(depPlatformNameLabel, HPos.LEFT);

                    gridPane.addRow(rowIndex, transportdepTimeLabel,circle, depStopNameLabel,
                            depPlatformNameLabel);
                    rowIndex++;

                    ImageView icon = new ImageView(VehicleIcons.iconFor(transport.vehicle()));
                    icon.setFitHeight(31);
                    icon.setFitWidth(31);
                    GridPane.setValignment(icon, VPos.CENTER);
                    GridPane.setHalignment(icon, HPos.CENTER);
                    int rowSpan = transport.intermediateStops().isEmpty() ? 1 : 2;

                    gridPane.add(icon, 0, rowIndex, 1, rowSpan);
                    Label routeDestination = new Label(FormatterFr.formatRouteDestination(transport));
                    GridPane.setHalignment(routeDestination, HPos.LEFT);

                    gridPane.add(routeDestination, 2, rowIndex, 2,1);
                    rowIndex++;

                    if(!(transport.intermediateStops().isEmpty())){
                        String titleOfMenuDeroulant = transport.intermediateStops().size()
                                + " arrêts,  " + FormatterFr.formatDuration(transport.duration());
                        int row = 0;
                        GridPane intermediateStopGrid = new GridPane();
                        for (Journey.Leg.IntermediateStop intermediateStop : transport.intermediateStops()) {
                            Label arrTimeLabel = new Label(FormatterFr.formatTime(intermediateStop.arrTime()));
                            Label stopLabel = new Label(intermediateStop.stop().name());
                            Label depTimeLabel = new Label(FormatterFr.formatTime(intermediateStop.depTime()));
                            intermediateStopGrid.addRow(row, arrTimeLabel, depTimeLabel, stopLabel);
                            row++;
                        }
                        intermediateStopGrid.getStyleClass().add("intermediate-stops");
                        TitledPane titledPane = new TitledPane();
                        titledPane.setText(titleOfMenuDeroulant);
                        titledPane.setContent(intermediateStopGrid);
                        Accordion accordion = new Accordion(titledPane);
                        GridPane.setHalignment(accordion, HPos.LEFT);

                        gridPane.add(accordion, 2, rowIndex, 2, 1);
                        rowIndex++;
                    }

                    Label transportArrTimeLabel = new Label(FormatterFr.formatTime(transport.arrTime()));
                    GridPane.setHalignment(transportArrTimeLabel, HPos.RIGHT);
                    Circle secondCircle = new Circle(3);
                    GridPane.setHalignment(secondCircle, HPos.LEFT);
                    Label arrStopNameLabel = new Label(transport.arrStop().name());
                    GridPane.setHalignment(arrStopNameLabel, HPos.LEFT);
                    Label arrPlatformNameLabel = new Label(FormatterFr.formatPlatformName(transport.arrStop()));
                    GridPane.setHalignment(arrPlatformNameLabel, HPos.LEFT);
                    gridPane.addRow(rowIndex, transportArrTimeLabel,
                            secondCircle,
                            arrStopNameLabel,
                            arrPlatformNameLabel);
                    circles.add(secondCircle);
                    rowIndex++;
                }
            }
        };

        GridPane pane = new GridPane () {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                getChildren().removeIf(n -> n instanceof Line);
                for (int i = 1; i < circles.size(); i+=2) {

                    Bounds firstCircleBound = circles.get(i-1).getBoundsInParent();
                    Bounds secondCircleBound = circles.get(i).getBoundsInParent();
                    Line line = new Line(firstCircleBound.getCenterX(), firstCircleBound.getCenterY(),
                            secondCircleBound.getCenterX(), secondCircleBound.getCenterY());
                    line.setStrokeWidth(2);
                    line.setStroke(Color.RED);
                    getChildren().add(line);
                }
            }
        };
        pane.setId("annotations");

        StackPane journeyStackPane = new StackPane(pane,gridPane);
        Button calendar = new Button("Calendrier");
        Button map = new Button("Carte");
        HBox hBox = new HBox(calendar,map);
        hBox.setId("buttons");

        VBox journeyVBox = new VBox(journeyStackPane, hBox);
        noJourneyVBox.setVisible(false);
        StackPane mainStackPane = new StackPane(noJourneyVBox, journeyVBox);
        ScrollPane scrollPane = new ScrollPane(mainStackPane);
        scrollPane.setId("detail");
        scrollPane.getStylesheets().add("detail.css");


        return new DetailUI(scrollPane);

    }
}