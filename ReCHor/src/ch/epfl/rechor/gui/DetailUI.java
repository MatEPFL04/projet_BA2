package ch.epfl.rechor.gui;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.JourneyGeoJsonConverter;
import ch.epfl.rechor.journey.JourneyIcalConverter;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.time.LocalDate;
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
     *
     *
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
        LocalDate localDate = LocalDate.from(listOfLegs.getFirst().depTime());
        List<Circles> pairsOfCircle = new ArrayList<>();

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
                    rowIndex++;

                    Circles pair = new Circles(circle, secondCircle);
                    pairsOfCircle.add(pair);
                }
            }
        };

        GridPane pane = new GridPane () {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                List<Line> listOfLines = new ArrayList<>();
                for (int i = 0; i < pairsOfCircle.size(); i++) {
                    Line line = getLineFromCircle(pairsOfCircle.get(i));
                    listOfLines.add(line);
                }
                getChildren().setAll(listOfLines);
            }
        };

        pane.setId("annotations");

        StackPane journeyStackPane = new StackPane(pane, gridPane);

        Button calendar = new Button("Calendrier");
        Button map = new Button("Carte");
        HBox hBox = new HBox(calendar,map);
        hBox.setId("buttons");

        calendar.setOnAction(calendarEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName("voyage_" + localDate + ".ics");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("iCalendar files", "*.ics"));

            File selectedFile = fileChooser.showSaveDialog(null);
            try {
                Files.writeString(selectedFile.toPath(), JourneyIcalConverter.toIcalendar(new Journey(listOfLegs)));
            } catch (IOException e) {
                throw new Error(e);
            }
        });

        map.setOnAction(mapEvent -> {
            try {
                URI url = new URI("https", "umap.osm.ch", "/fr/map",
                        "data=" + JourneyGeoJsonConverter.toGeoJson(new Journey(listOfLegs)), null);
                Desktop.getDesktop().browse(url);
            } catch (Exception e) {
                throw new Error(e);
            }
        });

        VBox journeyVBox = new VBox(journeyStackPane, hBox);
        noJourneyVBox.setVisible(false);
        StackPane mainStackPane = new StackPane(noJourneyVBox, journeyVBox);
        ScrollPane scrollPane = new ScrollPane(mainStackPane);
        scrollPane.setId("detail");
        scrollPane.getStylesheets().add("detail.css");

        return new DetailUI(scrollPane);
    }

    private static Line getLineFromCircle(Circles circles) {
        Bounds firstCircleBound = circles.depCircle().getBoundsInParent();
        Bounds secondCircleBound = circles.arrCircle().getBoundsInParent();
        Line line = new Line(firstCircleBound.getCenterX(), firstCircleBound.getCenterY(),
                secondCircleBound.getCenterX(), secondCircleBound.getCenterY());
        line.setStrokeWidth(2);
        line.setStroke(Color.RED);
        return line;
    }
}