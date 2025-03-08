package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.StationAliases;

import java.nio.ByteBuffer;
import java.util.List;

public final class BufferedStationAliases implements StationAliases {

    /**
     * Constants for the different fields
     */
    private static final int ALIAS_ID = 0;
    private static final int STATION_NAME_ID = 1;


    private final StructuredBuffer structuredBuffer;
    private final List<String> stringTable;

    public BufferedStationAliases(  List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = stringTable;
        Structure structureStation = new Structure();
        this.structuredBuffer = new StructuredBuffer(structureStation, buffer);
    }

    @Override
    public String alias(int id) throws IndexOutOfBoundsException {
        int aliasIndex = structuredBuffer.getU16(ALIAS_ID, id);
        return stringTable.get(aliasIndex);
    }

    @Override
    public String stationName(int id) throws IndexOutOfBoundsException {
        int stationNameIndex = structuredBuffer.getU16(STATION_NAME_ID, id);
        return stringTable.get(stationNameIndex);
    }

    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
