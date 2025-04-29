package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.CachedTimeTable;
import ch.epfl.rechor.timetable.Stations;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MyRouterTest {

    static int stationId(Stations stations, String stationName) {
        for (int i = 0; i < stations.size(); i++) {
            if (Objects.equals(stations.name(i), stationName)) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) throws IOException {

        long tStart = System.nanoTime();

        TimeTable timeTable =
                new CachedTimeTable(FileTimeTable.in(Path.of("timetable")));

        Stations stations = timeTable.stations();

        LocalDate date = LocalDate.of(2025, Month.APRIL, 1);
        int depStationId = stationId(stations, "Ecublens VD, EPFL");
        int arrStationId = stationId(stations, "Pully-Nord");
        Router router = new Router(timeTable);
        Profile profile = router.profile(date, arrStationId);


        Journey journey = JourneyExtractor
                .journeys(profile, depStationId)
                .get(32);
        System.out.println(JourneyIcalConverter.toIcalendar(journey));

        double elapsed = (System.nanoTime() - tStart) * 1e-9;
        System.out.printf("Temps écoulé : %.3f s%n", elapsed);
    }
}
