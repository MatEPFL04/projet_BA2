package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void testConstructorValidBuffer() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U16),
                Structure.field(1, Structure.FieldType.S32)
        );
        ByteBuffer buffer = ByteBuffer.allocate(12);
        assertDoesNotThrow(() -> new StructuredBuffer(structure, buffer));
    }

    @Test
    public void testConstructorInvalidBuffer() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U16),
                Structure.field(1, Structure.FieldType.S32)
        );
        ByteBuffer buffer = ByteBuffer.allocate(10);
        assertThrows(IllegalArgumentException.class, () -> new StructuredBuffer(structure, buffer));
    }

    @Test
    public void testSize() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U16),
                Structure.field(1, Structure.FieldType.S32)
        );
        ByteBuffer buffer = ByteBuffer.allocate(6);
        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);
        assertEquals(1, structuredBuffer.size());
    }


    @Test
    public void testSizeWithEmptyBuffer() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U16),
                Structure.field(1, Structure.FieldType.S32)
        );
        ByteBuffer buffer = ByteBuffer.allocate(0);
        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);
        assertEquals(0, structuredBuffer.size());
    }

    @Test
    public void testSizeWithExactMultipleBuffer() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U16),
                Structure.field(1, Structure.FieldType.S32)
        );
        ByteBuffer buffer = ByteBuffer.allocate(6); // Taille exacte de la structure
        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);
        assertEquals(1, structuredBuffer.size());
    }

    @Test
    public void testGetU8() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U8)
        );
        ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put(0, (byte) 255);
        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);
        assertEquals(255, structuredBuffer.getU8(0, 0));
    }

    @Test
    public void testGetU16() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U16)
        );
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort(0, (short) 65535);
        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);
        assertEquals(65535, structuredBuffer.getU16(0, 0));
    }

    @Test
    public void testGetS32() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.S32)
        );
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(0, -123456);
        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);
        assertEquals(-123456, structuredBuffer.getS32(0, 0));
    }

    @Test
    public void testGetU8InvalidFieldIndex() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U8)
        );
        ByteBuffer buffer = ByteBuffer.allocate(1);
        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> structuredBuffer.getU8(1, 0));
    }

    @Test
    public void testGetU16InvalidFieldIndex() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U16)
        );
        ByteBuffer buffer = ByteBuffer.allocate(2);
        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> structuredBuffer.getU16(1, 0));
    }

    @Test
    public void testGetS32InvalidFieldIndex() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.S32)
        );
        ByteBuffer buffer = ByteBuffer.allocate(4);
        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> structuredBuffer.getS32(1, 0));
    }

    @Test
    public void testGetU8InvalidElementIndex() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U8)
        );
        ByteBuffer buffer = ByteBuffer.allocate(1);
        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> structuredBuffer.getU8(0, 1));
    }

    @Test
    public void testGetU16InvalidElementIndex() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U16)
        );
        ByteBuffer buffer = ByteBuffer.allocate(2);
        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> structuredBuffer.getU16(0, 1));
    }

    @Test
    public void testGetS32InvalidElementIndex() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.S32)
        );
        ByteBuffer buffer = ByteBuffer.allocate(4);
        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> structuredBuffer.getS32(0, 1));
    }

    @Test
    public void testGetU8MultipleElements() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U8)
        );
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.put(0, (byte) 255);
        buffer.put(1, (byte) 128);
        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);
        assertEquals(255, structuredBuffer.getU8(0, 0));
        assertEquals(128, structuredBuffer.getU8(0, 1));
    }

    @Test
    public void testGetU16MultipleElements() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U16)
        );
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putShort(0, (short) 65535);
        buffer.putShort(2, (short) 32768);
        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);
        assertEquals(65535, structuredBuffer.getU16(0, 0));
        assertEquals(32768, structuredBuffer.getU16(0, 1));
    }

    @Test
    public void testGetS32MultipleElements() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.S32)
        );
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putInt(0, -123456);
        buffer.putInt(4, 123456);
        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);
        assertEquals(-123456, structuredBuffer.getS32(0, 0));
        assertEquals(123456, structuredBuffer.getS32(0, 1));
    }

    @Test
    public void testMultipleElementsAndFields() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U8),
                Structure.field(1, Structure.FieldType.U16),
                Structure.field(2, Structure.FieldType.S32)
        );
        ByteBuffer buffer = ByteBuffer.allocate(14);
        buffer.put(0, (byte) 255); // Element 0, Field 0 (U8)
        buffer.putShort(1, (short) 65535); // Element 0, Field 1 (U16)
        buffer.putInt(3, -123456); // Element 0, Field 2 (S32)
        buffer.put(7, (byte) 128); // Element 1, Field 0 (U8)
        buffer.putShort(8, (short) 32768); // Element 1, Field 1 (U16)
        buffer.putInt(10, 123456); // Element 1, Field 2 (S32)

        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);

        // Vérification des valeurs pour le premier élément
        assertEquals(255, structuredBuffer.getU8(0, 0));
        assertEquals(65535, structuredBuffer.getU16(1, 0));
        assertEquals(-123456, structuredBuffer.getS32(2, 0));

        // Vérification des valeurs pour le deuxième élément
        assertEquals(128, structuredBuffer.getU8(0, 1));
        assertEquals(32768, structuredBuffer.getU16(1, 1));
        assertEquals(123456, structuredBuffer.getS32(2, 1));
    }
}