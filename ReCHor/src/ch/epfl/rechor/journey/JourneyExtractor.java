package ch.epfl.rechor.journey;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.PackedRange;
import ch.epfl.rechor.timetable.TimeTable;

/*

public class JourneyExtractor {

    private JourneyExtractor() {}


    private static LocalDateTime dateFromMins
            (int minutes, LocalDate date) {
        LocalDateTime midnightDate = date.atStartOfDay();
        return midnightDate.plusMinutes(minutes);
    }


    private static Stop newStop (TimeTable timeTable, int depStationId ) {
        return new Stop(timeTable.stations().name(depStationId),
                timeTable.platformName(depStationId),
                timeTable.stations().longitude(depStationId), timeTable.stations().latitude(depStationId));
    }


    private static Journey.Leg newFootStep(TimeTable timetable,
                                           LocalDate date, int startingMinutes, int actualStopId, int expectedStationId) {

            int actualStationId = timetable.stationId(actualStopId);
            int minutesOfChange = timetable.transfers().minutesBetween(actualStationId, expectedStationId);

            LocalDateTime depTime = dateFromMins(startingMinutes,date);

            int arrTimeMinutes = startingMinutes + minutesOfChange;
            LocalDateTime arrTime = dateFromMins(arrTimeMinutes, date);
            Stop depStop = newStop(timetable, actualStationId);
            Stop arrStop = newStop(timetable, expectedStationId);

            return new Journey.Leg.Foot(depStop, depTime, arrStop, arrTime);
        }

    }


    public static List<Journey> journeys(Profile profile, int depStationId) {

        List <Journey> journeyList = new ArrayList<>();

        //Date du jour qui sera associé à chacun des tuples
        LocalDate currentDate = profile.date();

        //frontière associé au tuple courant
        ParetoFront paretoFront = profile.forStation(depStationId);

        //parcours des tuples associés à la gare de départ donnée

        paretoFront.forEach((long criteria) -> {

            List <Journey.Leg> currentTupleListLegs = new ArrayList<>();

            //informations utiles du premier tuple (pour l'entier du voyage)
            int journeydepMins = PackedCriteria.depMins(criteria);
            int journeyChanges = PackedCriteria.changes(criteria);
            int journeyArrMins = PackedCriteria.arrMins(criteria);
            int journeyArrStopIdx = profile.arrStationId();
            //journeydepMins, profile.date()

            // on regarde l'index de la station de départ de la première liaison et s'il est différent de depStationId,
            // une étape à pied est nécessaire

            int currentConnectionId = Bits32_24_8.unpack24(PackedCriteria.payload(criteria));
            int currentConnectiondepStopId = profile.connections().depStopId(currentConnectionId);
            int currentConnectionDepstationId = profile.timeTable().stationId(currentConnectiondepStopId);



            // étape à pied instanciée, au tout début du voyage, si nécessaire:
            //si l'index de gare (stop) de la liaison est différent de l'index de départ de la station
            //
            if (currentConnectiondepStopId != depStationId) {

                //heure de départ de l'étape à pied, grâce au payload du premier critère:

                //minutes nécessaires pour passer de la première gare à la seconde et commencer le voyage

                int minutesOfChange = profile.timeTable().transfers()
                        .minutesBetween(currentConnectionDepstationId, depStationId);

                //on trouve l'heure d'arrivée associée à notre première étape à pied

                int arrTimeMinutes = journeydepMins + minutesOfChange;
                LocalDateTime ArrTime = dateFromMins(arrTimeMinutes, currentDate);

                Stop depStop = newStop(profile, currentConnectionDepstationId);
                Stop arrStop = newStop(profile, depStationId);

                Journey.Leg.Foot footStep = new Journey.Leg.Foot(depStop, depTime, arrStop, ArrTime);
                currentTupleListLegs.add(footStep);
            }
            //première étape finie si celle-ci était nécessaire.
            // ajout d'étape (Foot or Leg) tant qu'il reste des changements à effectuer

            // la station est courante est désormais depStationId

            currentConnectionId = depStationId;
            // le critère courant pour l'instant est le criteria
            long currentCriteria = criteria;

            while (journeyChanges > 0) {


                //*Déterminer une étape en transport:

                // on trouve le nombre d'intermediateStops associés au payload
                int numberOfIntermediateStops = Bits32_24_8.unpack8(PackedCriteria.payload(currentCriteria));
                // index de la première connection,
                int firstConnectionId = profile.connections().depStopId(currentConnectionId);
                currentConnectionId =  firstConnectionId;
                // on itère sur notre nombre de stops à laisser passer
                for (int i = 0; i < numberOfIntermediateStops; i++) {
                    currentConnectionId = profile.connections().nextConnectionId(currentConnectionId);
                }
                // l'index de la dernière connection
                int lastConnectionId = currentConnectionId;

                // comme on a l'index de la première et dernière liaison, on peut déterminer la première étape du trajet
                int currentLegDepStopIdx = profile.connections().depStopId(firstConnectionId);
                int currentLegMinsTime = profile.connections().depMins(firstConnectionId);

                LocalDateTime currentLegDepTime = dateFromMins(currentLegMinsTime,profile.date());
                Stop currentLegDepStop = newStop(profile,currentLegDepStopIdx);

                int currentLegArrStopidx = profile.connections().arrStopId(lastConnectionId);
                int currentLegArrMins = profile.connections().arrMins(lastConnectionId);

                LocalDateTime currentLegArrTime = dateFromMins(currentLegArrMins,profile.date());
                Stop currentLegArrStop = newStop(profile, currentLegArrStopidx);

                // détermination du type de véhicule:
                int routeId = Bits32_24_8.unpack8(PackedCriteria.payload(criteria));
                Vehicle vehicle = profile.timeTable().routes().vehicle(routeId);
                String route = profile.timeTable().routes().name(routeId);

                Journey.Leg.Transport leg = new Journey.Leg.Transport
                        (currentLegDepStop, currentLegDepTime,
                                currentLegArrStop, currentLegArrTime


                currentCriteria = paretoFront.get(journeyArrMins,--journeyChanges);

                currentConnectionId = Bits32_24_8.unpack24(PackedCriteria.payload(currentCriteria));
                currentConnectiondepStopId = profile.connections().depStopId(currentConnectionId);
                currentConnectionDepstationId = profile.timeTable().stationId(currentConnectiondepStopId);
            }

        });


        journeys.sort(Comparator
                .comparing(Journey::depTime)
                .thenComparing(Journey::arrTime))


        return null;
    }
}

 */

