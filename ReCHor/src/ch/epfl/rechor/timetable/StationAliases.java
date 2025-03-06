package ch.epfl.rechor.timetable;

/**
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 * Interface representing the alias of stations.
 * All methods throw an IndexOutOfBoundsException if the given index
 * is out of range, meaning it is less than 0 or greater than or equal to size()
 */

public interface StationAliases extends Indexed {

    /**
     *
     * @param id
     * @return alias of stations
     * @throws IndexOutOfBoundsException
     */
    String alias(int id) throws IndexOutOfBoundsException;

    /**
     *
     * @param id
     * @return name of the station to which correspond the alias name of a given index
     * @throws IndexOutOfBoundsException
     */
    String stationName(int id) throws IndexOutOfBoundsException;
}
