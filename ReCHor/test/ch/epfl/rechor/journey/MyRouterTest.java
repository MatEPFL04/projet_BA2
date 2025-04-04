package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MyRouterTest {

    @Test
    void profileToGruyeresOnTuesdayContainsExpectedJourneysFromEPFL() throws IOException {
        TimeTable timeTable = FileTimeTable.in(Path.of("timetable"));
        Router router = new Router(timeTable);

        int depStationID = 7874;
        int arrStationID = 11486;

        LocalDate tuesday = LocalDate.of(2025, 3, 18);
        Profile profile = router.profile(tuesday, arrStationID);

        ParetoFront front = profile.forStation(depStationID);

        boolean foundExpectedTuple = front.toString().contains("(695,1)");
        assertTrue(foundExpectedTuple, "message");
    }
}
