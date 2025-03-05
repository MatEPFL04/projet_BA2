package ch.epfl.rechor.timetable;

import ch.epfl.rechor.journey.Vehicle;

/**
 * interface reoresenting the routes from the swiss timetable.
 */

public interface Routes extends Indexed {
    /**
     * method returning the vehicule of the route with the given index
     * @param id, given index
     * @return the vehicule corresponding to the given index
     */
    Vehicle vehicle(int id);

    /**
     * method returning the name of the route with the given index
     * @param id given index
     * @return the name (string), corresponding to the given index
     */
    String name(int id);

}
