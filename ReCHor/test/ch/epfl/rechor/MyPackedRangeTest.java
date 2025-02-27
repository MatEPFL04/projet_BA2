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
        int largeStart = 0b1001010101010010101000101111111;
        assertThrows(IllegalArgumentException.class, () -> {
            PackedRange.pack(largeStart, largeStart+1 );
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





}
