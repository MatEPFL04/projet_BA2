package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Platforms;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 * Class allows to access platform time table in flattened representation.
 */

public final class BufferedPlatforms implements Platforms {

    /**
     * Constants for the different fields
     */
    private static final int NAME_ID = 0;
    private static final int STATION_ID = 1;

    private final StructuredBuffer structuredBuffer;
    private final List<String> stringTable;

    public BufferedPlatforms(List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = stringTable;
        Structure structurePlatform = new Structure(Structure.field(NAME_ID, Structure.FieldType.U16),
                Structure.field(STATION_ID, Structure.FieldType.U16));
        this.structuredBuffer = new StructuredBuffer(structurePlatform, buffer);
    }

    /**
     *
     * @param id
     * @return
     * @throws IndexOutOfBoundsException
     */
    @Override
    public String name(int id) throws IndexOutOfBoundsException {
        int nameIndex = structuredBuffer.getU16(NAME_ID, id);
        return stringTable.get(nameIndex);
    }

    /**
     *
     * @param id
     * @return
     * @throws IndexOutOfBoundsException
     */
    @Override
    public int stationId(int id) throws IndexOutOfBoundsException {
        return structuredBuffer.getU16(STATION_ID, id);
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
