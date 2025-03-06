package ch.epfl.rechor.timetable;

/**
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 * Interface representing indexed platform from the swiss timetable.
 * All methods throw an IndexOutOfBoundsException if the given index
 * is out of range, meaning it is less than 0 or greater than or equal to size()
 */

public interface Platforms extends Indexed {
    /**
     *
     * @param id
     * @return the name of the platform with the given id
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    String name(int id) throws IndexOutOfBoundsException;

    /**
     * @param id
     * @return the index of the station
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    int stationId(int id) throws IndexOutOfBoundsException;
}
