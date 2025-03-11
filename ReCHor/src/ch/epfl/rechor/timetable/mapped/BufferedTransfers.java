package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Transfers;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public final class BufferedTransfers implements Transfers {
    private final static int DEP_STATION_ID = 0;
    private final static int ARR_STATION_ID = 1;
    private final static int TRANSFER_MINUTES = 2;
    private final StructuredBuffer structuredBuffer;
    private static final Structure structureTransfers = new Structure(Structure.field(DEP_STATION_ID, Structure.FieldType.U16),
            Structure.field(ARR_STATION_ID, Structure.FieldType.U16),Structure.field(TRANSFER_MINUTES, Structure.FieldType.U8));
    //private int [] arrayOfPackedChanges;

    public BufferedTransfers (ByteBuffer buffer) {
        structuredBuffer = new StructuredBuffer(structureTransfers,buffer);
        int numberOfDistinctsArrStops = 0;
        ArrayList <Integer> distinctArrStations = new ArrayList<>();
        for (int i = 0; i < size(); i++) {
            int currentArrId = structuredBuffer.getU16(ARR_STATION_ID,i);
            if (!distinctArrStations.contains(currentArrId)) {
                numberOfDistinctsArrStops++;
                distinctArrStations.add(currentArrId);
            }
        }

    }
    @Override
    public int depStationId(int id) throws IndexOutOfBoundsException {
        return structuredBuffer.getU16(DEP_STATION_ID,id);
    }

    @Override
    public int minutes(int id) throws IndexOutOfBoundsException {
        return structuredBuffer.getU8(TRANSFER_MINUTES,id);
    }

    @Override
    public int arrivingAt(int stationId) throws IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int minutesBetween(int depStationId, int arrStationId) throws NoSuchElementException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
