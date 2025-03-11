package ch.epfl.rechor.timetable.mapped;
import ch.epfl.rechor.journey.Vehicle;
import ch.epfl.rechor.timetable.Routes;

import java.util.List;
import java.awt.*;
import java.nio.ByteBuffer;

public final class BufferedRoutes implements Routes {
    private static final int NAME_ID = 0;
    private static final int KIND = 1;
    private Vehicle vehicle;
    private final StructuredBuffer structuredBuffer;
    private final List <String> stringTable;
    private static final Structure routeStructure = new Structure(Structure.field(NAME_ID, Structure.FieldType.U16),Structure.field(KIND, Structure.FieldType.U8));

    public BufferedRoutes(List <String> stringTable, ByteBuffer buffer) {
        structuredBuffer = new StructuredBuffer(routeStructure,buffer);
        this.stringTable = stringTable;
    }

    @Override
    public Vehicle vehicle(int id) throws IndexOutOfBoundsException {
        int posVehicle = structuredBuffer.getU8(KIND,id);

        return Vehicle.ALL.get(posVehicle);
    }

    @Override
    public String name(int id) throws IndexOutOfBoundsException {
        int listIdx= structuredBuffer.getU16(NAME_ID,id);
        return stringTable.get(listIdx);
    }

    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
