package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.Connections;
import ch.epfl.rechor.timetable.Stations;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.Transfers;
import static ch.epfl.rechor.PackedRange.startInclusive;
import static ch.epfl.rechor.PackedRange.endExclusive;
import static ch.epfl.rechor.journey.ParetoFront.Builder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        Profile profile = new Profile(timeTable, date, arrStationId, paretoFronts);
        Stations changes = timeTable.stations();
        Connections connections = profile.connections();
        Transfers transfers = timeTable.transfers();
        for (int i = 0; i < connections.size(); i++) {
           Builder frontConnection = new Builder();
           int connectionArrStationId = timeTable.stationId(connections.arrStopId(i));
           int connectionArrMins = connections.arrMins(i);

           // Cas 1 : descendre à la fin de la liaison et marcher jusqu'à la destination finale
            if (startInclusive(transfers.arrivingAt(arrStationId)) <= i && (i < endExclusive(transfers.arrivingAt(i)))) {

                long tuple = PackedCriteria.pack(connectionArrMins +
                        transfers.minutesBetween(connectionArrStationId,arrStationId), 0, 0);

                frontConnection.add(tuple);
            }

            // Cas 2 : rester dans le véhicule afin de continuer avec la liaison suivante
            //if ()

                //connections.nextConnectionId(i))


                    //startInclusive(transfers.arrivingAt(arrStationId);

                long tuple = PackedCriteria.pack(connectionArrMins,0, 0);
                frontConnection.add(tuple);

            // Cas 3 : elle peut changer de véhicule à la fin de la liaison


            //startInclusive(transfers.arrivingAt(arrStationId))

            //long tuple = PackedCriteria.pack(connectionArrMins,1, 0);
            frontConnection.add(tuple);
        }
        return null;
    }
}
