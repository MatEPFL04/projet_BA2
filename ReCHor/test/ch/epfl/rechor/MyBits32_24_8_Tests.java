package ch.epfl.rechor;

import ch.epfl.rechor.journey.PackedCriteria;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyBits32_24_8Test {

    @Test
    void packingAndUnpacking8Works () {
        int value24 = 0b0101010100001010100101;
        int value8 = 0b10101011;
        int packedValue = Bits32_24_8.pack(value24,value8);
        assertEquals(value8,Bits32_24_8.unpack8(packedValue));

    }

    @Test
    void packingAndUnpacking24Works () {
        int value24 = 0b0101010100001010100101;
        int value8 = 0b10101011;
        int packedValue = Bits32_24_8.pack(value24,value8);
        assertEquals(value24,Bits32_24_8.unpack24(packedValue));

    }

    @Test
    void ThrownExceptionwhenBitFormatisIncorrect () {
        int valueBiggerThan24 = 0b010101010000101010010110000010;
        int valueBiggerThan8 = 0b1010101000111;

        assertThrows(IllegalArgumentException.class, () -> {
            Bits32_24_8.pack(valueBiggerThan24,1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits32_24_8.pack(1,valueBiggerThan8);
        });

    }
}
