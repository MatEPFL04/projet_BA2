package ch.epfl.rechor;

import ch.epfl.rechor.journey.PackedCriteria;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MyPackedRangeTest {
    @Test
    void packingAndUnpackingStartInclusive () {
        int expectedFinalValue = 0b010101011;
        int pack = PackedRange.pack(expectedFinalValue,300);
        int startExtracted = PackedRange.startInclusive(pack);
        assertEquals(expectedFinalValue, startExtracted);
    }

    @Test
    void packingAndUnpackingEndExclsuive () {
        int expectedFinalValue = 0xFFAE;
        int pack = PackedRange.pack(expectedFinalValue -20,expectedFinalValue);
        int endExtracted = PackedRange.endExclusive(pack);
        assertEquals(expectedFinalValue, endExtracted);
    }

    @Test
    void exceptionThrownWhenIntervalIsTooBig () {
        assertThrows(IllegalArgumentException.class, () -> {
            PackedRange.pack(1, 257);
        });
    }

    @Test
    void exceptionThrownWhenStartIntervalIsTooBig () {
        int largeStart = 1 << 24;
        assertThrows(IllegalArgumentException.class, () -> {
            PackedRange.pack(largeStart, largeStart + 1 );
        });
    }
    @Test
    void exceptionNotThrownWhenStartIntervalIsAsTheLimit () {
        int largeStart = (1 << 24) - 1;
        int packedBigInterval = PackedRange.pack(largeStart,largeStart + 3);
        assertEquals(largeStart,PackedRange.startInclusive(packedBigInterval));

    }

    @Test
    void exceptionThrownWhenValuesAreInverted () {
        int largeStart = 10;
        int end = 3;
        assertThrows(IllegalArgumentException.class, () -> {
            PackedRange.pack(largeStart,end);
        });

    }

    @Test
    void lengthIsCalculatedCorrectly() {
        int start = 0xFAAAB;
        int end = start + 20;
        int pack = PackedRange.pack(start,end);
        int expectedInterval = end - start;
        assertEquals(expectedInterval,PackedRange.length(pack));
    }

    @Test
    void maxLengthDoesNotThrowAnException() {
        int start = 0xFAAAB;
        int length = (1 << 8) - 1;
        int end = start + length;
        int packedRange = PackedRange.pack(start,end);
        assertEquals(start,PackedRange.startInclusive(packedRange));
    }

    @Test
    void moreThanMaxLengthDoesThrowAnException() {
        int start = 0xFAAAB;
        int length = (1 << 8);
        int end = start + length;
        assertThrows(IllegalArgumentException.class, () -> {
            PackedRange.pack(start,end);
        });

    }







}
