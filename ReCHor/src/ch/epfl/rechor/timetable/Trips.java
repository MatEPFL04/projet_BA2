package ch.epfl.rechor.timetable;

/**
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 * Interface representing public transport trips in the swiss timetable.
 * All methods throw an IndexOutOfBoundsException if the given index
 * is out of range, meaning it is less than 0 or greater than or equal to size()
 */

public interface Trips extends Indexed {

    /**
     *
     * @param id
     * @return
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    int routeId(int id) throws IndexOutOfBoundsException;

    /**
     *
     * @param id
     * @return
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    String destination(int id) throws IndexOutOfBoundsException;
}
