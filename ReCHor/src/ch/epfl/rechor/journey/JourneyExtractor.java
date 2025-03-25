package ch.epfl.rechor.journey;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ch.epfl.rechor.FormatterFr;
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
        String platform = timeTable.isPlatformId(stopId) ? timeTable.platformName(stopId) : "";
        return new Stop(timeTable.stations().name(depStationId),
                platform,
                timeTable.stations().longitude(depStationId), timeTable.stations().latitude(depStationId));
    }

    private static Leg newFootLeg(TimeTable timetable, LocalDate date,
                                           int startingMinutes, int startStopId, int arrStopId) {

        int startStationId = timetable.stationId(startStopId);
        int arrStationId = timetable.stationId(arrStopId);

        int minutesByFoot = timetable.transfers().minutesBetween(startStationId, arrStationId);
        LocalDateTime depTime = dateFromMins(startingMinutes, date);
        int arrTimeMinutes = startingMinutes + minutesByFoot;
        LocalDateTime arrTime = dateFromMins(arrTimeMinutes, date);
        Stop depStop = newStop(timetable, startStopId);
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

            int journeyArrStationId = profile.arrStationId();

            List<Journey.Leg> currentLegs = new ArrayList<>();

            int connectionID = unpack24(payload(criteria));
            int connectionDepStopId = connections.depStopId(connectionID);
            int connectionDepStationId = timeTable.stationId(connectionDepStopId);
            int connectionArrStopId = connections.arrStopId(connectionID);
            int connectionArrStationId = timeTable.stationId(connectionArrStopId);
            int connectionDepMins = connections.depMins(connectionID);
            int connectionArrMins = connections.arrMins(connectionID);
            int numberOfIntermediateStops = unpack8(payload(criteria));


            if (connectionDepStationId != depStationId) {
                Leg beginningLegFoot = newFootLeg(timeTable,
                        currentDate, journeydepMins, depStationId, connectionDepStationId);

                currentLegs.add(beginningLegFoot);
            }

            while (journeyChanges >= 0) {

                //System.out.println("journeyChanges (début boucle) = " + journeyChanges);

                List < IntermediateStop > intermediateStopList = new ArrayList<>();

                for (int i = 0; i < numberOfIntermediateStops; i++) {

                    int intermediateStopDepMins = connections.arrMins(connectionID);
                    LocalDateTime arrTime = dateFromMins(intermediateStopDepMins, currentDate);

                    connectionID = connections.nextConnectionId(connectionID);

                    int intermediateStopId = connections.depStopId(connectionID);
                    Stop stop = newStop(timeTable, intermediateStopId);

                    int intermediateStopArrMins = connections.depMins(connectionID);
                    LocalDateTime depTime = dateFromMins(intermediateStopArrMins, currentDate);

                    intermediateStopList.add(new IntermediateStop(stop,arrTime,depTime));
                    //System.out.println("name Stop: " + timeTable.stations().name(timeTable.stationId(intermediateStopId)));
                }

                Stop currentLegDepStop = newStop(timeTable, connectionDepStopId);

                LocalDateTime currentLegDepTime = dateFromMins(connectionDepMins, currentDate);

                connectionArrStopId  = connections.arrStopId(connectionID);
                connectionArrMins = connections.arrMins(connectionID);

                LocalDateTime currentLegArrTime = dateFromMins(connectionArrMins, currentDate);
                Stop currentLegArrStop = newStop(timeTable, connectionArrStopId);

                int tripId = connections.tripId(connectionID);
                int routeId = trips.routeId(tripId);
                String destination = trips.destination(tripId);
                Vehicle vehicle = routes.vehicle(routeId);
                String route = routes.name(routeId);


                Transport transportStep = new Transport(currentLegDepStop,
                       currentLegDepTime, currentLegArrStop, currentLegArrTime,
                        intermediateStopList, vehicle, route, destination);

                currentLegs.add(transportStep);
                //System.out.println("Transport: " + FormatterFr.formatLeg(transportStep));

                connectionArrStationId = timeTable.stationId(connectionArrStopId);

                journeyChanges--;

                if (journeyChanges >= 0) {

                    criteria = profile.forStation(connectionArrStationId).
                                get(journeyArrMins, journeyChanges);

                    connectionID = unpack24(payload(criteria));
                    numberOfIntermediateStops = unpack8(payload(criteria));

                    connectionDepStopId = connections.depStopId(connectionID);
                    connectionDepStationId = timeTable.stationId(connectionDepStopId);
                    connectionDepMins = connections.depMins(connectionID);


                    int durationFootStep = transfers.minutesBetween(connectionArrStationId, connectionDepStationId);
                    LocalDateTime endFootStepTime = dateFromMins(connectionArrMins + durationFootStep, currentDate);
                    Stop nextStop = newStop(timeTable, connectionDepStopId);

                    Foot footStep = new Foot(currentLegArrStop, currentLegArrTime, nextStop, endFootStepTime);
                    currentLegs.add(footStep);
                }


            }

            if (connectionArrStationId != journeyArrStationId)  {

                Leg endLegFoot = newFootLeg(timeTable,
                        currentDate, connectionArrMins, connectionArrStationId, journeyArrStationId);
                currentLegs.add(endLegFoot);

            }
            journeyList.add(new Journey(currentLegs));

        });

        journeyList.sort(Comparator
                .comparing(Journey::depTime)
                .thenComparing(Journey::arrTime));

        return journeyList;
    }
}



