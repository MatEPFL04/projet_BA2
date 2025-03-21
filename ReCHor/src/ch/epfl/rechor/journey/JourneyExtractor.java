package ch.epfl.rechor.journey;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.PackedRange;
import ch.epfl.rechor.timetable.TimeTable;



public class JourneyExtractor {

    private JourneyExtractor() {}


    private static LocalDateTime dateFromMins
            (int minutes, LocalDate date) {
        LocalDateTime midnightDate = date.atStartOfDay();
        return midnightDate.plusMinutes(minutes);
    }


    private static Stop newStop(TimeTable timeTable, int depStationId) {
        return new Stop(timeTable.stations().name(depStationId),
                timeTable.platformName(depStationId),
                timeTable.stations().longitude(depStationId), timeTable.stations().latitude(depStationId));
    }


    private static Journey.Leg newFootStep(TimeTable timetable,
                                           LocalDate date, int startingMinutes, int actualStopId, int expectedStationId) {

        int actualStationId = timetable.stationId(actualStopId);
        int minutesOfChange = timetable.transfers().minutesBetween(actualStationId, expectedStationId);

        LocalDateTime depTime = dateFromMins(startingMinutes, date);

        int arrTimeMinutes = startingMinutes + minutesOfChange;
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
            List<Journey.Leg> currentTupleListLegs = new ArrayList<>();

            //informations utiles du premier tuple (pour l'ENTIER du voyage)
            int journeydepMins = PackedCriteria.depMins(criteria);
            int journeyChanges = PackedCriteria.changes(criteria);
            int journeyArrMins = PackedCriteria.arrMins(criteria);
            int journeyArrStopIdx = profile.arrStationId();


            int currentConnectionId = Bits32_24_8.unpack24(PackedCriteria.payload(criteria));
            int currentConnectiondepStopId = profile.connections().depStopId(currentConnectionId);


            if (currentConnectiondepStopId != depStationId) {

                Journey.Leg newLeg = newFootStep(profile.timeTable(),
                        profile.date(), journeydepMins, currentConnectiondepStopId, depStationId);
                currentTupleListLegs.add(newLeg);
            }

            currentConnectionId = depStationId;
            long currentCriteria = criteria;

            while (journeyChanges > 0) {

                int numberOfIntermediateStops = Bits32_24_8.unpack8(PackedCriteria.payload(currentCriteria));

                int firstConnectionId = profile.connections().depStopId(currentConnectionId);
                currentConnectionId = firstConnectionId;

                for (int i = 0; i < numberOfIntermediateStops; i++) {
                    currentConnectionId = profile.connections().nextConnectionId(currentConnectionId);
                }

                int lastConnectionId = currentConnectionId;

                int currentLegDepStopIdx = profile.connections().depStopId(firstConnectionId);
                int currentLegMinsTime = profile.connections().depMins(firstConnectionId);

                Stop currentLegDepStop = newStop(profile.timeTable(), currentLegDepStopIdx);
                LocalDateTime currentLegDepTime = dateFromMins(currentLegMinsTime, profile.date());

                int currentLegArrStopidx = profile.connections().arrStopId(lastConnectionId);
                int currentLegArrMins = profile.connections().arrMins(lastConnectionId);

                LocalDateTime currentLegArrTime = dateFromMins(currentLegArrMins, profile.date());
                Stop currentLegArrStop = newStop(profile.timeTable(), currentLegArrStopidx);

                int routeId = Bits32_24_8.unpack8(PackedCriteria.payload(criteria));
                Vehicle vehicle = profile.timeTable().routes().vehicle(routeId);
                String route = profile.timeTable().routes().name(routeId);
                String destination = profile.trips().destination(routeId);

                //Journey.Leg transportStep = new Journey.Leg.Transport(currentLegDepStop,
                   //     currentLegDepTime, currentLegArrStop, currentLegArrTime, , vehicle, route, destination);

                currentCriteria = paretoFront.get(journeyArrMins, --journeyChanges);

                currentConnectionId = Bits32_24_8.unpack24(PackedCriteria.payload(currentCriteria));
                currentConnectiondepStopId = profile.connections().depStopId(currentConnectionId);
            }

        });



        /*
        journeys.sort(Comparator
                .comparing(Journey::depTime)
                .thenComparing(Journey::arrTime))


        return null;

         */
        return null;
    }
}



