package ch.epfl.rechor.gui;

import ch.epfl.rechor.StopIndex;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.LocalTimeStringConverter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Query interface, helps the user to choose the departure and arrival stops for a certain time.
 */
public record QueryUI(Node rootNode, ObservableValue<String> depStopO,
                      ObservableValue<String> arrStopO, ObservableValue<LocalDate> date0,
                      ObservableValue<LocalTime> time0)
{


    public static QueryUI create(StopIndex stopIndex){

        // First Box :

        // Departure
        Label labelDeparture = new Label();
        labelDeparture.setText("Départ\u202f:");
        TextField depStopField = new TextField();
        depStopField.setPromptText("Nom de l'arrêt de départ");
        depStopField.setId("depStop");

        // Arrival
        Label labelArrival = new Label();
        labelArrival.setText("Arrivée\u202f:");
        TextField arrStopField = new TextField();
        arrStopField.setPromptText("Nom de l'arrêt d'arrivée");

        // Exchange
        Button button = new Button();
        button.setOnAction(e -> {
            String departure = depStopField.getText();
            String arrival = arrStopField.getText();
            depStopField.setText(arrival);
            arrStopField.setText(departure);
        });

        HBox box1 = new HBox(labelDeparture, depStopField, labelArrival, arrStopField, button);

        // Second Box :

        // Date
        Label labelDate = new Label();
        labelDate.setText("Date\u202f:");
        DatePicker datePicker = new DatePicker();
        datePicker.setId("date");

        // Time
        Label labelTime = new Label();
        labelTime.setText("Heure\u202f:");
        TextField textFieldTime = new TextField();
        textFieldTime.setId("time");

        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter timeToString = DateTimeFormatter.ofPattern("HH:mm");


        LocalTimeStringConverter converter = new LocalTimeStringConverter(format, timeToString);

        TextFormatter<LocalTime> timeFormatter = new TextFormatter<>(converter, LocalTime.now());

        depStopField.setTextFormatter(timeFormatter);
        timeFormatter.valueProperty().addListener(
                (obs, oldTime, newTime) -> {




                });
        ObservableValue<LocalTime> time0 = timeFormatter.valueProperty();

        HBox box2 = new HBox(labelTime, datePicker, labelDate, textFieldTime);

        // VBox

        VBox vBox = new VBox(box1, box2);
        vBox.getStylesheets().add("query.css");

        return new QueryUI(vBox, depStopField.textProperty(), arrStopField.textProperty()
                , datePicker.valueProperty(), time0);
    }
}
