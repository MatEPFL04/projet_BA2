package ch.epfl.rechor.journey;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.IcalBuilder;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MyJourneyIcalConverterTest {
    private static List<Journey.Leg> exampleLegs() {
        var s1 = new Stop("Ecublens VD, EPFL", null, 6.566141, 46.522196);
        var s2 = new Stop("Renens VD, gare", null, 6.578519, 46.537619);
        var s3 = new Stop("Renens VD", "4", 6.578935, 46.537042);
        var s4 = new Stop("Lausanne", "5", 6.629092, 46.516792);
        var s5 = new Stop("Lausanne", "1", 6.629092, 46.516792);
        var s6 = new Stop("Romont FR", "2", 6.911811, 46.693508);

        var d = LocalDate.of(2025, Month.FEBRUARY, 18);
        var l1 = new Journey.Leg.Transport(
                s1,
                d.atTime(16, 13),
                s2,
                d.atTime(16, 19),
                List.of(),
                Vehicle.METRO,
                "m1",
                "Renens VD, gare");

        var l2 = new Journey.Leg.Foot(s2, d.atTime(16, 19), s3, d.atTime(16, 22));

        var l3 = new Journey.Leg.Transport(
                s3,
                d.atTime(16, 26),
                s4,
                d.atTime(16, 33),
                List.of(),
                Vehicle.TRAIN,
                "R4",
                "Bex");

        var l4 = new Journey.Leg.Foot(s4, d.atTime(16, 33), s5, d.atTime(16, 38));

        var l5 = new Journey.Leg.Transport(
                s5,
                d.atTime(16, 40),
                s6,
                d.atTime(17, 13),
                List.of(),
                Vehicle.TRAIN,
                "IR15",
                "Luzern");

        return List.of(l1, l2, l3, l4, l5);
    }



    @Test
    void testToIcalendar_WithValidJourney() {

        LocalDateTime depTime = LocalDateTime.of(2025, 2, 26, 10, 0);
        LocalDateTime footArrTime = LocalDateTime.of(2025, 2, 26, 10, 5);
        LocalDateTime transportDepTime = footArrTime;
        LocalDateTime transportArrTime = LocalDateTime.of(2025, 2, 26, 10, 50);
        LocalDateTime finalFootDepTime = transportArrTime;
        LocalDateTime finalFootArrTime = LocalDateTime.of(2025, 2, 26, 10, 55);

        Stop depStop = new Stop("Station A", "voie 4", 0, 0);
        Stop midStop = new Stop("Station Mid", "voie 2", 0, 0);
        Stop arrStop = new Stop("Station B", "voie 3", 0, 0);


        Journey.Leg.Foot footLeg1 = new Journey.Leg.Foot(depStop, depTime, midStop, footArrTime);
        Journey.Leg.Transport transportLeg = new Journey.Leg.Transport(
                midStop, transportDepTime, arrStop, transportArrTime, List.of(), Vehicle.TRAIN, "Train Line X", "Destination");
        Journey.Leg.Foot footLeg2 = new Journey.Leg.Foot(arrStop, finalFootDepTime, arrStop, finalFootArrTime);

        Journey journey = new Journey(List.of(footLeg1, transportLeg, footLeg2));


        String ical = JourneyIcalConverter.toIcalendar(journey);
        System.out.println(ical);
        assertTrue(ical.contains("BEGIN:VCALENDAR"));
        assertTrue(ical.contains("VERSION:2.0"));
        assertTrue(ical.contains("PRODID:ReCHor"));
        assertTrue(ical.contains("BEGIN:VEVENT"));
        assertTrue(ical.contains("DTSTART:20250226T100000"));
        assertTrue(ical.contains("DTEND:20250226T105500"));
        assertTrue(ical.contains("SUMMARY:Station A → Station B"));
        assertTrue(ical.contains("END:VEVENT"));
        assertTrue(ical.contains("END:VCALENDAR"));

    }


    @Test
    void testFormatDescription() {
        LocalDateTime depTime = LocalDateTime.of(2025, 2, 26, 10, 0);
        LocalDateTime footArrTime = LocalDateTime.of(2025, 2, 26, 10, 5);
        LocalDateTime transportDepTime = LocalDateTime.of(2025, 2, 26, 10, 10);
        LocalDateTime transportArrTime = LocalDateTime.of(2025, 2, 26, 10, 15);

        Stop depStop = new Stop("Station A", "4", 0, 0);
        Stop midStop = new Stop("Station Mid", "2", 0, 0);
        Stop arrStop = new Stop("Station B", "3", 0, 0);

        Journey.Leg.Foot footLeg1 = new Journey.Leg.Foot(depStop, depTime, midStop, footArrTime);
        Journey.Leg.Transport transportLeg = new Journey.Leg.Transport(
                midStop, transportDepTime, arrStop, transportArrTime, List.of(), Vehicle.TRAIN, "Train Line X", "Destination");

        Journey journey = new Journey(List.of(footLeg1, transportLeg));

        String ical = JourneyIcalConverter.toIcalendar(journey);

        assertTrue(ical.contains("DESCRIPTION:"));
        assertTrue(ical.contains("Station A → Station B"));
        assertTrue(ical.contains("trajet à"));

    }
}
