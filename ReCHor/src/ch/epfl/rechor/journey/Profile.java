package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.Connections;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.Trips;

import java.util.List;
import java.awt.*;
import java.time.LocalDate;
import java.util.Objects;

public record Profile(TimeTable timeTable, LocalDate date, int arrStationId, List<ParetoFront> stationFront) {
    public Profile {
        Objects.requireNonNull(stationFront);
        stationFront = List.copyOf(stationFront);
    }

    public Connections connections() {
        return timeTable.connectionsFor(date);
    }

    public Trips trips(){
        return timeTable.tripsFor(date);
    }

    public ParetoFront forStation(int stationId) throws IndexOutOfBoundsException {
        return stationFront.get(stationId);
    }

    public static final class Builder {
        TimeTable timeTable;
        Builder(TimeTable timeTable, LocalDate date, int arrStationId) {
            this.timeTable = timeTable;

        }
        Builder forStation(int stationId) {

        }
        void setForStation(int stationId, Builder builder) {

        }
        Builder forTrip(int tripId) {

        }

        void setForTrip(int tripId, Builder builder) {

        }

        Profile build() {
        }
    }



}
