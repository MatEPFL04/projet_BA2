package ch.epfl.rechor.journey;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import ch.epfl.rechor.timetable.*;
import ch.epfl.rechor.journey.Journey.Leg.Transport;
import ch.epfl.rechor.journey.Journey.Leg.IntermediateStop;
import ch.epfl.rechor.journey.Journey.Leg.Foot;
import ch.epfl.rechor.journey.Journey.Leg;
import static ch.epfl.rechor.Bits32_24_8.unpack24;
import static ch.epfl.rechor.Bits32_24_8.unpack8;
import static ch.epfl.rechor.journey.PackedCriteria.*;

/**
 *  @author Matteo Lazzari (397247)
 *  @author Pamphil Nedev (380400)
 */

public class JourneyExtractor {

    private JourneyExtractor() {}

    private static LocalDateTime dateFromMins
            //check the thing 4h before midnight
            (int minutes, LocalDate date) {
        LocalDateTime midnightDate = date.atStartOfDay();
        return midnightDate.plusMinutes(minutes);
    }

    private static Stop newStop(TimeTable timeTable, int stopId) {
        int depStationId = timeTable.stationId(stopId);
        return new Stop(timeTable.stations().name(depStationId),
                timeTable.platformName(depStationId),
                timeTable.stations().longitude(depStationId), timeTable.stations().latitude(depStationId));
    }

    private static Leg newFootLeg(TimeTable timetable, LocalDate date,
                                           int startingMinutes, int startStopId, int arrStopId) {

        int startStationId = timetable.stationId(startStopId);
        int minutesByFoot = timetable.transfers().minutesBetween(startStationId, arrStopId);
        LocalDateTime depTime = dateFromMins(startingMinutes, date);
        int arrTimeMinutes = startingMinutes + minutesByFoot;
        LocalDateTime arrTime = dateFromMins(arrTimeMinutes, date);
        Stop depStop = newStop(timetable, startStationId);
        Stop arrStop = newStop(timetable, arrStopId);
        return new Foot(depStop, depTime, arrStop, arrTime);
    }


    public static List <Journey> journeys(Profile profile, int depStationId) {

        LocalDate currentDate = profile.date();
        Connections connections = profile.connections();
        Transfers transfers = profile.timeTable().transfers();
        TimeTable timeTable = profile.timeTable();
        Trips trips = profile.trips();
        Routes routes = profile.timeTable().routes();
        ParetoFront paretoFront = profile.forStation(depStationId);
        List <Journey> journeyList = new ArrayList<>(paretoFront.size());

        paretoFront.forEach((long criteria) -> {



            int journeydepMins = depMins(criteria);
            int journeyChanges = changes(criteria);
            int journeyArrMins = arrMins(criteria);
            int journeyArrStopIdx = profile.arrStationId();

            List<Journey.Leg> currentLegs = new ArrayList<>();

            int firstConnectionId = unpack24(payload(criteria));
            int depStopId = connections.depStopId(firstConnectionId);
            int arrStopId = connections.arrStopId(firstConnectionId);

            if (depStopId != depStationId) {
                Leg beginningLegFoot = newFootLeg(timeTable,
                        currentDate, journeydepMins, depStationId, depStopId);
                currentLegs.add(beginningLegFoot);
            }

            long currentCriteria = criteria;

            while (journeyChanges > 0) {

                int numberOfIntermediateStops = unpack8(payload(currentCriteria));
                firstConnectionId = unpack24(payload(currentCriteria));
                int currentConnectionId = firstConnectionId;

                List < IntermediateStop > intermediateStopList = new ArrayList<>();
                for (int i = 0; i < numberOfIntermediateStops; i++) {

                    int arrMinsConnection = connections.arrMins(currentConnectionId);
                    LocalDateTime arrTime = dateFromMins(arrMinsConnection, currentDate);

                    currentConnectionId = connections.nextConnectionId(currentConnectionId);

                    int currentConnectionstopId = connections.depStopId(currentConnectionId);
                    Stop stop = newStop(timeTable,currentConnectionstopId);

                    int depMinsConnection = connections.depMins(currentConnectionId);
                    LocalDateTime depTime = dateFromMins(depMinsConnection, currentDate);

                    intermediateStopList.add(new IntermediateStop(stop,arrTime,depTime));
                }

                int lastConnectionId = currentConnectionId;
                arrStopId = connections.depStopId(firstConnectionId);
                int currentLegMinsTime = connections.depMins(firstConnectionId);
                Stop currentLegDepStop = newStop(timeTable, arrStopId);

                LocalDateTime currentLegDepTime = dateFromMins(currentLegMinsTime, currentDate);

                int currentLegArrStopidx = connections.arrStopId(lastConnectionId);
                int currentLegArrMins = connections.arrMins(lastConnectionId);
                LocalDateTime currentLegArrTime = dateFromMins(currentLegArrMins, currentDate);
                Stop currentLegArrStop = newStop(timeTable, currentLegArrStopidx);
                int routeId = unpack8(payload(criteria));
                Vehicle vehicle = routes.vehicle(routeId);
                String route = routes.name(routeId);
                String destination = trips.destination(routeId);
                Leg transportStep = new Transport(currentLegDepStop,
                       currentLegDepTime, currentLegArrStop, currentLegArrTime,
                        intermediateStopList, vehicle, route, destination);
                currentLegs.add(transportStep);



                currentCriteria = profile.forStation (timeTable.stationId(currentLegArrStopidx)).
                        get(journeyArrMins, --journeyChanges);


                int startStationId = timeTable.stationId(currentLegArrStopidx);
                int nextConnectionIdx = unpack24(payload(currentCriteria));
                int nextConnectionDepStopId = connections.depStopId(nextConnectionIdx);
                int nextConnectionDepStationId = timeTable.stationId(nextConnectionDepStopId);
                int durationFootStep = transfers.minutesBetween(startStationId, nextConnectionDepStationId);
                LocalDateTime endFootStepTime = dateFromMins(currentLegArrMins + durationFootStep, currentDate);
                Stop nextStop = newStop(timeTable, nextConnectionDepStationId);

                Foot footStep = new Foot(currentLegArrStop,currentLegArrTime,nextStop, endFootStepTime);
                currentLegs.add(footStep);
            }

            if (arrStopId != journeyArrStopIdx)  {
            }
            journeyList.add(new Journey(currentLegs));

        });


        journeyList.sort(Comparator
                .comparing(Journey::depTime)
                .thenComparing(Journey::arrTime));


        return journeyList;
    }
}



