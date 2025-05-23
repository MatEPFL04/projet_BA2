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
 *
 * @param rootNode
 * @param depStopO
 * @param arrStopO
 * @param date0
 * @param time0
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public record QueryUI(Node rootNode, ObservableValue<String> depStopO,
                      ObservableValue<String> arrStopO, ObservableValue<LocalDate> date0,
                      ObservableValue<LocalTime> time0) {

    private static final DateTimeFormatter STRING_TO_HOUR_MINUTES_USER =
            DateTimeFormatter.ofPattern("H:mm");
    private static final DateTimeFormatter STRING_TO_HOUR_MINUTES =
            DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Builds a QueryUI with stop, date and time selectors.
     *
     * @param stopIndex the index providing auto-completion for stop names
     * @return a new QueryUI ready to be inserted into the scene graph
     */
    public static QueryUI create(StopIndex stopIndex) {

        StopField departureStopField = StopField.create(stopIndex);
        StopField arrivalStopField = StopField.create(stopIndex);

        Label labelDeparture = new Label();
        labelDeparture.setText("Départ\u202f:");
        TextField depStopField = departureStopField.textField();
        depStopField.setPromptText("Nom de l'arrêt de départ");
        depStopField.setId("depStop");

        Label labelArrival = new Label();
        labelArrival.setText("Arrivée\u202f:");
        TextField arrStopField = arrivalStopField.textField();
        arrStopField.setPromptText("Nom de l'arrêt d'arrivée");

        Button button = new Button();
        button.setText("\u2194");
        button.setOnAction(e -> {
            String departure = depStopField.getText();
            String arrival = arrStopField.getText();
            departureStopField.setTo(arrival);
            arrivalStopField.setTo(departure);
        });

        HBox box1 = new HBox(labelDeparture, depStopField,button, labelArrival, arrStopField);

        Label labelDate = new Label();
        labelDate.setText("Date\u202f:");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setId("date");

        Label labelTime = new Label();
        labelTime.setText("Heure\u202f:");
        TextField textFieldTime = new TextField();
        textFieldTime.setId("time");

        LocalTimeStringConverter converter =
                new LocalTimeStringConverter(STRING_TO_HOUR_MINUTES, STRING_TO_HOUR_MINUTES_USER);

        TextFormatter<LocalTime> textFormatter = new TextFormatter<>(converter);
        textFormatter.setValue(LocalTime.now());
        textFieldTime.setTextFormatter(textFormatter);

        HBox box2 = new HBox(labelDate, datePicker, labelTime, textFieldTime);

        VBox vBox = new VBox(box1, box2);
        vBox.getStylesheets().add("query.css");

        return new QueryUI(vBox, departureStopField.stop0(), arrivalStopField.stop0()
                , datePicker.valueProperty(), textFormatter.valueProperty());
    }
}