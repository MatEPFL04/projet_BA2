package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MyStructuredBufferTest {

    @Test
    public void testBufferSize() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U8),
                Structure.field(1, Structure.FieldType.U16),
                Structure.field(2, Structure.FieldType.S32)
        );
        ByteBuffer buffer = ByteBuffer.allocate(3 * structure.totalSize());
        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);
        assertEquals(3, structuredBuffer.size());
    }

    @Test
    public void testGetValues() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U8),
                Structure.field(1, Structure.FieldType.U16),
                Structure.field(2, Structure.FieldType.S32)
        );
        ByteBuffer buffer = ByteBuffer.allocate(structure.totalSize());
        buffer.put(0, (byte) 5);
        buffer.putShort(1, (short) 300);
        buffer.putInt(3, 1000000);
        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);
        assertEquals(5, structuredBuffer.getU8(0, 0));
        assertEquals(300, structuredBuffer.getU16(1, 0));
        assertEquals(1000000, structuredBuffer.getS32(2, 0));
    }

    @Test
    public void testInvalidIndex() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U8)
        );
        ByteBuffer buffer = ByteBuffer.allocate(structure.totalSize());
        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> structuredBuffer.getU8(0, 1));
    }
}