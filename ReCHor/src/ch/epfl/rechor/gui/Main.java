package ch.epfl.rechor.gui;

import ch.epfl.rechor.StopIndex;
import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.JourneyExtractor;
import ch.epfl.rechor.journey.Profile;
import ch.epfl.rechor.journey.Router;
import ch.epfl.rechor.timetable.*;
import ch.epfl.rechor.timetable.mapped.BufferedStations;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Main extends Application {

    private static int STAGE_MIN_WIDTH = 800;
    private static int STAGE_MIN_HEIGHT = 600;
    private ObservableValue <List<Journey>> observableJourneys;

    public static void main(String[] args) {
        launch(args);
    }

    static int stationId(Stations stations, String stationName) {
        for (int i = 0; i < stations.size(); i++) {
            if (Objects.equals(stations.name(i), stationName)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void start(Stage stage) throws Exception {

        Path pathToTimetable = Path.of("timetable_april");

        // Load timetable
        TimeTable  timetable = FileTimeTable.in(pathToTimetable);
        Stations stations = timetable.stations();
        StationAliases stationAliases = timetable.stationAliases();

        // Observable Journeys
        List<String> stopNames = new LinkedList<>();

        for (int i = 0; i < stations.size() ; i++) {
            stopNames.add(stations.name(i));
        }

        Map<String, String> alternativeStopNamesTable = new HashMap<>();
        for (int i = 0; i < stationAliases.size(); i++) {
            alternativeStopNamesTable.put(stationAliases.alias(i), stationAliases.stationName(i));
        }

        StopIndex stopIndex = new StopIndex(stopNames,alternativeStopNamesTable);

        QueryUI queryUI = QueryUI.create(stopIndex);

        ObservableValue<String> depStopObserved = queryUI.depStopO();
        ObservableValue<String> arrStopObserved = queryUI.arrStopO();
        ObservableValue<LocalDate> dateObserved = queryUI.date0();
        ObservableValue<LocalTime> timeObserved = queryUI.time0();

        CachedTimeTable cachedTimeTable = new CachedTimeTable(timetable);
        Router router = new Router(cachedTimeTable);

        observableJourneys = Bindings.createObjectBinding( () -> {
                    Profile profile = router.profile(dateObserved.getValue(),
                            stationId(stations, arrStopObserved.getValue()));
                    int depStationId = stationId(stations, depStopObserved.getValue());

                    return JourneyExtractor.journeys(profile, depStationId);
                },
            arrStopObserved, depStopObserved, dateObserved
        );

        // Main Window

        SummaryUI summaryUI = SummaryUI.create(observableJourneys, timeObserved);
        DetailUI detailUI = DetailUI.create(summaryUI.selectedJourneyO());

        SplitPane splitPane = new SplitPane(summaryUI.rootNode(), detailUI.rootNode());
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(splitPane);
        borderPane.setTop(queryUI.rootNode());

        Scene scene = new Scene(borderPane);
        stage.setScene(scene);

        stage.setMinWidth(STAGE_MIN_WIDTH);
        stage.setMinHeight(STAGE_MIN_HEIGHT);
        stage.setTitle("ReCHor");
        stage.show();

        Platform.runLater(() ->
                scene.lookup("#depStop").requestFocus());
    }
}
