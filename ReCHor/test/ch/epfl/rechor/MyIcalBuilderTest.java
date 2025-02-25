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
    void exceptionThrownWhenAComponentIsNotEnded() {
        IcalBuilder journeyCalendar = new IcalBuilder();


        assertThrows(IllegalArgumentException.class, () -> {
            journeyCalendar.begin(IcalBuilder.Component.VCALENDAR)
                    .begin(IcalBuilder.Component.VEVENT)
                    .end();
        });
    }

    @Test
    void exceptionThrownWhenNoComponentsStartedButEnded() {
        IcalBuilder journeyCalendar = new IcalBuilder();

        assertThrows(IllegalArgumentException.class, () -> {
            journeyCalendar.add(IcalBuilder.Name.UID,LocalDateTime.now()).end();
        });
    }

    @Test
    void dateIsCorrectlyAddedAndFormatted() {
        IcalBuilder journeyCalendar = new IcalBuilder();
        var d = LocalDate.of(2025, Month.FEBRUARY, 18);
        LocalDateTime dateToFormat = d.atTime(20, 10,59);
        String expectedLineWhenNewDateIsAdded = "DTSTAMP:20250218T201059\n";
        journeyCalendar.add(IcalBuilder.Name.DTSTAMP, dateToFormat);
        assertEquals(expectedLineWhenNewDateIsAdded,journeyCalendar.build());
    }
}
