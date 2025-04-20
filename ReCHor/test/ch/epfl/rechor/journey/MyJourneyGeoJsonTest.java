package ch.epfl.rechor.journey;

import ch.epfl.rechor.Json;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyJourneyGeoJsonTest {

    private static List<Journey.Leg> exampleLegs() {
        var lausanne = new Stop("Lausanne", "1", 6.62909, 46.51679);
        var palezieux = new Stop("Palezieux", "1", 6.83787, 46.54276);
        var romont = new Stop("Romont", "1", 6.91181, 46.69351);
        var fribourg = new Stop("Fribourg", "1", 7.15105, 46.80315);

        var d = LocalDate.of(2025, Month.FEBRUARY, 18);

        var l1 = new Journey.Leg.Foot(
                lausanne,
                d.atTime(16, 19),
                palezieux,
                d.atTime(16, 22)
        );

        var l2 = new Journey.Leg.Transport(
                palezieux,
                d.atTime(16, 26),
                romont,
                d.atTime(16, 33),
                List.of(),
                Vehicle.TRAIN,
                "R4",
                "Bex"
        );

        var l3 = new Journey.Leg.Foot(
                romont,
                d.atTime(16, 33),
                fribourg,
                d.atTime(16, 38)
        );


        return List.of(l1, l2, l3);
    }

    @Test
    public void jspTest() {
        Json toGeoJson = JourneyGeoJsonConverter.toGeoJson(new Journey(exampleLegs()));

        String expected = """
                {
                  "type": "LineString",
                  "coordinates": [
                    [6.62909, 46.51679],
                    [6.83787, 46.54276],
                    [6.91181, 46.69351],
                    [7.15105, 46.80315]
                  ]
                }
                """;

        assertEquals(expected.trim(), toGeoJson.toString().trim());
    }
}