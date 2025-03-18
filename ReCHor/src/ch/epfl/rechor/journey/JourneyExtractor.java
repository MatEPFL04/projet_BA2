package ch.epfl.rechor.journey;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import ch.epfl.rechor.Bits32_24_8;

public class JourneyExtractor {
    private JourneyExtractor() {}

    private static LocalDateTime ofCriteriaDateTime
            (long criteria, boolean isArrTimeWanted, LocalDate date) {

        int currentDayMins = isArrTimeWanted ?
                PackedCriteria.arrMins(criteria) : PackedCriteria.depMins(criteria);
        int hoursDep = currentDayMins / 60;
        int minutesDep = currentDayMins % 60;
        return date.atTime(hoursDep,minutesDep);
    }


    public static List<Journey> journeys(Profile profile, int depStationId) {
        List <Journey> journeyList = new ArrayList<>();
        ParetoFront paretoFront = profile.forStation(depStationId);
        paretoFront.forEach((long criteria) -> {

            int changesToBeMade = PackedCriteria.changes(criteria);
            int currentConnectionId = Bits32_24_8.unpack24(PackedCriteria.payload(criteria));

            List <Journey.Leg> legList = new ArrayList<>();
            int stopId = profile.connections().depStopId(currentConnectionId);

            int stationId = profile.timeTable().stationId(stopId);
            int numberOfIntermediateStops = Bits32_24_8.unpack8(PackedCriteria.payload(criteria));


            profile.timeTable().stations();

            LocalDateTime depTime = ofCriteriaDateTime(criteria,false, profile.date());
            LocalDateTime arrTime = ofCriteriaDateTime(criteria,true, profile.date());


            if (stationId != depStationId) { // étape à pied, au tout début du voyage
                Stop depStop = new Stop(profile.timeTable().stations().name(stationId),
                        profile.timeTable().platformName(stopId),
                        profile.timeTable().stations().longitude(stationId),
                        profile.timeTable().stations().latitude(stationId));

                Stop arrStop = new Stop(profile.timeTable().stations().name(depStationId),
                        profile.timeTable().platformName(depStationId),
                        profile.timeTable().stations().longitude(depStationId),
                        profile.timeTable().stations().latitude(depStationId));

                Journey.Leg.Foot footStep = new Journey.Leg.Foot(depStop, depTime, arrStop, arrTime);
                legList.add(footStep);
            }
            int nextStepConnectionId = profile.connections().nextConnectionId(currentConnectionId + numberOfIntermediateStops);

            while (changesToBeMade > 0) {


                changesToBeMade--;
        }


        });
        /*
        journeys.sort(Comparator
                .comparing(Journey::depTime)
                .thenComparing(Journey::arrTime))

         */
        return null;
    }
}
