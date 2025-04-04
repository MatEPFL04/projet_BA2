package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.Connections;
import ch.epfl.rechor.timetable.Stations;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.Transfers;

import static ch.epfl.rechor.journey.PackedCriteria.*;
import static ch.epfl.rechor.journey.ParetoFront.Builder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This record represents a router, which is an object capable of getting
 * from any station to a given arrival station.
 *
 * @param timeTable the given timetable to use
 */
public record Router(TimeTable timeTable) {

    /**
     *
     * @param date
     * @param arrStationId
     * @return optimal's journeys profiles
     */
    public Profile profile (LocalDate date, int arrStationId) {

        List <ParetoFront> paretoFronts = new ArrayList<>();
        Profile.Builder profile = new Profile.Builder(timeTable, date, arrStationId);
        Stations stations = timeTable.stations();
        Connections connections = timeTable.connectionsFor(date);
        Transfers transfers = timeTable.transfers();

        int[] arrayOfTransfersDuration = new int[stations.size()];

        for (int i = 0; i < stations.size(); i++) {
            try {
                arrayOfTransfersDuration[i] = transfers.minutesBetween(i, arrStationId);
            } catch (NoSuchElementException e){
                arrayOfTransfersDuration[i] = -1;
            }
        }

        for (int i = 0; i < connections.size(); i++) {
           Builder frontConnection = new Builder();
           int connectionArrStationId = timeTable.stationId(connections.arrStopId(i));
           int connectionArrMins = connections.arrMins(i);
           int tripId = (connections.tripId(i));

            if (arrayOfTransfersDuration[connectionArrStationId] != -1){

                long tuple = PackedCriteria.pack(connectionArrMins +
                        arrayOfTransfersDuration[connectionArrStationId], 0, 0);

                frontConnection.add(tuple);
            }

            Builder tripBuilder = profile.forTrip(tripId);
            frontConnection.addAll(tripBuilder);

            Builder stationBuilder = profile.forStation(connectionArrStationId);
            stationBuilder.forEach((long criteria) -> {
                if(depMins(criteria) >= connectionArrMins) {
                    long packedTuple = pack(arrMins(criteria), changes(criteria) + 1, 0);
                    frontConnection.add(packedTuple);
                }
            });
        }
        return null;
    }
}
