package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.PackedRange;
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
    private int [] arrayOfPackedChanges;

    public BufferedTransfers (ByteBuffer buffer) {

        structuredBuffer = new StructuredBuffer(structureTransfers,buffer);
        ArrayList <Integer> distinctArrStations = new ArrayList<>();
        for (int i = 0; i < size(); i++) {
            int currentArrId = structuredBuffer.getU16(ARR_STATION_ID,i);
            if (!distinctArrStations.contains(currentArrId)) {
                distinctArrStations.add(currentArrId);
            }
        }

        int startIdx, lastExcludedIdx = 0;
        int [] arrStationsPackedInterval = new int[distinctArrStations.size()];
        startIdx = lastExcludedIdx;
        int currentPos = startIdx;
        int currentArrId = structuredBuffer.getU16(ARR_STATION_ID,startIdx);
        while((currentPos + 1 < size()) && currentArrId == structuredBuffer.getU16(ARR_STATION_ID,++currentPos)) {
            lastExcludedIdx++;
        }
        arrStationsPackedInterval[currentArrId] = PackedRange.pack(startIdx,++lastExcludedIdx);

        arrayOfPackedChanges = arrStationsPackedInterval;



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
        return arrayOfPackedChanges[stationId];
    }

    @Override
    public int minutesBetween(int depStationId, int arrStationId) throws NoSuchElementException, IndexOutOfBoundsException {

        int startIdx_ArrStation = PackedRange.startInclusive(arrivingAt(arrStationId));
        int changeIdx = startIdx_ArrStation;
        int endIdx_ArrStation = PackedRange.endExclusive(arrivingAt(arrStationId));
        boolean isChangeValid = false;
        for (int i = startIdx_ArrStation; i < endIdx_ArrStation ; i++) {
            if (depStationId(i) == depStationId) {
                isChangeValid = true;
                changeIdx = i;
                break;
            }
        }
        if (!isChangeValid) {
            throw new NoSuchElementException();
        }
        return minutes(changeIdx);
    }

    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
