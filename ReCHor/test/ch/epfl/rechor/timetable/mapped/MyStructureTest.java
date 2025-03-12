package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyStructureTest {

    @Test
    public void testTotalSizeCalculation() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U8),
                Structure.field(1, Structure.FieldType.U16),
                Structure.field(2, Structure.FieldType.S32)
        );
        assertEquals(1 + 2 + 4, structure.totalSize());
    }

    @Test
    public void testOffsetCalculation() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U8),
                Structure.field(1, Structure.FieldType.U16),
                Structure.field(2, Structure.FieldType.S32)
        );
        assertEquals(1, structure.offset(1, 0));
        assertEquals(3, structure.offset(2, 0));
        assertEquals(7, structure.offset(0, 1));
    }

    @Test
    public void testInvalidFieldOrder() {
        assertThrows(IllegalArgumentException.class, () ->
                new Structure(Structure.field(1, Structure.FieldType.U8), Structure.field(0, Structure.FieldType.U16))
        );
    }


}