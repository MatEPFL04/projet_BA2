package ch.epfl.rechor.timetable;

/**
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 * Interface representing all the train connections. Extending the interface Indexed,
 * as connections form a list in the swiss timetable.
 */

public interface Connections extends Indexed {

    /**
     *
     * @param id, the index of the connection, from which we want to evaluate the starting stop station
     * @return the index of the departure station
     */
    int depStopId(int id);

    /**
     *
     * @param id, the index of the connection, from which we want to evaluate the starting stop station
     * @return the departure time (in minutes from midnight)
     */
    int depMins(int id);

    /**
     *
     * @param id, the index of the connection, from which we want to evaluate the starting stop station
     * @return the arrival stop of the given connection index
     */
    int arrStopId(int id);

    /**
     *
     * @param id, the index of the connection, from which we want to evaluate the starting stop station
     * @return the arrival time (in minutes from midnight)
     */
    int arrMins(int id);

    /**
     *
     * @param id, the index of the connection, from which we want to evaluate the starting stop station
     * @return the idx of the trip
     */
    int tripId(int id);

    /**
     *
     * @param id, the index of the connection, from which we want to evaluate the starting stop station
     * @return the position of the trip
     */
    int tripPos(int id);

    /**
     *
     * @param id, the index of the connection, from which we want to evaluate the next connection id
     * @return the index of the next connection in the journey,
     * or the first index if the given index was the last connection of the journey
     */
    int nextConnectionId(int id);
}
