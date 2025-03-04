package ch.epfl.rechor.timetable;

public interface Stations extends Indexed {

    /**
     * Name string.
     *
     * @param id the id
     * @return the string
     * @throws IndexOutOfBoundsException the index out of bounds exception
     */
    String name (int id) throws IndexOutOfBoundsException;
    double longitude (int id) throws IndexOutOfBoundsException;
    double latitude (int id) throws IndexOutOfBoundsException;
}
