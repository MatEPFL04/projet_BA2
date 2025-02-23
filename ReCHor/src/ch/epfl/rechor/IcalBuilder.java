package ch.epfl.rechor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Objects;

/**
 * builder class, used to construct events in an Icalendar format,
 *      that can then be exported as a .ics file
 */

public final class IcalBuilder {
    private final ArrayList<Component> components = new ArrayList<>();
    private final StringBuilder stringBuilder = new StringBuilder();

    /**
     * enum used to keep track of the opened Components
     */
    public enum Component {
        VCALENDAR,
        VEVENT;
    }

    /**
     * enum used to centralize all the possible names (at least for the ReCHor project)
     *      that can be used inside an Icalendar file
     */
    public enum Name {
        BEGIN,
        END,
        PRODID,
        VERSION,
        UID,
        DTSTAMP,
        DTSTART,
        DTEND,
        SUMMARY,
        DESCRIPTION;
    }

    /**
     *
     * @param name the left String displayed before the ":" in a .ics file
     * @param value the value associated to the name parameter in a .ics file
     * @return the current instantiation of IcalBuilder
     */

    public IcalBuilder add (Name name, String value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
        Preconditions.checkArgument(!value.isEmpty());
        stringBuilder.append(name).append(":").append(value).append("\r\n");

        return this;
    }

    /**
     * private method used to format a date for the .ics file
     * @param date the date that needs to be instantiated
     * @return the current instantiation of IcalBuilder
     */

    private String icsFormatDate (LocalDateTime date) {
        DateTimeFormatter fmt = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.YEAR)
                .appendValue(ChronoField.MONTH_OF_YEAR)
                .appendValue(ChronoField.DAY_OF_MONTH)
                .appendLiteral("T")
                .appendValue(ChronoField.HOUR_OF_DAY)
                .appendValue(ChronoField.MINUTE_OF_HOUR)
                .appendValue(ChronoField.SECOND_OF_DAY)
                .toFormatter();

        return date.format(fmt);

    }

    /**
     *
     * @param name the name of the line added
     * @param dateTime a date that needs to be added in the correct .ics format
     * @return the current instantiation of IcalBuilder
     */

    public IcalBuilder add (Name name, LocalDateTime dateTime) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(dateTime);
        stringBuilder.append(name)
                .append(":")
                .append(icsFormatDate(dateTime))
                .append("\n");

        return this;

    }

    /**
     * method used to create a line in a .ics file
     *      with the name "BEGIN" and the given component
     * @param component
     * @return the current instantiation of IcalBuilder
     */

    public IcalBuilder begin (Component component) {
        Objects.requireNonNull(component);
        stringBuilder.append(Name.BEGIN)
                .append(":")
                .append(component)
                .append("\n");


        components.add(component);
        return this;

    }

    /**
     * method used to close a component in a .ics file
     *      using the "END" name.
     * @return the current instantiation of IcalBuilder
     */

    public IcalBuilder end() {
        Preconditions.checkArgument(!components.isEmpty());
        Component lastComponentStarted = components.getLast();
        stringBuilder.append(Name.END)
                .append(":")
                .append(lastComponentStarted)
                .append("\n");

        components.removeLast();
        return this;

    }

    /**i
     * method used to transform the object IcalBuilder
     *      into a string, once the .ics file is finished
     * @throws IllegalArgumentException if a component has not been ended
     * @return the string associated to the stringbuilder IcalBuilder
     */
    public String build() {
        Preconditions.checkArgument(components.isEmpty());
        return stringBuilder.toString();

    }


}
