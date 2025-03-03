package ch.epfl.rechor.timetable;

import java.time.LocalDate;

public interface TimeTable {
    Stations stations() throws IndexOutOfBoundsException;
    StationAliases stationAliases();
    Platforms platforms();
    Routes routes();
    Transfers transfers();
    Trips tripsFor(LocalDate date);
    Connections connectionsFor(LocalDate date);

    default boolean isStationId(int stopId) {
        return stopId < stations().size();
    }

    default boolean isPlatformId(int stopId) {
        return stopId >= stations().size();

    }

    default int stationId(int stopId) {
        if (isPlatformId(stopId)) {
            int idxPlatform = stopId - stations().size();
            return platforms().stationId(idxPlatform);
        }
        else {
            return stopId;
        }
    }

    default String platformName(int stopId) {
        int idxPlatform = stopId - stations().size();
        return isPlatformId(stopId) ? platforms().name(idxPlatform) : null;
    }

}
