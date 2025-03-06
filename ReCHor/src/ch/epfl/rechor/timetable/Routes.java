package ch.epfl.rechor.timetable;

import ch.epfl.rechor.journey.Vehicle;

/**
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 * Interface reoresenting the routes from the swiss timetable.
 * All methods throw an IndexOutOfBoundsException if the given index
 * is out of range, meaning it is less than 0 or greater than or equal to size()
 */

public interface Routes extends Indexed {

    /**
     * method returning the vehicule of the route with the given index
     * @param id, given index
     * @return the vehicule corresponding to the given index
     */
    Vehicle vehicle(int id) throws IndexOutOfBoundsException;

    /**
     * method returning the name of the route with the given index
     * @param id given index
     * @return the name (string), corresponding to the given index
     */
    String name(int id) throws IndexOutOfBoundsException;
}
