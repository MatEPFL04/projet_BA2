package ch.epfl.rechor.timetable;

import java.util.NoSuchElementException;

/**
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */

public interface Transfers extends Indexed {
    int depStationId(int id);
    int minutes(int id);
    int arrivingAt(int stationId);
    int minutesBetween(int depStationId, int arrStationId) throws NoSuchElementException;
}
