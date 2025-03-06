package ch.epfl.rechor.timetable;

/**
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 * Interface representing the indexed stations.
 * All methods throw an IndexOutOfBoundsException if the given index
 * is out of range, meaning it is less than 0 or greater than or equal to size()
 */

public interface Stations extends Indexed {

    /**
     * Name string.
     *
     * @param id the id
     * @return station's name
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    String name (int id) throws IndexOutOfBoundsException;

    /**
     *
     * @param id
     * @return longitude of the station in degrees
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    double longitude (int id) throws IndexOutOfBoundsException;

    /**
     *
     * @param id
     * @return latitude of the stations in degrees
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    double latitude (int id) throws IndexOutOfBoundsException;
}
