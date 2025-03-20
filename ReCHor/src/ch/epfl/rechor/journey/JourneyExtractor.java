package ch.epfl.rechor.journey;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import ch.epfl.rechor.Bits32_24_8;

public class JourneyExtractor {
    private JourneyExtractor() {}


    private static LocalDateTime dateFromMins
            (int minutes, LocalDate date) {

        LocalDateTime midnightDate = date.atStartOfDay();
        return midnightDate.plusMinutes(minutes);
    }


    public static List<Journey> journeys(Profile profile, int depStationId) {

        List <Journey> journeyList = new ArrayList<>();

        //Date du jour qui sera associé à chacun des tuples
        LocalDate currentDate = profile.date();

        //frontière associé au tuple courant
        ParetoFront paretoFront = profile.forStation(depStationId);

        //parcours des tuples associés à la gare de départ donnée

        paretoFront.forEach((long criteriaForFirstStep) -> {

            List <Journey.Leg> currentTupleListLegs = new ArrayList<>();

            //informations utiles du premier tuple (pour l'entier du voyage)
            int journeydepMins = PackedCriteria.depMins(criteriaForFirstStep);
            int journeyChanges = PackedCriteria.changes(criteriaForFirstStep);
            int journeyArrMins = PackedCriteria.arrMins(criteriaForFirstStep);

            // on regarde l'index de la station de départ de la première liaison et s'il est différent de depStationId,
            // une étape à pied est nécessaire

            int currentConnectionId = Bits32_24_8.unpack24(PackedCriteria.payload(criteriaForFirstStep));
            int currentConnectiondepStopId = profile.connections().depStopId(currentConnectionId);
            int currentConnectionDepstationId = profile.timeTable().stationId(currentConnectiondepStopId);

            // étape à pied instanciée, au tout début du voyage, si nécessaire:
            //si l'index de gare (stop) de la liaison est différent de l'index de départ de la station
            //
            if (currentConnectiondepStopId != depStationId) {

                //heure de départ de l'étape à pied, grâce au payload du premier critère:

                LocalDateTime footDepTime = dateFromMins(journeydepMins, profile.date());

                //minutes nécessaires pour passer de la première gare à la seconde et commencer le voyage

                int minutesOfChange = profile.timeTable().transfers()
                        .minutesBetween(currentConnectionDepstationId, depStationId);

                //on trouve l'heure d'arrivée associée à notre première étape à pied

                int arrTimeMinutes = journeydepMins + minutesOfChange;
                LocalDateTime footArrTime = dateFromMins(arrTimeMinutes, currentDate);

                Stop depStop = new Stop(profile.timeTable().stations().name(currentConnectionDepstationId),
                        profile.timeTable().platformName(currentConnectiondepStopId),
                        profile.timeTable().stations().longitude(currentConnectionDepstationId),
                        profile.timeTable().stations().latitude(currentConnectionDepstationId));

                Stop arrStop = new Stop(profile.timeTable().stations().name(depStationId),
                        profile.timeTable().platformName(depStationId),
                        profile.timeTable().stations().longitude(depStationId),
                        profile.timeTable().stations().latitude(depStationId));

                Journey.Leg.Foot footStep = new Journey.Leg.Foot(depStop, footDepTime, arrStop, footArrTime);
                currentTupleListLegs.add(footStep);
            }

            //première étape finie si celle-ci était nécessaire.
            // ajout d'étape (Foot or Leg) tant qu'il reste des changements à effectuer

            while (journeyChanges > 0) {
                int numberOfIntermediateStops = Bits32_24_8.unpack8(PackedCriteria.payload(criteriaForFirstStep));
                int nextStepConnectionId = profile.connections().nextConnectionId(currentConnectionId + numberOfIntermediateStops);

                int nextStationId = profile.connections().depStopId(nextStepConnectionId);
                ParetoFront nextparetoFront = profile.forStation(nextStationId);
                long nextCriteria = paretoFront.get(journeyArrMins,--journeyChanges);

                    ParetoFront pareto = profile.forStation(nextStationId);
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
