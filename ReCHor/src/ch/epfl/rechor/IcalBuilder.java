package ch.epfl.rechor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Objects;

public final class IcalBuilder {
    private final ArrayList<Component> components = new ArrayList<>();
    private final StringBuilder stringBuilder = new StringBuilder();
    public enum Component {
        VCALENDAR,
        VEVENT;
    }
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

    public IcalBuilder add (Name name, String value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
        Preconditions.checkArgument(name != Name.BEGIN && name != Name.END);
        Preconditions.checkArgument(!value.isEmpty());
        stringBuilder.append(name).append(":").append(value).append("\r\n");

        return this;
    }

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

    public IcalBuilder add (Name name, LocalDateTime dateTime) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(dateTime);
        Preconditions.checkArgument(name != Name.BEGIN && name != Name.END);
        stringBuilder.append(name)
                .append(":")
                .append(icsFormatDate(dateTime))
                .append("\n");

        return this;

    }

    public IcalBuilder begin (Component component) {
        Objects.requireNonNull(component);
        stringBuilder.append(Name.BEGIN)
                .append(":")
                .append(component)
                .append("\n");


        components.add(component);
        return this;

    }

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
    public String build() {
        return stringBuilder.toString();

    }


}
