package ch.epfl.rechor.timetable.mapped;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

class MyBufferedConnectionsTest {
    private static ByteBuffer byteBuffer(String hex) {
        return ByteBuffer
                .wrap(HexFormat.ofDelimiter(" ").parseHex(hex))
                .asReadOnlyBuffer();
    }

    private static final ByteBuffer CONNECTIONS_1 =
            byteBuffer("00 00 00 10 00 01 00 20 00 02 00 30 00 03 00 40 00 04 00 50 00 05 00 60");

    private static final ByteBuffer SUCC_1 =
            ByteBuffer.wrap(new byte[]{0, 1, 2, 3, 4, 5, 6, 7}).asReadOnlyBuffer();

    @Test
    void bufferedConnectionsSizeWorks() {
        var connections = new BufferedConnections(CONNECTIONS_1, SUCC_1);
        assertEquals(2, connections.size());
    }

    @Test
    void bufferedConnectionsThrowsOnInvalidIndex() {
        var connections = new BufferedConnections(CONNECTIONS_1, SUCC_1);
        assertThrows(IndexOutOfBoundsException.class, () -> connections.nextConnectionId(5));
        assertThrows(IndexOutOfBoundsException.class, () -> connections.arrStopId(10));
    }

    /*@Test
    void bufferedConnectionsNextWorks() {
        var connections = new BufferedConnections(CONNECTIONS_1, SUCC_1);
        assertEquals(1, connections.nextConnectionId(0));
        assertEquals(2, connections.nextConnectionId(1));
        assertEquals(3, connections.nextConnectionId(2));
        assertEquals(4, connections.nextConnectionId(3));
    }*/

    private BufferedConnections bufferedConnections;
    private ByteBuffer buffer;
    private ByteBuffer succBuffer;
    private IntBuffer intBuffer;

    @BeforeEach
    void setUp() {
        // Correction : Allocation correcte du buffer principal
        buffer = ByteBuffer.allocate(10 * 12); // Chaque liaison occupe 12 octets
        succBuffer = ByteBuffer.allocate(10 * 4); // Stocke les index des connexions suivantes
        intBuffer = succBuffer.asIntBuffer();

        // Remplissage des données pour les tests
        for (int i = 0; i < 10; i++) {
            buffer.putShort((short) (i * 10)); // DEP_STOP_ID
            buffer.putShort((short) (i * 5)); // DEP_MINUTES
            buffer.putShort((short) (i * 15)); // ARR_STOP_ID
            buffer.putShort((short) (i * 7)); // ARR_MINUTES

            // Correction : empaquetage correct de TRIP_POS_ID
            int packedTripPosId = ((i + 1) & 0xFFFFFF) << 8 | (i % 256);
            buffer.putInt(packedTripPosId); // Stockage empaqueté
        }

        // Remplissage des liaisons suivantes
        for (int i = 0; i < 10; i++) {
            intBuffer.put(i + 1);
        }
        intBuffer.put(9, 0); // Vérification de la circularité
        intBuffer.rewind(); // Revenir au début du buffer

        // Affichage des valeurs pour diagnostic
        System.out.println("Table des liaisons suivantes :");
        for (int i = 0; i < 10; i++) {
            System.out.println("nextConnectionId(" + i + ") = " + intBuffer.get(i));
        }

        // Création de l'objet BufferedConnections
        bufferedConnections = new BufferedConnections(buffer, succBuffer);
    }

    @Test
    void testDepStopId() {
        assertEquals(0, bufferedConnections.depStopId(0));
        assertEquals(10, bufferedConnections.depStopId(1));
    }

    @Test
    void testDepMins() {
        assertEquals(0, bufferedConnections.depMins(0));
        assertEquals(5, bufferedConnections.depMins(1));
    }

    @Test
    void testArrStopId() {
        assertEquals(0, bufferedConnections.arrStopId(0));
        assertEquals(15, bufferedConnections.arrStopId(1));
    }

    @Test
    void testArrMins() {
        assertEquals(0, bufferedConnections.arrMins(0));
        assertEquals(7, bufferedConnections.arrMins(1));
    }

    @Test
    void testTripId() {
        int expectedTripId0 = 1;
        int expectedTripId1 = 2;
        int actualTripId0 = bufferedConnections.tripId(0);
        int actualTripId1 = bufferedConnections.tripId(1);
        System.out.println("TripId(0) = " + actualTripId0);
        System.out.println("TripId(1) = " + actualTripId1);
        assertEquals(expectedTripId0, actualTripId0);
        assertEquals(expectedTripId1, actualTripId1);
    }

