package ch.epfl.rechor.timetable.mapped;
import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.PackedRange;
import ch.epfl.rechor.timetable.Connections;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public final class BufferedConnections implements Connections {
    private static final int DEP_STOP_ID = 0;
    private static final int DEP_MINUTES = 1;
    private static final int ARR_STOP_ID = 2;
    private static final int ARR_MINUTES = 3;
    private static final int TRIP_POS_ID = 4;
    private IntBuffer intBuffer;
    private StructuredBuffer structuredBuffer;
    private static final Structure connectionStructure = new Structure(Structure.field(DEP_STOP_ID, Structure.FieldType.U16),
            Structure.field(DEP_MINUTES, Structure.FieldType.U16), Structure.field(ARR_STOP_ID, Structure.FieldType.U16),
            Structure.field(ARR_MINUTES, Structure.FieldType.U16), Structure.field(TRIP_POS_ID, Structure.FieldType.S32));

    public BufferedConnections(ByteBuffer buffer, ByteBuffer succBuffer) {
        intBuffer = succBuffer.asIntBuffer();
        structuredBuffer = new StructuredBuffer(connectionStructure,buffer);
    }

    @Override
    public int depStopId(int id) throws IndexOutOfBoundsException {
        return structuredBuffer.getU16(DEP_STOP_ID,id);
    }

    @Override
    public int depMins(int id) throws IndexOutOfBoundsException {
        return structuredBuffer.getU16(DEP_MINUTES,id);
    }

    @Override
    public int arrStopId(int id) throws IndexOutOfBoundsException {
        return structuredBuffer.getU16(ARR_STOP_ID,id);
    }

    @Override
    public int arrMins(int id) throws IndexOutOfBoundsException {
        return structuredBuffer.getU16(ARR_MINUTES,id);
    }

    @Override
    public int tripId(int id) throws IndexOutOfBoundsException {
        return Bits32_24_8.unpack24(structuredBuffer.getS32(TRIP_POS_ID,id));
    }

    @Override
    public int tripPos(int id) throws IndexOutOfBoundsException {
        return Bits32_24_8.unpack8(structuredBuffer.getS32(TRIP_POS_ID,id));
    }

    @Override
    public int nextConnectionId(int id) throws IndexOutOfBoundsException {
        return intBuffer.get(id);
    }

    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
