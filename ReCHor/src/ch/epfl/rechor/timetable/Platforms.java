package ch.epfl.rechor.timetable;

/**
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 * interface representing indexed platform from the swiss timetable,
 */

public interface Platforms extends Indexed {
    /**
     *
     * @param id, the index of the platform in the indexed list
     * @return the name of the platform with the given id
     */
    String name(int id);

    /**
     * method used to return the stationId
     * @param id the platform id
     * @return the index of the station
     */
    int stationId(int id);

}