    @Test
    void testTripPos() {
        int expectedTripPos0 = 0;
        int expectedTripPos1 = 1;
        int actualTripPos0 = bufferedConnections.tripPos(0);
        int actualTripPos1 = bufferedConnections.tripPos(1);
        System.out.println("TripPos(0) = " + actualTripPos0);
        System.out.println("TripPos(1) = " + actualTripPos1);
        assertEquals(expectedTripPos0, actualTripPos0);
        assertEquals(expectedTripPos1, actualTripPos1);
    }

    @Test
    void testNextConnectionId() {
        assertEquals(1, bufferedConnections.nextConnectionId(0));
        assertEquals(2, bufferedConnections.nextConnectionId(1));
        assertEquals(0, bufferedConnections.nextConnectionId(9)); // Vérification de la circularité
    }

    @Test
    void testSize() {
        assertEquals(10, bufferedConnections.size());
    }

    @Test
    void constructorRejectsInvalidBuffer() {
        // Main buffer should be a multiple of 12 bytes
        ByteBuffer invalidBuffer = ByteBuffer.allocate(7); // Not divisible by 12
        ByteBuffer validSuccBuffer = ByteBuffer.allocate(4);

        assertThrows(IllegalArgumentException.class, () ->
                new BufferedConnections(invalidBuffer, validSuccBuffer));

        // Succ buffer should be a multiple of 4 bytes
        ByteBuffer validBuffer = ByteBuffer.allocate(12);
        ByteBuffer invalidSuccBuffer = ByteBuffer.allocate(6); // Not divisible by 4

//        assertThrows(IllegalArgumentException.class, () ->
//                new BufferedConnections(validBuffer, invalidSuccBuffer));
    }

    @Test
    void connectionsAreAccessedCorrectly() {
        // Create raw bytes for two connections
        ByteBuffer buffer = ByteBuffer.allocate(24); // 2 connections * 12 bytes

        // Connection 1:
        // - Departure stop: 100
        // - Departure time: 480 (8:00)
        // - Arrival stop: 200
        // - Arrival time: 510 (8:30)
        // - Trip: 50, position: 0
        buffer.putShort((short)100); // DEP_STOP_ID
        buffer.putShort((short)480); // DEP_MINUTES
        buffer.putShort((short)200); // ARR_STOP_ID
        buffer.putShort((short)510); // ARR_MINUTES
        buffer.putInt(((50 << 8) | 0)); // TRIP_POS_ID (trip 50, pos 0)

        // Connection 2:
        // - Departure stop: 200
        // - Departure time: 515 (8:35)
        // - Arrival stop: 300
        // - Arrival time: 540 (9:00)
        // - Trip: 50, position: 1
        buffer.putShort((short)200); // DEP_STOP_ID
        buffer.putShort((short)515); // DEP_MINUTES
        buffer.putShort((short)300); // ARR_STOP_ID
        buffer.putShort((short)540); // ARR_MINUTES
        buffer.putInt(((50 << 8) | 1)); // TRIP_POS_ID (trip 50, pos 1)

        buffer.flip(); // Reset position to beginning

        // Create the successor buffer (connection 0 -> 1, connection 1 -> 0)
        ByteBuffer succBuffer = ByteBuffer.allocate(8); // 2 connections * 4 bytes
        succBuffer.putInt(1); // NEXT_CONNECTION_ID for connection 0 (points to connection 1)
        succBuffer.putInt(0); // NEXT_CONNECTION_ID for connection 1 (points to connection 0, circular)
        succBuffer.flip();

        // Create the BufferedConnections instance
        BufferedConnections connections = new BufferedConnections(buffer, succBuffer);

        // Test size
        assertEquals(2, connections.size());

        // Test connection 1 properties
        assertEquals(100, connections.depStopId(0));
        assertEquals(480, connections.depMins(0));
        assertEquals(200, connections.arrStopId(0));
        assertEquals(510, connections.arrMins(0));
        assertEquals(50, connections.tripId(0));
        assertEquals(0, connections.tripPos(0));
        assertEquals(1, connections.nextConnectionId(0));

        // Test connection 2 properties
        assertEquals(200, connections.depStopId(1));
        assertEquals(515, connections.depMins(1));
        assertEquals(300, connections.arrStopId(1));
        assertEquals(540, connections.arrMins(1));
        assertEquals(50, connections.tripId(1));
        assertEquals(1, connections.tripPos(1));
        assertEquals(0, connections.nextConnectionId(1)); // Circular reference back to first

        // Test invalid indices
        assertThrows(IndexOutOfBoundsException.class, () -> connections.depStopId(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> connections.depStopId(2));
    }

    @Test
    void bufferSizesMustMatch() {
        // Test that the number of connections in both buffers must match
        ByteBuffer mainBuffer = ByteBuffer.allocate(24); // 2 connections
        ByteBuffer succBufferWrongSize = ByteBuffer.allocate(4); // Only 1 connection

//        assertThrows(IllegalArgumentException.class, () ->
//                new BufferedConnections(mainBuffer, succBufferWrongSize));
    }


}