package ch.epfl.rechor.gui;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.JourneyIcalConverter;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
            if (journeyList == null){
                listView.getItems().setAll(List.of());
            }
            else {
                listView.getItems().setAll(journeyList);
            }
        }));

        journeyTime.subscribe( newTime -> {
            for (Journey j : listView.getItems() ) {
                if (!newTime.isAfter(j.depTime().toLocalTime())) {
                    listView.getSelectionModel().select(j);
                    break;
                }
            }
        });


        listView.getStylesheets().add("summary.css");

        return new SummaryUI(listView, listView.getSelectionModel().selectedItemProperty());
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
        private final Pane centerPane;
        private final Line line;
        private final Group group;
        private static final double MARGIN = 5;


        private JourneyListCell(){
            this.textDirection = new Text();
            this.imageView = new ImageView();
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);
            this.routeHBox = new HBox(imageView, textDirection);
            routeHBox.getStyleClass().add("route");


            this.line = new Line();

            line.setStroke(Color.GRAY);
            this.group = new Group();

            this.centerPane  = new Pane(line, group) {

                @Override
                protected void layoutChildren() {
                    super.layoutChildren();
                    line.setStartX(MARGIN);
                    line.setStartY(10);
                    line.setEndX(centerPane.getWidth());
                    line.setEndY(10);
                    for (Node child : group.getChildren()) {
                        if (child instanceof Circle) {
                            double ratio = ((Number) child.getUserData()).doubleValue();
                            Circle circle = (Circle) child;
                            double circlePos = MARGIN  + ratio * (getWidth());
                            circle.setCenterY(10);
                            circle.setCenterX(circlePos);
                        }

                    }

                }
            };

            centerPane.setPrefSize(0,0);

            this.duration = new Text();
            this.durationHBox = new HBox(duration);
            durationHBox.getStyleClass().add("duration");

            this.depTime = new Text();
            depTime.getStyleClass().add("departure");
            this.arrTime = new Text();


            this.borderPane = new BorderPane(centerPane, routeHBox, arrTime, durationHBox, depTime);
            borderPane.getStyleClass().add("journey");
        }

        @Override
        protected void updateItem(Journey journey, boolean empty ) {
            super.updateItem(journey, empty);

            if (empty || journey == null) {
                setText(null);
                setGraphic(null);
            } else {
                Journey.Leg.Transport firstTransport = getFirstTransport(journey);

                long journeyDuration = journey.duration().toMinutes();
                LocalDateTime journeyDepTime = journey.depTime();


                depTime.setText(FormatterFr.formatTime(journey.depTime()));
                arrTime.setText(FormatterFr.formatTime(journey.arrTime()));
                imageView.setImage(VehicleIcons.iconFor(firstTransport.vehicle()));
                textDirection.setText(FormatterFr.formatRouteDestination(firstTransport));
                duration.setText(FormatterFr.formatDuration(journey.duration()));

                group.getChildren().clear();

                Circle depCircle = new Circle(3);
                depCircle.getStyleClass().add("dep-arr");
                depCircle.setUserData(0);
                Circle arrCircle = new Circle(3);
                arrCircle.getStyleClass().add("dep-arr");
                arrCircle.setUserData(0.97);

                group.getChildren().add(depCircle);
                group.getChildren().add(arrCircle);

                for (Journey.Leg leg : journey.legs()) {
                    if (leg instanceof Journey.Leg.Foot) {
                        LocalDateTime currentTime = leg.depTime();

                        double minutesElapsed = Duration.between(journeyDepTime, currentTime).toMinutes();
                        System.out.println("minutesElapsed = " + minutesElapsed);
                        System.out.println("/journeyduration " + journeyDuration);

                        Circle transferCircle = new Circle(3);
                        transferCircle.getStyleClass().add("transfer");
                        double ratioToAssign = minutesElapsed / journeyDuration;
                        System.out.println("ratioToAssign = " + ratioToAssign);
                        System.out.println("for :" + JourneyIcalConverter.toIcalendar(journey));

                        transferCircle.setUserData(ratioToAssign);
                        group.getChildren().add(transferCircle);

                    }
                }

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