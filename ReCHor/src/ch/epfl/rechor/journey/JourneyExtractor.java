package ch.epfl.rechor.journey;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.timetable.TimeTable;


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


    private static Stop newStop(TimeTable timeTable, int depStationId) {
        return new Stop(timeTable.stations().name(depStationId),
                timeTable.platformName(depStationId),
                timeTable.stations().longitude(depStationId), timeTable.stations().latitude(depStationId));
    }

    private static Journey.Leg newFootStep(TimeTable timetable, LocalDate date,
                                           int startingMinutes, int actualStopId, int expectedStationId) {

        int actualStationId = timetable.stationId(actualStopId);
        int minutesByFoot = timetable.transfers().minutesBetween(actualStationId, expectedStationId);

        LocalDateTime depTime = dateFromMins(startingMinutes, date);

        int arrTimeMinutes = startingMinutes + minutesByFoot;
        LocalDateTime arrTime = dateFromMins(arrTimeMinutes, date);
        Stop depStop = newStop(timetable, actualStationId);
        Stop arrStop = newStop(timetable, expectedStationId);

        return new Journey.Leg.Foot(depStop, depTime, arrStop, arrTime);
    }


    public static List<Journey> journeys(Profile profile, int depStationId) {

        List<Journey> journeyList = new ArrayList<>();
        LocalDate currentDate = profile.date();

        ParetoFront paretoFront = profile.forStation(depStationId);

        paretoFront.forEach((long criteria) -> {

            List<Journey.Leg> currentListLegs = new ArrayList<>();

            // informations utiles du premier tuple (pour l'ENTIER du voyage)
            int journeydepMins = PackedCriteria.depMins(criteria);
            int journeyChanges = PackedCriteria.changes(criteria);
            int journeyArrMins = PackedCriteria.arrMins(criteria);
            int journeyArrStopIdx = profile.arrStationId();


            int startConnectionId = Bits32_24_8.unpack24(PackedCriteria.payload(criteria));
            int startConnectiondepStopId = profile.connections().depStopId(startConnectionId);

            // cas : Première étape à pied
            if (startConnectiondepStopId != depStationId) {
                Journey.Leg newLeg = newFootStep(profile.timeTable(),
                        currentDate, journeydepMins, depStationId, startConnectiondepStopId);
                currentListLegs.add(newLeg);
            }

            int lastStationIdx = 0;

            long currentCriteria = criteria;

            while (journeyChanges > 0) {

                int numberOfIntermediateStops = Bits32_24_8.unpack8(PackedCriteria.payload(currentCriteria));
                int firstConnectionId = Bits32_24_8.unpack24(PackedCriteria.payload(currentCriteria));
                int currentConnectionId = firstConnectionId;

                // on crée une liste avec toutes les arrets intermédiaires
                List < Journey.Leg.IntermediateStop > intermediateStopList = new ArrayList<>();
                for (int i = 0; i < numberOfIntermediateStops; i++) {
                    // on extrait l'arrivée de la première liaison
                    int arrMinsConnection = profile.connections().arrMins(currentConnectionId);
                    LocalDateTime arrTime = dateFromMins(arrMinsConnection, currentDate);
                    // passage à la prochaine liaison
                    currentConnectionId = profile.connections().nextConnectionId(currentConnectionId);
                    int currentConnectionstopId = profile.connections().depStopId(currentConnectionId);
                    int currentStationId = profile.timeTable().stationId(currentConnectionstopId);
                    Stop stop = newStop(profile.timeTable(),currentStationId);
                    // on extrait le départ de la prochaine liaison
                    int depMinsConnection = profile.connections().depMins(currentConnectionId);
                    LocalDateTime depTime = dateFromMins(depMinsConnection, currentDate);
                    intermediateStopList.add(new Journey.Leg.IntermediateStop(stop,arrTime,depTime));
                }

                // cas : voyage1
                int lastConnectionId = currentConnectionId;
                int currentLegDepStopIdx = profile.connections().depStopId(firstConnectionId);
                int currentLegMinsTime = profile.connections().depMins(firstConnectionId);
                Stop currentLegDepStop = newStop(profile.timeTable(), currentLegDepStopIdx);
                LocalDateTime currentLegDepTime = dateFromMins(currentLegMinsTime, currentDate);
                int currentLegArrStopidx = profile.connections().arrStopId(lastConnectionId);
                int currentLegArrMins = profile.connections().arrMins(lastConnectionId);
                LocalDateTime currentLegArrTime = dateFromMins(currentLegArrMins, currentDate);
                Stop currentLegArrStop = newStop(profile.timeTable(), currentLegArrStopidx);
                int routeId = Bits32_24_8.unpack8(PackedCriteria.payload(criteria));
                Vehicle vehicle = profile.timeTable().routes().vehicle(routeId);
                String route = profile.timeTable().routes().name(routeId);
                String destination = profile.trips().destination(routeId);
                Journey.Leg transportStep = new Journey.Leg.Transport(currentLegDepStop,
                       currentLegDepTime, currentLegArrStop, currentLegArrTime,
                        intermediateStopList, vehicle, route, destination);

                currentListLegs.add(transportStep);

                //étape en transport finie, on change de tuple

                currentCriteria = profile.forStation
                        (profile.timeTable().stationId(currentLegArrStopidx)).
                        get(journeyArrMins, --journeyChanges);

                // cas : étape à pied entre deux voyages

                int startStationId = profile.timeTable().stationId(currentLegArrStopidx);
                lastStationIdx = startStationId;
                int nextConnectionIdx = Bits32_24_8.unpack24(PackedCriteria.payload(currentCriteria));
                int nextConnectionDepStopId = profile.connections().depStopId(nextConnectionIdx);
                int nextConnectionDepStationId = profile.timeTable().stationId(nextConnectionDepStopId);
                int durationFootStep = profile.timeTable().transfers().minutesBetween(startStationId, nextConnectionDepStationId);
                LocalDateTime endFootStepTime = dateFromMins(currentLegArrMins + durationFootStep, profile.date());

                Stop nextStop = newStop(profile.timeTable(), nextConnectionDepStationId);

                Journey.Leg.Foot footStep = new Journey.Leg.Foot(currentLegArrStop,currentLegArrTime,nextStop, endFootStepTime);
                currentListLegs.add(footStep);
            }

            if (lastStationIdx != journeyArrStopIdx)  {

            }
            journeyList.add(new Journey(currentListLegs));

        });


        journeyList.sort(Comparator
                .comparing(Journey::depTime)
                .thenComparing(Journey::arrTime));


        return journeyList;
    }
}



