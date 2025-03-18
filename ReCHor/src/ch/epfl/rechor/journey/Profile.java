package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.Connections;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.Trips;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.*;
import java.time.LocalDate;
import java.util.Objects;

public record Profile(TimeTable timeTable, LocalDate date, int arrStationId, List<ParetoFront> stationFront) {
    public Profile {
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
        LocalDate date;
        int arrStationId;
        ParetoFront.Builder [] stationsFront;
        ParetoFront.Builder [] tripsFront;
        public Builder(TimeTable timeTable, LocalDate date, int arrStationId) {
            this.timeTable = timeTable;
            this.date = date;
            this.arrStationId = arrStationId;
            stationsFront = new ParetoFront.Builder[timeTable.stations().size()];
            tripsFront = new ParetoFront.Builder[timeTable.tripsFor(date).size()];
        }
        public ParetoFront.Builder forStation(int stationId) {
            return stationsFront[stationId];
        }
        public void setForStation(int stationId, ParetoFront.Builder builder) {
            stationsFront[stationId] = builder;

        }
        public ParetoFront.Builder forTrip(int tripId) {
            return tripsFront[tripId];
        }

        public void setForTrip(int tripId, ParetoFront.Builder builder) {
            tripsFront [tripId] = builder;

        }

        public Profile build() {
            List <ParetoFront> frontList = new ArrayList<>();
            for (int i = 0; i < stationsFront.length; i++) {
                if (Objects.isNull(stationsFront[i])) {
                    frontList.add(ParetoFront.EMPTY);
                }
                frontList.add(stationsFront[i].build());
            }
            return new Profile(timeTable,date,arrStationId, frontList);
        }
    }

}
