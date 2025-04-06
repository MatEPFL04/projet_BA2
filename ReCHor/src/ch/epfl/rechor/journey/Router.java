package ch.epfl.rechor.journey;

import ch.epfl.rechor.PackedRange;
import ch.epfl.rechor.timetable.Connections;
import ch.epfl.rechor.timetable.Stations;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.Transfers;

import static ch.epfl.rechor.journey.PackedCriteria.*;
import static ch.epfl.rechor.journey.ParetoFront.Builder;

import java.sql.SQLOutput;
import java.time.LocalDate;
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
     *
     * @param date the date for which the optimal journeys must be computed
     * @param arrStationId arrival station id
     * @return optimal's journeys profiles
     */
    public Profile profile (LocalDate date, int arrStationId) {
        System.out.println("START");

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

        Builder connectionParetoFront = new Builder();
        Builder stationBuilder;
        Builder tripsBuilder;
        int count = 0;

        for (int i = 0; i < connections.size(); i++) {

           int connectionDepStationId = timeTable.stationId(connections.depStopId(i));
           int connectionArrStationId = timeTable.stationId(connections.arrStopId(i));
           int connectionDepMins = connections.depMins(i);
           int connectionArrMins = connections.arrMins(i);
           int tripId = connections.tripId(i);


           //option 1

            if (arrayOfTransfersDuration[connectionArrStationId] != -1){

                System.out.println("option 1");

                long tuple = PackedCriteria.pack(connectionArrMins +
                        arrayOfTransfersDuration[connectionArrStationId], 0, 0);

                connectionParetoFront.add(tuple);
            }

            //option 2
            tripsBuilder = profile.forTrip(tripId);
            if (tripsBuilder != null) {

                connectionParetoFront.addAll(tripsBuilder);
            }

            //option 3

            stationBuilder = profile.forStation(connectionArrStationId);
            if (stationBuilder != null){
                System.out.println("option 3");
                stationBuilder.forEach((long criteria) -> {

                if((depMins(criteria) >= connectionArrMins)) {
                    long packedTuple = pack(arrMins(criteria), changes(criteria) + 1, 0);
                    connectionParetoFront.add(packedTuple);

                }
            });}

            //mise à jour de la frontière de la liaison

            if (tripsBuilder != null) {
                System.out.println("mise à jour de la frontière de la liaison");
                tripsBuilder.addAll(connectionParetoFront);
            }


            //mise à jour des frontières des gares

            int firstChangeToDepStation = PackedRange.startInclusive
                    (transfers.arrivingAt(connectionDepStationId));
            int lastChangeToDepStation = PackedRange.endExclusive(
                    transfers.arrivingAt(connectionDepStationId)) - 1;

            for (int j = firstChangeToDepStation; j <= lastChangeToDepStation; j++) {

                int transferId = j;
                int d = connectionDepMins - transfers.minutes(j);

                connectionParetoFront.forEach( (long criteria) -> {

                    //on y entre

                    long tupleWithoutDepMins = PackedCriteria.pack(arrMins(criteria),changes(criteria),0);
                    long addedTuple = PackedCriteria.withDepMins(tupleWithoutDepMins, d);
                    if (profile.forStation(transfers.depStationId(transferId)) != null) {
                        System.out.println("added for: " + transfers.depStationId(transferId));
                        (profile.forStation(transfers.depStationId(transferId))).add(addedTuple);
                    }

                });
            }

            connectionParetoFront.clear();
        }
        System.out.println("DONE");
        return profile.build();
    }
}
