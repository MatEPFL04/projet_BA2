package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.PackedRange;
import ch.epfl.rechor.timetable.Transfers;
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

    @Test
    void testBufferedTransfers() {
        // Création d'un ByteBuffer avec 3 enregistrements (U16, U16, U8)
        ByteBuffer buffer = ByteBuffer.allocate(3 * 5);

        buffer.putShort(0, (short) 1); // Départ station 1
        buffer.putShort(2, (short) 3); // Arrivée station 3
        buffer.put(4, (byte) 5); // Transfert = 5 minutes

        buffer.putShort(5, (short) 2);
        buffer.putShort(7, (short) 3);
        buffer.put(9, (byte) 4);

        buffer.putShort(10, (short) 1);
        buffer.putShort(12, (short) 4);
        buffer.put(14, (byte) 6);

        Transfers transfers = new BufferedTransfers(buffer);

        assertEquals(1, transfers.depStationId(0));
        assertEquals(2, transfers.arrivingAt(3));
        assertEquals(5, transfers.minutesBetween(1, 3));
        assertEquals(6, transfers.minutesBetween(1, 4));
    }


    @Test
    void testInvalidStationId() {
        ByteBuffer buffer = ByteBuffer.allocate(3 * 5);
        BufferedTransfers transfers = new BufferedTransfers(buffer);

        assertThrows(IndexOutOfBoundsException.class, () -> transfers.arrivingAt(99));
    }

    // Exemple de données de changements
    // Les données sont ordonnées pour que les changements arrivant à la même gare soient consécutifs
    // Format: DEP_STATION_ID (U16) | ARR_STATION_ID (U16) | TRANSFER_MINUTES (U8)
    private static final byte[] TRANSFERS_BYTES = {
            // Gare d'arrivée 1
            0x00, 0x02, 0x00, 0x01, 0x05,  // Changement de la gare 2 vers gare 1, durée 5 min
            0x00, 0x03, 0x00, 0x01, 0x08,  // Changement de la gare 3 vers gare 1, durée 8 min

            // Gare d'arrivée 2
            0x00, 0x01, 0x00, 0x02, 0x04,  // Changement de la gare 1 vers gare 2, durée 4 min
            0x00, 0x04, 0x00, 0x02, 0x06,  // Changement de la gare 4 vers gare 2, durée 6 min

            // Gare d'arrivée 3
            0x00, 0x03, 0x00, 0x03, 0x03,  // Changement au sein de la gare 3, durée 3 min
            0x00, 0x04, 0x00, 0x03, 0x09   // Changement de la gare 4 vers gare 3, durée 9 min
    };

    @Test
    void sizeReturnsCorrectNumberOfTransfers() {
        ByteBuffer buffer = ByteBuffer.wrap(TRANSFERS_BYTES);
        BufferedTransfers transfers = new BufferedTransfers(buffer);

        assertEquals(6, transfers.size());
    }

    @Test
    void depStationIdReturnsCorrectDepartureStationId() {
        ByteBuffer buffer = ByteBuffer.wrap(TRANSFERS_BYTES);
        BufferedTransfers transfers = new BufferedTransfers(buffer);

        assertEquals(2, transfers.depStationId(0));
        assertEquals(3, transfers.depStationId(1));
        assertEquals(1, transfers.depStationId(2));
        assertEquals(4, transfers.depStationId(3));
        assertEquals(3, transfers.depStationId(4));
        assertEquals(4, transfers.depStationId(5));
    }

    @Test
    void minutesReturnsCorrectTransferDuration() {
        ByteBuffer buffer = ByteBuffer.wrap(TRANSFERS_BYTES);
        BufferedTransfers transfers = new BufferedTransfers(buffer);

        assertEquals(5, transfers.minutes(0));
        assertEquals(8, transfers.minutes(1));
        assertEquals(4, transfers.minutes(2));
        assertEquals(6, transfers.minutes(3));
        assertEquals(3, transfers.minutes(4));
        assertEquals(9, transfers.minutes(5));
    }

    @Test
    void arrivingAtReturnsCorrectTransferInterval() {
        ByteBuffer buffer = ByteBuffer.wrap(TRANSFERS_BYTES);
        BufferedTransfers transfers = new BufferedTransfers(buffer);

        // Test pour la gare d'arrivée 1 (les deux premiers changements)
        int stationId = 1;
        int interval = transfers.arrivingAt(stationId);

        // Vérifier que l'intervalle contient bien les index 0 et 1
        assertEquals(0, PackedRange.startInclusive(interval));
        assertEquals(2, PackedRange.endExclusive(interval));

        // Tester pour gare d'arrivée 2 (changements 2 et 3)
        stationId = 2;
        interval = transfers.arrivingAt(stationId);
        assertEquals(2, PackedRange.startInclusive(interval));
        assertEquals(4, PackedRange.endExclusive(interval));

        // Tester pour gare d'arrivée 3 (changements 4 et 5)
        stationId = 3;
        interval = transfers.arrivingAt(stationId);
        assertEquals(4, PackedRange.startInclusive(interval));
        assertEquals(6, PackedRange.endExclusive(interval));


    }

    @Test
    void minutesBetweenReturnsCorrectTransferTime() {
        ByteBuffer buffer = ByteBuffer.wrap(TRANSFERS_BYTES);
        BufferedTransfers transfers = new BufferedTransfers(buffer);

        // Test de changements existants
        assertEquals(5, transfers.minutesBetween(2, 1));
        assertEquals(8, transfers.minutesBetween(3, 1));
        assertEquals(4, transfers.minutesBetween(1, 2));
        assertEquals(6, transfers.minutesBetween(4, 2));
        assertEquals(3, transfers.minutesBetween(3, 3)); // Changement interne à une gare
        assertEquals(9, transfers.minutesBetween(4, 3));
    }

    @Test
    void minutesBetweenThrowsExceptionForNonExistingTransfer() {
        ByteBuffer buffer = ByteBuffer.wrap(TRANSFERS_BYTES);
        BufferedTransfers transfers = new BufferedTransfers(buffer);

        // Test de changements qui n'existent pas
        assertThrows(NoSuchElementException.class, () -> transfers.minutesBetween(1, 3));
    }

    @Test
    void throwsExceptionForInvalidIndex() {
        ByteBuffer buffer = ByteBuffer.wrap(TRANSFERS_BYTES);
        BufferedTransfers transfers = new BufferedTransfers(buffer);

        assertThrows(IndexOutOfBoundsException.class, () -> transfers.depStationId(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> transfers.depStationId(6));
        assertThrows(IndexOutOfBoundsException.class, () -> transfers.minutes(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> transfers.minutes(6));
    }

    private static final HexFormat HEX = HexFormat.ofDelimiter(" ");
    private static final ByteBuffer BUFFER = ByteBuffer.wrap(HEX.parseHex(
            "00 01 00 02 05 " +          // Changement de 1 → 2 en 5 minutes
                    "00 03 00 02 0A " +  // Changement de 3 → 2 en 10 minutes
                    "00 04 00 02 0F " +  // Changement de 4 → 2 en 15 minutes
                    "00 02 00 05 14"    // Changement de 2 → 5 en 20 minutes
    ));


    private final BufferedTransfers transfers = new BufferedTransfers(BUFFER);


    @Test
    void sizeReturnsCorrectValue() {
        assertEquals(4, transfers.size());
    }


    @Test
    void depStationIdReturnsCorrectValues() {
        assertEquals(1, transfers.depStationId(0));
        assertEquals(3, transfers.depStationId(1));
        assertEquals(4, transfers.depStationId(2));
        assertEquals(2, transfers.depStationId(3));
    }

    @Test
    void minutesReturnsCorrectValues() {
        assertEquals(5, transfers.minutes(0));
        assertEquals(10, transfers.minutes(1));
        assertEquals(15, transfers.minutes(2));
        assertEquals(20, transfers.minutes(3));
    }


    @Test
    void depStationIdThrowsForInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> transfers.depStationId(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> transfers.depStationId(999));
    }


    @Test
    void minutesThrowsForInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> transfers.minutes(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> transfers.minutes(999));
    }

    @Test
    void constructorHandlesEmptyBuffer() {
        ByteBuffer emptyBuffer = ByteBuffer.allocate(0);
        BufferedTransfers emptyTransfers = new BufferedTransfers(emptyBuffer);
        assertEquals(0, emptyTransfers.size());
    }

    @Test
    void constructorRejectsInvalidBufferSize() {
        ByteBuffer invalidBuffer = ByteBuffer.allocate(7);
        assertThrows(IllegalArgumentException.class, () -> new BufferedTransfers(invalidBuffer));
    }

    @Test
    void arrivingAtHandlesMultipleTransfersToSameStation() {
        ByteBuffer buffer = ByteBuffer.wrap(HEX.parseHex(
                "00 01 00 02 05 " +
                        "00 03 00 02 10 " +
                        "00 04 00 02 15 " +
                        "00 05 00 02 20 " +
                        "00 06 00 03 25"
        ));
        BufferedTransfers multipleTransfers = new BufferedTransfers(buffer);


        int interval = multipleTransfers.arrivingAt(2);
        assertEquals(0, interval >>> 16);
        assertEquals(4, interval & 0xFFFF);


        int interval3 = multipleTransfers.arrivingAt(3);
        assertEquals(4, interval3 >>> 8);
    }


    @Test
    void transfersAllowCircularConnections() {
        ByteBuffer buffer = ByteBuffer.wrap(HEX.parseHex(
                "00 01 00 01 07 " +
                        "00 02 00 02 05"
        ));
        BufferedTransfers circularTransfers = new BufferedTransfers(buffer);


        assertEquals(7, circularTransfers.minutesBetween(1, 1));
        assertEquals(5, circularTransfers.minutesBetween(2, 2));
    }


    @Test
    void transfersHandleExtremeValues() {
        ByteBuffer buffer = ByteBuffer.wrap(HEX.parseHex(
                "00 01 00 02 00 " +
                        "00 02 00 03 FF"
        ));
        BufferedTransfers extremeTransfers = new BufferedTransfers(buffer);


        assertEquals(0, extremeTransfers.minutesBetween(1, 2));
        assertEquals(255, extremeTransfers.minutesBetween(2, 3));
    }


    @Test
    void constructorRejectsInvalidBuffers() {
        ByteBuffer tooSmallBuffer = ByteBuffer.wrap(HEX.parseHex("00 01"));
        ByteBuffer misalignedBuffer = ByteBuffer.wrap(HEX.parseHex("00 01 00 02 05 00"));


        assertThrows(IllegalArgumentException.class, () -> new BufferedTransfers(tooSmallBuffer));
        assertThrows(IllegalArgumentException.class, () -> new BufferedTransfers(misalignedBuffer));
    }

    @Test
    void allTransfersGoToSameStation2() {
        ByteBuffer buffer = ByteBuffer.wrap(HEX.parseHex(
                "00 01 00 05 03 " +
                        "00 02 00 05 06 " +
                        "00 03 00 05 09 " +
                        "00 04 00 05 12"
        ));
        BufferedTransfers allToSame = new BufferedTransfers(buffer);

        int interval = allToSame.arrivingAt(5);
        assertEquals(0, interval >>> 16);
        assertEquals(PackedRange.pack(0,4), interval & 0xFFFF);
    }

    @Test
    void constructorRejectsInvalidBufferSize2() {
        ByteBuffer invalidBuffer = ByteBuffer.allocate(7);
        assertThrows(IllegalArgumentException.class, () -> new BufferedTransfers(invalidBuffer));
    }

    @Test
    void constructorHandlesEmptyBuffer2() {
        ByteBuffer emptyBuffer = ByteBuffer.allocate(0);
        BufferedTransfers emptyTransfers = new BufferedTransfers(emptyBuffer);
        assertEquals(0, emptyTransfers.size());
    }
}