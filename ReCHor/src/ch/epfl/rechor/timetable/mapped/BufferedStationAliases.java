package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.StationAliases;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 * Class allows to access alias station timetable in flattened representation.
 */

public final class BufferedStationAliases implements StationAliases {

    /**
     * Constants for the different fields
     */
    private static final int ALIAS_ID = 0;
    private static final int STATION_NAME_ID = 1;
    private static final Structure structureStationAliases = new Structure(Structure.field(ALIAS_ID, Structure.FieldType.U16),
            Structure.field(STATION_NAME_ID, Structure.FieldType.U16));


    private final StructuredBuffer structuredBuffer;
    private final List<String> stringTable;

    public BufferedStationAliases(  List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = stringTable;

        this.structuredBuffer = new StructuredBuffer(structureStationAliases, buffer);
    }

    /**
     *
     * @param id
     * @return
     * @throws IndexOutOfBoundsException
     */
    @Override
    public String alias(int id) throws IndexOutOfBoundsException {
        int aliasIndex = structuredBuffer.getU16(ALIAS_ID, id);
        return stringTable.get(aliasIndex);
    }

    /**
     *
     * @param id
     * @return
     * @throws IndexOutOfBoundsException
     */
    @Override
    public String stationName(int id) throws IndexOutOfBoundsException {
        int stationNameIndex = structuredBuffer.getU16(STATION_NAME_ID, id);
        return stringTable.get(stationNameIndex);
    }

    /**
     *
     * @return
     */
    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
