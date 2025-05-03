package ch.epfl.rechor.gui;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.journey.Journey;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Record representing the list of journeys.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public record SummaryUI(Node rootNode, ObservableValue<Journey> selectedJourneyO) {

    public static SummaryUI create(ObservableValue<List<Journey>> listOfJourneys,
                                   ObservableValue<LocalTime> journeyTime){

        ListView<Journey> listView = new ListView<>();
        listView.setCellFactory(view -> new JourneyListCell());
        listOfJourneys.subscribe((journeyList -> {
            if(journeyList == null){
                listView.getItems().setAll(List.of());
            }
            else {
                listView.getItems().setAll(journeyList);
            }
        }));

        JourneyListCell journeyListCell = new JourneyListCell();

        listView.getStylesheets().add("summary.css");
        journeyListCell.borderPane.setLeft(listView);

        return new SummaryUI(journeyListCell.borderPane, listView.getSelectionModel().selectedItemProperty());
    }

    private static class JourneyListCell extends ListCell<Journey> {

        private final BorderPane borderPane;
        private final Text depTime;
        private final Text arrTime;
        private final HBox routeHBox;
        private final HBox durationHBox;
        private final Text duration;
        private final ImageView imageView;
        private final Text textDirection;
        private final Pane pane;

        private JourneyListCell(){
            this.textDirection = new Text();
            this.imageView = new ImageView();
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);
            this.routeHBox = new HBox(imageView, textDirection);
            routeHBox.getStyleClass().add("route");

            Circle circle = new Circle();
            circle.getStyleClass().add("dep-arr");

            Pane pane  = new Pane() {
                Line line = new Line();

                @Override
                protected void layoutChildren() {

                }
            };
            pane.setPrefSize(0,0);

            Group group = new Group(circle);
            Line line = new Line();
            this.pane = new Pane(line, group);

            this.duration = new Text();
            this.durationHBox = new HBox(duration);
            durationHBox.getStyleClass().add("duration");

            this.depTime = new Text();
            depTime.getStyleClass().add("departure");

            this.arrTime = new Text();

            this.borderPane = new BorderPane(pane, routeHBox, arrTime, durationHBox, depTime);
            borderPane.getStyleClass().add("journey");
        }

        @Override
        protected void updateItem(Journey item, boolean empty ) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                Journey.Leg.Transport firstTransport = getFirstTransport(item);

                depTime.setText(FormatterFr.formatTime(item.depTime()));
                arrTime.setText(FormatterFr.formatTime(item.arrTime()));
                imageView.setImage(VehicleIcons.iconFor(firstTransport.vehicle()));
                textDirection.setText(FormatterFr.formatRouteDestination(firstTransport));
                duration.setText(FormatterFr.formatDuration(item.duration()));

                List<Circle> listOfCircles = new ArrayList<>();
                listOfCircles.add()

                List<Journey.Leg.IntermediateStop> intermediateStopList = new ArrayList<>();
                for (Journey.Leg leg : item.legs()) {
                    intermediateStopList.addAll(leg.intermediateStops());}

            }
            setGraphic(borderPane);
        }
    };

    public static Journey.Leg.Transport getFirstTransport(Journey journey) {
        for (Journey.Leg leg : journey.legs()) {
            if (leg instanceof Journey.Leg.Transport) {
                return (Journey.Leg.Transport) leg;
            }
        }
        return null;
    }
}