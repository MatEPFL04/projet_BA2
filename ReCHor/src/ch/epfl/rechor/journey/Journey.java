package ch.epfl.rechor.journey;

import ch.epfl.rechor.Preconditions;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public record Journey(List<Leg> legs) {
    public Journey{
        legs = List.copyOf(legs);
        Preconditions.checkArgument(!legs.isEmpty());
        boolean isItAlternating = true;
        for (int i = 0; i < legs.size() - 1; i++){
            if (legs.get(i) instanceof Leg.Foot && legs.get(i+1) instanceof Leg.Foot){
                isItAlternating  = false;
            }
            if (legs.get(i) instanceof Leg.Transport && legs.get(i+1) instanceof Leg.Transport){
                isItAlternating = false;
            }
        }
        Preconditions.checkArgument(isItAlternating);

    }
    public interface Leg{
        public record IntermediateStop(Stop stop, LocalDateTime arrTime, LocalDateTime depTime) implements Leg{
            public IntermediateStop{

            }

        }
        public record  Transport() implements Leg{

        }
        public record Foot() implements Leg{

        }
        abstract Stop depStop();
        abstract LocalDateTime depTime();

        abstract Stop arrStop();
        abstract LocalDateTime arrTime();
        abstract List<IntermediateStop> intermediateStops();
        default Duration duration(){}

    }

    public Stop depStop(){

    }
    public Stop arrStop(){

    }
    public LocalDateTime depTime(){
        return legs.getFirst()

    }
    public LocalDateTime arrTime(){
        return legs.getLast()
    }
    public Duration duration(){

    }



}
