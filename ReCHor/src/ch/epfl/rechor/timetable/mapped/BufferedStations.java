package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Stations;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 * Class allows to access stationtime table in flattened representation.
 */

public final class BufferedStations implements Stations {

    /**
     * Constants for the different fields
     */
    private static final int NAME_ID = 0;
    private static final int LON = 1;
    private static final int LAT = 2;

    private final StructuredBuffer structuredBuffer;
    private final List<String> stringTable;
    private static final Structure structureStation = new Structure(Structure.field(NAME_ID, Structure.FieldType.U16),
            Structure.field(LON, Structure.FieldType.S32),Structure.field(LAT, Structure.FieldType.S32));

    /**
     *  Constructs an instance that provides access to
     *  the flattened data available in the given buffer.
     * @param stringTable
     * @param buffer
     */
    public BufferedStations(List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = stringTable;
        this.structuredBuffer = new StructuredBuffer(structureStation, buffer);
    }

    /**
     *
     * @param id the id
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
    public double longitude(int id) throws IndexOutOfBoundsException {
        double longitudeIndex = structuredBuffer.getS32(LON, id);
        return Math.scalb(longitudeIndex * 360, -32);
    }

    /**
     *
     * @param id
     * @return
     * @throws IndexOutOfBoundsException
     */
    @Override
    public double latitude(int id) throws IndexOutOfBoundsException {
        double latitudeIndex = structuredBuffer.getS32(LAT, id);
        return Math.scalb(latitudeIndex * 360, -32);
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