package ch.epfl.rechor.timetable;

/**
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 * Interface used to represent indexed data from the swiss timetable, designed to
 * be extended by all interfaces that handle such data.
 */

public interface Indexed {

    /**
     * method returning the size of the indexed data
     * @return the size of the list
     */
    int size ();
}
