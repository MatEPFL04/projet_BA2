package ch.epfl.rechor.timetable;

/**
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */

public interface Trips extends Indexed {
    int routeId(int id);
    String destination(int id);
}
