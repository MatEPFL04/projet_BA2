package ch.epfl.rechor.journey;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.IcalBuilder;

import java.time.LocalDateTime;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * public class, not-instantiable,
 *      containing a unique static method used to transform a journey into a string formatted in .ics
 */

public class JourneyIcalConverter {
    /**
     * private constructor as the class is not-instantiable
     */
    private JourneyIcalConverter() {}

    /**
     *
     * @param journey the journey that needs to be converted into a string in the right format (.ics format)
     * @return a string corresponding to the journey expressed in an Icalendar format.
     */

    public static String toIcalendar (Journey journey) {

        IcalBuilder journeyCalendar = new IcalBuilder();

        String firstLegStopName = journey.legs().getFirst().depStop().name();
        String lastLegStopName = journey.legs().getLast().arrStop().name();
        StringBuilder sb = new StringBuilder();
        sb.append(firstLegStopName)
                .append(" -> ")
                .append(lastLegStopName);
        String summary = sb.toString();

        StringJoiner j = new StringJoiner("\\n");
        for (int i = 0; i < journey.legs().size(); i++) {
            switch (journey.legs().get(i)) {
                case Journey.Leg.Foot f -> j.add(FormatterFr.formatLeg(f));
                case Journey.Leg.Transport t -> j.add(FormatterFr.formatLeg(t));
            }
        }
        String description = j.toString();


        journeyCalendar.begin(IcalBuilder.Component.VCALENDAR)
                .add(IcalBuilder.Name.VERSION,"2.0")
                .add(IcalBuilder.Name.PRODID,"ReCHor")
                .begin(IcalBuilder.Component.VEVENT)
                .add(IcalBuilder.Name.UID,UUID.randomUUID().toString())
                .add(IcalBuilder.Name.DTSTAMP, LocalDateTime.now())
                .add(IcalBuilder.Name.DTSTART, journey.depTime())
                .add(IcalBuilder.Name.DTEND, journey.arrTime())
                .add(IcalBuilder.Name.SUMMARY,summary)
                .add(IcalBuilder.Name.DESCRIPTION, description)
                .end()
                .end();


        return journeyCalendar.build();
    }

}
