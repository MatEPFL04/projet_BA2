package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.journey.Vehicle;
import ch.epfl.rechor.timetable.Trips;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.List;

/**
 *
 */

public final class BufferedTrips implements Trips {
    private static final int ROUTE_ID = 0;
    private static final int DESTINATION_ID = 1;
    private Vehicle vehicle;
    private final StructuredBuffer structuredBuffer;
    private final List <String> stringTable;
    private static final Structure tripsStructure = new Structure(Structure.field(ROUTE_ID, Structure.FieldType.U16),
            Structure.field(DESTINATION_ID, Structure.FieldType.U16));


    public BufferedTrips (List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = stringTable;
        structuredBuffer = new StructuredBuffer(tripsStructure,buffer);
    }

    @Override
    public int routeId(int id) throws IndexOutOfBoundsException {
        return structuredBuffer.getU16(ROUTE_ID,id);
    }

    @Override
    public String destination(int id) throws IndexOutOfBoundsException {
        int stringIdx = structuredBuffer.getU16(DESTINATION_ID,id);
        return stringTable.get(stringIdx);
    }

    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
