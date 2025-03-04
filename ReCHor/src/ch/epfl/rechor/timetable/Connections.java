package ch.epfl.rechor.timetable;

/**
 * Interface representing all the connections of the swiss trains. Extending the interface Indexed,
 * as connections form a list in the swiss timetable.
 */

public interface Connections extends Indexed {

    int depStopId(int id);
    int depMins(int id);
    int arrStopId(int id);
    int arrMins(int id);
    int tripId(int id);
    int tripPos(int id);
    int nextConnectionId(int id);
}
