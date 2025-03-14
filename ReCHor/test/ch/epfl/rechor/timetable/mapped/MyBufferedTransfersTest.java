package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.HexFormat;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MyBufferedTransfersTest {
    @Test
    void arrivingAtMethodWorks() {
        HexFormat hexFormat = HexFormat.ofDelimiter(" ");
        byte[] transfersArray = hexFormat.parseHex("00 00 00 02 04" +
                " 00 03 00 02 08 " +
                "00 02 00 02 03 " +
                "00 04 00 01 05 " +
                "00 06 00 01 04 " +
                "00 02 00 03 09 " +
                "00 04 00 03 08");

        BufferedTransfers structuredTransferts = new BufferedTransfers(ByteBuffer.wrap(transfersArray));
        assertEquals(5,structuredTransferts.minutesBetween(4,1));
        assertEquals(8,structuredTransferts.minutesBetween(4,3));
        assertEquals(9,structuredTransferts.minutesBetween(2,3));
        assertEquals(4,structuredTransferts.minutesBetween(0,2));
        assertThrows(NoSuchElementException.class, () -> structuredTransferts.minutesBetween(1,2));
    }
}
