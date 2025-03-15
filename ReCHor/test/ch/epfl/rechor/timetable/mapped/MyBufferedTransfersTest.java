package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.PackedRange;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.HexFormat;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MyBufferedTransfersTest {

    @Test
    void minutesBetweenMethodWorks() {
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

    @Test
    void arrivingAtMethodWorks() {
        HexFormat hexFormat = HexFormat.ofDelimiter(" ");
        byte[] transfersArray = hexFormat.parseHex("00 20 00 02 04" +
                " 00 03 00 02 08 " +
                "00 02 00 01 03 " +
                "00 04 00 01 05 " +
                "00 06 00 01 04 " +
                "00 02 00 03 09 " +
                "00 02 00 03 09 " +
                "00 02 00 03 09 " +
                "00 02 00 03 09 " +
                "00 02 00 10 09 " +
                "00 04 00 10 08");
        BufferedTransfers structuredTransferts = new BufferedTransfers(ByteBuffer.wrap(transfersArray));
        assertEquals(PackedRange.pack(2,5),structuredTransferts.arrivingAt(1));
        assertEquals(PackedRange.pack(5,9),structuredTransferts.arrivingAt(3));
        assertEquals(PackedRange.pack(9,11),structuredTransferts.arrivingAt(16));
    }

    @Test
    void minutesAreCorrectlyReturned() {
        HexFormat hexFormat = HexFormat.ofDelimiter(" ");
        byte[] transfersArray = hexFormat.parseHex("00 20 00 02 09" +
                " 00 03 00 02 08 " +
                "00 02 00 01 07 " +
                "00 04 00 01 06 " +
                "00 06 00 01 05 " +
                "00 02 00 03 04 " +
                "00 02 00 03 03 " +
                "00 02 00 03 02 " +
                "00 02 00 03 01 " +
                "00 02 00 10 00");
        BufferedTransfers structuredTransferts = new BufferedTransfers(ByteBuffer.wrap(transfersArray));
        for (int i = 9; i >= 0 ; i--) {
            assertEquals(i, structuredTransferts.minutes(9-i));

        }
    }

    @Test
    void depStationsAreCorrectlyReturned() {
        HexFormat hexFormat = HexFormat.ofDelimiter(" ");
        byte[] transfersArray = hexFormat.parseHex("00 00 00 02 09" +
                " 00 02 00 02 08 " +
                "00 04 00 01 07 " +
                "00 06 00 01 06 " +
                "00 08 00 01 05");
        BufferedTransfers structuredTransferts = new BufferedTransfers(ByteBuffer.wrap(transfersArray));
        for (int i = 0; i <= 8 ; i+=2) {
            assertEquals(i, structuredTransferts.depStationId(i/2));

        }
        //size method works
        assertEquals(5,structuredTransferts.size());
    }
}
