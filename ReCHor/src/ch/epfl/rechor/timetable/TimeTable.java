package ch.epfl.rechor.timetable;

import java.time.LocalDate;

/**
 * The interface Time table.
 */
public interface TimeTable {
    /**
     * Stations stations.
     *
     * @return the stations
     * @throws IndexOutOfBoundsException the index out of bounds exception
     */
    Stations stations() throws IndexOutOfBoundsException;

    /**
     * Station aliases station aliases.
     *
     * @return the station aliases
     */
    StationAliases stationAliases();

    /**
     * Platforms platforms.
     *
     * @return the platforms
     */
    Platforms platforms();

    /**
     * Routes routes.
     *
     * @return the routes
     */
    Routes routes();

    /**
     * Transfers transfers.
     *
     * @return the transfers
     */
    Transfers transfers();

    /**
     * Trips for trips.
     *
     * @param date the date
     * @return the trips
     */
    Trips tripsFor(LocalDate date);

    /**
     * Connections for connections.
     *
     * @param date the date
     * @return the connections
     */
    Connections connectionsFor(LocalDate date);

    /**
     * Is station id boolean.
     *
     * @param stopId the stop id
     * @return the boolean
     */
    default boolean isStationId(int stopId) {
        return stopId < stations().size();
    }

    /**
     * Is platform id boolean.
     *
     * @param stopId the stop id
     * @return the boolean
     */
    default boolean isPlatformId(int stopId) {
        return stopId >= stations().size();

    }

    /**
     * Station id int.
     *
     * @param stopId the stop id
     * @return the int
     */
    default int stationId(int stopId) {
        if (isPlatformId(stopId)) {
            int idxPlatform = stopId - stations().size();
            return platforms().stationId(idxPlatform);
        }
        else {
            return stopId;
        }
    }

    /**
     * Platform name string.
     *
     * @param stopId the stop id
     * @return the string
     */
    default String platformName(int stopId) {
        int idxPlatform = stopId - stations().size();
        return isPlatformId(stopId) ? platforms().name(idxPlatform) : null;
    }

}
