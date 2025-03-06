package ch.epfl.rechor.timetable;

/**
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 * Interface representing all the train connections. Extending the interface Indexed,
 * as connections form a list in the swiss timetable.
 * All methods throw an IndexOutOfBoundsException if the given index
 * is out of range, meaning it is less than 0 or greater than or equal to size()
 */

public interface Connections extends Indexed {

    /**
     *
     * @param id, the index of the connection, from which we want to evaluate the starting stop station
     * @return the index of the departure station
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    int depStopId(int id) throws IndexOutOfBoundsException;

    /**
     *
     * @param id, the index of the connection, from which we want to evaluate the starting stop station
     * @return the departure time (in minutes from midnight)
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    int depMins(int id) throws IndexOutOfBoundsException;

    /**
     *
     * @param id, the index of the connection, from which we want to evaluate the starting stop station
     * @return the arrival stop of the given connection index
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    int arrStopId(int id) throws IndexOutOfBoundsException;

    /**
     *
     * @param id, the index of the connection, from which we want to evaluate the starting stop station
     * @return the arrival time (in minutes from midnight)
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    int arrMins(int id) throws IndexOutOfBoundsException;

    /**
     *
     * @param id, the index of the connection, from which we want to evaluate the starting stop station
     * @return the idx of the trip
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    int tripId(int id) throws IndexOutOfBoundsException;

    /**
     *
     * @param id, the index of the connection, from which we want to evaluate the starting stop station
     * @return the position of the trip
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    int tripPos(int id) throws IndexOutOfBoundsException;

    /**
     *
     * @param id, the index of the connection, from which we want to evaluate the next connection id
     * @return the index of the next connection in the journey,
     * or the first index if the given index was the last connection of the journey
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    int nextConnectionId(int id) throws IndexOutOfBoundsException;
}
