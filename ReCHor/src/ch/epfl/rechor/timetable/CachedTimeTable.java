package ch.epfl.rechor.timetable;

import ch.epfl.rechor.journey.Journey;

import java.sql.Time;
import java.time.LocalDate;
import java.util.Objects;

public class CachedTimeTable implements TimeTable {
    private TimeTable timeTable;
    private Connections connections;
    private Trips trips;
    private LocalDate currentDate;

    public CachedTimeTable (TimeTable timetable) {
        this.timeTable = timetable;
    }

    @Override
    public Stations stations() {
        return timeTable.stations();
    }

    @Override
    public StationAliases stationAliases() {
        return timeTable.stationAliases();
    }

    @Override
    public Platforms platforms() {
        return timeTable.platforms();
    }

    @Override
    public Routes routes() {
        return timeTable.routes();
    }

    @Override
    public Transfers transfers() {
        return timeTable.transfers();
    }

    @Override
    public Trips tripsFor(LocalDate date) {
        if (!Objects.equals(currentDate,date) || trips == null) {
            trips = timeTable.tripsFor(date);
            currentDate = date;
            return trips;
        }
        return trips;
    }

    @Override
    public Connections connectionsFor(LocalDate date) {
        if (!Objects.equals(currentDate,date) || connections == null) {
            connections = timeTable.connectionsFor(date);
            currentDate = date;
            return connections;
        }
        return connections;
    }
}
