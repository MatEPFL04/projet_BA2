package ch.epfl.rechor.timetable;

/**
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */

public interface StationAliases extends Indexed {
    String alias(int id);
    String stationName(int id);
}
