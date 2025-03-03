package ch.epfl.rechor;

import ch.epfl.rechor.journey.Journey;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MyIcalBuilderTest {


    @Test
    void addmethodWorksWithSmallTexts () {
        IcalBuilder journeyCalendar = new IcalBuilder();
        journeyCalendar.add(IcalBuilder.Name.PRODID,"ReCHor");
        String expectedString = "PRODID:ReCHor\r\n";
        assertEquals(expectedString,journeyCalendar.build());
    }
    @Test
    void addmethodWorksWithBigTexts () {
        IcalBuilder journeyCalendar = new IcalBuilder();
        String textAdded = "Ceci est un test du déroulage des lignes lorsque les " +
                "caractères d'une ligne excèdent la limite de la norme icalendar de 75 caractères";
        journeyCalendar.add(IcalBuilder.Name.DTSTART,textAdded);
        String expectedString = "DTSTART:Ceci est un test du déroulage des lignes lorsque les caractères d'u" +"\r\n" + " " + "ne ligne excèdent la limite de la norme icalendar de 75 caractères" + "\r\n";

        assertEquals(expectedString,journeyCalendar.build());

    }


    @Test
    void exceptionThrownWhenAComponentIsNotEndedWhenBuilt() {
    IcalBuilder journeyCalendar = new IcalBuilder();
        assertThrows(IllegalArgumentException.class, () -> {
            journeyCalendar.begin(IcalBuilder.Component.VCALENDAR)
                    .begin(IcalBuilder.Component.VEVENT)
                    .end().build();
        });
    }

    @Test
    void exceptionThrownWhenNoComponentsStartedButEnded() {
        IcalBuilder journeyCalendar = new IcalBuilder();

        assertThrows(IllegalArgumentException.class, () -> {
            journeyCalendar.end();
        });
    }

    @Test
    void dateIsCorrectlyAddedAndFormatted() {
        IcalBuilder journeyCalendar = new IcalBuilder();
        var d = LocalDate.of(2025, Month.FEBRUARY, 18);
        LocalDateTime dateToFormat = d.atTime(20, 10,59);
        String expectedLineWhenNewDateIsAdded = "DTSTAMP:20250218T201059\r\n";
        journeyCalendar.add(IcalBuilder.Name.DTSTAMP, dateToFormat);
        assertEquals(expectedLineWhenNewDateIsAdded,journeyCalendar.build());
    }

    @Test
    void add() {
        String veryLongValue = "This is an even longer test value that exceeds 200 characters. " +
                "It should be folded correctly by the IcalBuilder method. We want to ensure that every line has 75 characters" +
                " or fewer and that the folding mechanism works as expected in this case.";
        IcalBuilder builder = new IcalBuilder();
        builder.add(IcalBuilder.Name.SUMMARY, veryLongValue);
        String result = builder.build();
        assertTrue(result.contains("SUMMARY:"), "The result does not contain the expected attribute name.");
    }

    // tests 2

    @Test
    void addLocalDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 3, 15, 14, 30, 0);
        String expectedFormattedDate = dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"));

        IcalBuilder builder = new IcalBuilder();
        builder.add(IcalBuilder.Name.DTSTART, dateTime);

        String result = builder.build();

        assertTrue(result.contains("DTSTART:" + expectedFormattedDate));
    }

    @Test
    void addShortString() {
        String shortValue = "Short summary without folding.";

        IcalBuilder builder = new IcalBuilder();
        builder.add(IcalBuilder.Name.SUMMARY, shortValue);

        String result = builder.build();

        assertTrue(result.contains("SUMMARY:" + shortValue));
    }


}