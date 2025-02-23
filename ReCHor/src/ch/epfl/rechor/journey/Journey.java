package ch.epfl.rechor.journey;

import ch.epfl.rechor.Preconditions;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * The journey Record represents a journey in public transport.
 * @author Matteo Lazzari (397247)
 * @param legs a list of legs (steps) that constitute the Journey
 *
 */

public record Journey(List<Leg> legs) {

    /**
     * checks whether the given information concerning the legs are valid,
     * @param legs the list steps that constitute the journey
     * @throws IllegalArgumentException if:
     *      - the given list legs is empty,
     *      - if there is no alternance betweem steps in public transports and steps by foot,
     *      - if one departure time precedes the arrival time of the previous leg
     *      - if the arrival stop of a leg doesn't correspond to the departure stop of the next leg.

     */
    public Journey{
        legs = List.copyOf(legs);
        Preconditions.checkArgument(!legs.isEmpty());
        boolean isItAlternating = true;
        boolean depPrecedesArr = true;
        boolean stopConfirmity = true;
        for (int i = 1; i < legs.size(); i++) {
            if (legs.get(i-1) instanceof Leg.Foot && legs.get(i) instanceof Leg.Foot)
                isItAlternating  = false;

            if (legs.get(i-1) instanceof Leg.Transport && legs.get(i) instanceof Leg.Transport)
                isItAlternating = false;

            if (legs.get(i).depTime().isBefore(legs.get(i-1).arrTime()))
                depPrecedesArr = false;

            if (!legs.get(i).depStop().equals(legs.get(i-1).arrStop()))
                stopConfirmity = false;

        }
        Preconditions.checkArgument(isItAlternating);
        Preconditions.checkArgument(depPrecedesArr);
        Preconditions.checkArgument(stopConfirmity);

    }

    /**
     * used to encapsulate the notion of step of a journey
     * implemented only by Transport and Foot as an intermediate stop doesn't
     * have the notion of movement
     * abtract methods are automatically implemented by the records who implement the interface
     */
    public sealed interface Leg{

        Stop depStop();
        LocalDateTime depTime();
        Stop arrStop();
        LocalDateTime arrTime();
        List<IntermediateStop> intermediateStops();
        default Duration duration(){
            return Duration.between(depTime(),arrTime());

        }

        /**
         *
         * @param stop
         *      name of the stop
         * @param arrTime
         * @param depTime
         */
        record IntermediateStop(Stop stop, LocalDateTime arrTime, LocalDateTime depTime){
            /**
             * checks whether the stop is null, and that the arrival and departure time make sense.
             * @param stop
             * @param arrTime
             * @param depTime
             * @throws NullPointerException
             *      if the entry stop is incorrect
             * @throws IllegalArgumentException
             *      if the parameters arrTime and depTime don't make sense
             */
            public IntermediateStop{
                Objects.requireNonNull(stop);
                Preconditions.checkArgument(!depTime.isBefore(arrTime));
            }

        }

        /**
         * record used to emulate a public transport
         * @param depStop
         * @param depTime
         * @param arrStop
         * @param arrTime
         * @param intermediateStops
         *      a list of Intermediate stops between the departure stop and the arrival stop
         * @param vehicle
         * @param route
         * @param destination
         */

        record Transport(Stop depStop,LocalDateTime depTime, Stop arrStop,LocalDateTime arrTime,
                                List<IntermediateStop> intermediateStops,Vehicle vehicle,String route,String destination) implements Leg{
            public Transport{
                Objects.requireNonNull(depStop);
                Objects.requireNonNull(depTime);
                Objects.requireNonNull(arrStop);
                Objects.requireNonNull(arrTime);
                Objects.requireNonNull(vehicle);
                Objects.requireNonNull(route);
                Objects.requireNonNull(destination);
                Preconditions.checkArgument(!depTime.isAfter(arrTime));
                intermediateStops = List.copyOf(intermediateStops);

            }
        }

        /**
         * used to modelize a step of the Journey by foot
         * @param depStop
         * @param depTime
         * @param arrStop
         * @param arrTime
         */

        record Foot(Stop depStop,LocalDateTime depTime,Stop arrStop,LocalDateTime arrTime) implements Leg{
            public Foot{
                Objects.requireNonNull(depStop);
                Objects.requireNonNull(arrStop);
                Objects.requireNonNull(depTime);
                Objects.requireNonNull(arrTime);
                Preconditions.checkArgument(!arrTime.isBefore(depTime));
            }

            @Override
            public List<IntermediateStop> intermediateStops() {
                return List.of();
            }
            public boolean isTransfer(){
                return depStop.name().equals(arrStop.name());
            }
        }


    }

    /**
     * the first Stop of the journey
     * @return the first Stop of the journey
     */

    public Stop depStop(){
        return legs.getFirst().depStop();
    }

    /**the last Stop of the journey
     * @return the last Stop of the journey
     */

    public Stop arrStop(){
        return legs.getLast().arrStop();
    }

    /**
     * @return the departure time of the Journey
     */

    public LocalDateTime depTime(){
        return legs.getFirst().depTime();
    }
    /**
     * @return the arrival time of the Journey
     */
    public LocalDateTime arrTime(){
        return legs.getLast().arrTime();
    }
    /**
     * @return the time elapsed betweeen the start and the end of the Journey
     */
    public Duration duration(){
        return Duration.between(legs.getFirst().depTime(), legs.getLast().arrTime());

    }

}
