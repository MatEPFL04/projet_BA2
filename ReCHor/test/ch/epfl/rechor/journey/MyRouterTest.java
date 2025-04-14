package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;

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
}
