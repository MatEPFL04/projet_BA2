package ch.epfl.rechor.timetable;

import java.util.NoSuchElementException;

/**
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 * Interface representing indexed transfers.
 */

public interface Transfers extends Indexed {

    /**
     *
     * @param id
     * @return
     * @throws IndexOutOfBoundsException
     */
    int depStationId(int id) throws IndexOutOfBoundsException;

    /**
     *
     * @param id
     * @return
     * @throws IndexOutOfBoundsException
     */
    int minutes(int id) throws IndexOutOfBoundsException;

    /**
     *
     * @param stationId
     * @return
     * @throws IndexOutOfBoundsException
     */
    int arrivingAt(int stationId) throws IndexOutOfBoundsException;

    /**
     *
     * @param depStationId
     * @param arrStationId
     * @return
     * @throws NoSuchElementException
     * @throws IndexOutOfBoundsException
     */
    int minutesBetween(int depStationId, int arrStationId)
            throws NoSuchElementException, IndexOutOfBoundsException;
}
