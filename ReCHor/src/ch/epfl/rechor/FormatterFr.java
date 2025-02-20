package ch.epfl.rechor;

import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.Stop;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * The formatter class is a utility class that
 * ensures to get every text representation in the right format
 * @author Pamphil Nedev (380400)
 * @throws NullPointerException
 *      in case the name of the stop is null
 * @throws IllegalArgumentException
 *      in the case the location, consisting of the longitude, latitude is incorrect
 *
 */

public final class FormatterFr {

    /**
     * Private constructor to prevent instantiation
     */

    private FormatterFr() {}

    /**
     * Displays hours and minutes
     * @param duration
     * @return a string representing the formatted duration
     */

    public static String formatDuration(Duration duration) {

        long totalMinutes = duration.getSeconds()/60;
        long totalHours = totalMinutes/60;
        long remainingMinutes = totalMinutes % 60;

        if (totalMinutes == 0) {
            return "0 min";
        } else if (totalMinutes < 60) {
            return totalMinutes +" min";
        }
        else {
            return totalHours + " h " + remainingMinutes +" min";
        }
    }

    /**
     * Displays date time
     * @param dateTime
     * @return a string representing the formatted time
     */

    public static String formatTime(LocalDateTime dateTime) {

        DateTimeFormatter fmt = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.HOUR_OF_DAY)
                .appendLiteral('h')
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                .toFormatter();

        return fmt.format(dateTime);
    }

    /**
     * Displays platform name
     * @param stop
     * @return a string representing the formatted platform name or an empty string
     */

    public static String formatPlatformName(Stop stop) {

        String platformName = stop.platformName();

        if (platformName == null || platformName.isEmpty()) {
            return "";
        }

        char firstChar = platformName.charAt(0);

        if (Character.isDigit(firstChar)) {
            return "voie " + platformName;
        } else {
            return "quai " + platformName;
        }
    }

    /**
     * Displays a walking leg of a journey or a change
     * @param footLeg
     * @return a string representing the formatted walking leg
     */

    public static String formatLeg(Journey.Leg.Foot footLeg) {
        StringBuilder sb = new StringBuilder();

        if (footLeg.isTransfer()) {
            sb.append("changement");
        } else {
            sb.append("trajet à pied");
        }

        sb.append(" (").append(formatDuration(footLeg.duration())).append(")");
        return sb.toString();
    }

    /**
     * Displays a transport leg of a journey
     * @param leg
     * @return a string representing the formatted transport leg
     */

    public static String formatLeg(Journey.Leg.Transport leg) {
        StringBuilder sb = new StringBuilder();

        sb.append(formatTime(leg.depTime())).append(" ");
        sb.append(leg.depStop().name());

        String departurePlatform = formatPlatformName(leg.depStop());
        if (!departurePlatform.isEmpty()) {
            sb.append(" (").append(departurePlatform).append(")");
        }

        sb.append(" → ");
        sb.append(leg.arrStop().name());
        sb.append(" (arr. ").append(formatTime(leg.arrTime()));

        String arrivalPlatform = formatPlatformName(leg.arrStop());
        if (!arrivalPlatform.isEmpty()) {
            sb.append(" ").append(arrivalPlatform);
        }

        sb.append(")");
        return sb.toString();
    }

    /**
     * Displays the route destination
     * @param transportLeg
     * @return a string representing the formatted route destination
     */

    public static String formatRouteDestination(Journey.Leg.Transport transportLeg) {
        return transportLeg.route() + " Direction " + transportLeg.destination();

    }
}
