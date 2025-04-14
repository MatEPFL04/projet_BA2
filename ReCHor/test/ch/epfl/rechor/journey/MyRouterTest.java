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

    @Test
    void profileToGruyeresOnTuesdayContainsExpectedJourneysFromEPFL() throws IOException {
        TimeTable timeTable = FileTimeTable.in(Path.of("timetable"));
        Router router = new Router(timeTable);

        int depStationID = 7872;
        int arrStationID = 11486;

        LocalDate tuesday = LocalDate.of(2025, Month.MARCH, 18);
        Profile profile = router.profile(tuesday, arrStationID);

        ParetoFront front = profile.forStation(depStationID);

        //tuple qui arrive à 17h57 avec 4 changements à Gruyères, devrait avoir été crée...
        long tupleNeededToBeInFront = PackedCriteria.pack(17*60 + 57, 3, 0);
        System.out.println(front);
        assertEquals(tupleNeededToBeInFront, front.get(17*60 + 57, 3));


    }
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

        LocalDate date = LocalDate.of(2025, Month.MARCH, 18);
        int depStationId = stationId(stations, "Ecublens VD, EPFL");
        int arrStationId = stationId(stations, "Gruyères");
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
