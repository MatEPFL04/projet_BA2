package ch.epfl.rechor.timetable;

import java.time.LocalDate;

/**
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
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
     * the method checks whether the index given is a station index
     * @param stopId the stop id given
     * @return true, if the given index is a station id, false otherwise
     */
    default boolean isStationId(int stopId) {
        return stopId < stations().size();
    }

    //boollen? à méditer

    /**
     * the method checks whether the index given is a platform index
     *
     * @param stopId the stop id given
     * @return true, if the given index is a platfrom id, false otherwise
     */
    default boolean isPlatformId(int stopId) {
        return stopId >= stations().size();

    }

    /**
     * Station id, returns the index of the given stop id
     *
     * @param stopId the stop id
     * @return the id of the given platform.
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
     * Method returning the name of the platform of given id
     *
     * @param stopId the stop id
     * @return the platform name if the index concerns a platform, null otherwise
     */
    default String platformName(int stopId) {
        int idxPlatform = stopId - stations().size();
        return isPlatformId(stopId) ? platforms().name(idxPlatform) : null;
    }
}