package ch.epfl.rechor.journey;

import ch.epfl.rechor.PackedRange;
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

public final class Router {
    TimeTable timeTable;

    public Router (TimeTable timeTable) {
        this.timeTable = timeTable;
    }

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

           // cas 1
            if (startInclusive(transfers.arrivingAt(arrStationId)) <= i && (i < endExclusive(transfers.arrivingAt(i)))) {

                long tuple = PackedCriteria.pack(connectionArrMins +
                        transfers.minutesBetween(connectionArrStationId,arrStationId), 0, 0);

                frontConnection.add(tuple);
            }

            // cas 2: continuer avec liaison suivante


        }
        return null;



    }

}
