package ch.epfl.rechor.gui;

import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.timetable.*;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.List;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        /*

        ObservableValue<Journey> journeyObservableValue;

        // Chargement des données horaires

        FileTimeTable timeTable = new FileTimeTable();
        timeTable.in(Path.of("timetable"));

        // Voyages observables


        // Center
        SummaryUI summaryUI = new SummaryUI(stage, journeyObservableValue);
        DetailUI detailUI = new DetailUI();
        SplitPane splitPane = new SplitPane();

         */

        // Top
        QueryUI queryUI = new QueryUI();



        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.show();
    }
}
