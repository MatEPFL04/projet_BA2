package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Trips;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MyBufferedTripsTest {
    private static ByteBuffer byteBuffer(String hex) {
        return ByteBuffer
                .wrap(HexFormat.ofDelimiter(" ").parseHex(hex))
                .asReadOnlyBuffer();
    }

    private static final ByteBuffer TRIPS_1 =
            byteBuffer("00 00 00 00 00 01 00 01 00 02 00 02"); // 3 courses

    private static final List<String> STRING_TABLE_1 =
            List.of("Destination 1", "Destination 2", "Destination 3");

    @Test
    void bufferedTripsSizeWorks() {
        var trips = new BufferedTrips(STRING_TABLE_1, TRIPS_1);
        assertEquals(3, trips.size());
    }

    @Test
    void bufferedTripsThrowsOnInvalidIndex() {
        var trips = new BufferedTrips(STRING_TABLE_1, TRIPS_1);
        assertThrows(IndexOutOfBoundsException.class, () -> trips.destination(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> trips.routeId(3));
    }

    @Test
    void bufferedTripsDestinationWorks() {
        var trips = new BufferedTrips(STRING_TABLE_1, TRIPS_1);
        assertEquals("Destination 1", trips.destination(0));
        assertEquals("Destination 2", trips.destination(1));
        assertEquals("Destination 3", trips.destination(2));
    }

    private BufferedTrips bufferedTrips;
    private ByteBuffer buffer;
    private List<String> stringTable;

    @BeforeEach
    void setUp() {
        // Création d'une table de chaînes simulée
        stringTable = Arrays.asList("Paris", "Lyon", "Marseille", "Bordeaux", "Nice");

        // Allocation d'un buffer pour stocker les routes et destinations (chaque champ U16 fait 2 octets)
        buffer = ByteBuffer.allocate(10 * 4); // Simule 10 voyages

        // Remplissage des données pour les tests
        for (int i = 0; i < 10; i++) {
            buffer.putShort((short) (i % 3)); // ROUTE_ID (exemple : valeurs entre 0 et 2)
            buffer.putShort((short) (i % 5)); // DESTINATION_ID (index dans stringTable)
        }

        // Création de l'objet BufferedTrips
        bufferedTrips = new BufferedTrips(stringTable, buffer);
    }

    @Test
    void testRouteId() {
        assertEquals(0, bufferedTrips.routeId(0));
        assertEquals(1, bufferedTrips.routeId(1));
        assertEquals(2, bufferedTrips.routeId(2));
        assertEquals(0, bufferedTrips.routeId(3));
    }

    @Test
    void testDestination() {
        assertEquals("Paris", bufferedTrips.destination(0));
        assertEquals("Lyon", bufferedTrips.destination(1));
        assertEquals("Marseille", bufferedTrips.destination(2));
        assertEquals("Bordeaux", bufferedTrips.destination(3));
        assertEquals("Nice", bufferedTrips.destination(4));
        assertEquals("Paris", bufferedTrips.destination(5)); // Vérification du cycle
    }

    @Test
    void testSize() {
        assertEquals(10, bufferedTrips.size());
    }

    @Test
    void testBufferedTrips() {
        List<String> stringTable = List.of("Paris", "Berlin", "Rome");
        ByteBuffer buffer = ByteBuffer.allocate(2 * 2); // U16, U16

        buffer.putShort(0, (short) 42); // Route ID
        buffer.putShort(2, (short) 1);  // Destination index (Berlin)

        Trips trips = new BufferedTrips(stringTable, buffer);

        assertEquals(42, trips.routeId(0));
        assertEquals("Berlin", trips.destination(0));
    }

    @Test
    void testInvalidDestinationIndex() {
        List<String> stringTable = List.of("Paris");
        ByteBuffer buffer = ByteBuffer.allocate(2 * 2);

        buffer.putShort(0, (short) 5);
        buffer.putShort(2, (short) 10); // Index hors limites

        Trips trips = new BufferedTrips(stringTable, buffer);

        assertThrows(IndexOutOfBoundsException.class, () -> trips.destination(0));
    }

    private static final BufferedTrips TRIPS = new BufferedTrips(STRING_TABLE_1, TRIPS_1);

    @Test
    void constructorCreatesValidObject() {
        assertNotNull(TRIPS);
    }

    @Test
    void sizeReturnsCorrectValue() {
        assertEquals(3, TRIPS.size());  // On a bien 3 courses
    }

    @Test
    void routeIdThrowsExceptionForInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> TRIPS.routeId(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> TRIPS.routeId(3));
    }


    @Test
    void destinationThrowsExceptionForInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> TRIPS.destination(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> TRIPS.destination(3));
    }


    // Exemple de données de courses (trips)
    // Format: ROUTE_ID (U16) | DESTINATION_ID (U16)
    private static final byte[] TRIPS_BYTES = {
            0x00, 0x01, 0x00, 0x04,  // Route d'index 1, destination "Lausanne" (index 4)
            0x00, 0x02, 0x00, 0x02,  // Route d'index 2, destination "Anet" (index 2)
            0x00, 0x00, 0x00, 0x06   // Route d'index 0, destination "Palézieux" (index 6)
    };

    private static final List<String> STRING_TABLE2 = List.of(
            "1", "70", "Anet", "Ins", "Lausanne", "Losanna", "Palézieux"
    );

    @Test
    void sizeReturnsCorrectNumberOfTrips() {
        ByteBuffer buffer = ByteBuffer.wrap(TRIPS_BYTES);
        BufferedTrips trips = new BufferedTrips(STRING_TABLE2, buffer);

        assertEquals(3, trips.size());
    }

    @Test
    void routeIdReturnsCorrectRouteIndex() {
        ByteBuffer buffer = ByteBuffer.wrap(TRIPS_BYTES);
        BufferedTrips trips = new BufferedTrips(STRING_TABLE2, buffer);

        assertEquals(1, trips.routeId(0));
        assertEquals(2, trips.routeId(1));
        assertEquals(0, trips.routeId(2));
    }

    @Test
    void destinationReturnsCorrectDestinationName() {
        ByteBuffer buffer = ByteBuffer.wrap(TRIPS_BYTES);
        BufferedTrips trips = new BufferedTrips(STRING_TABLE2, buffer);

        assertEquals("Lausanne", trips.destination(0));
        assertEquals("Anet", trips.destination(1));
        assertEquals("Palézieux", trips.destination(2));
    }

    @Test
    void throwsExceptionForInvalidIndex() {
        ByteBuffer buffer = ByteBuffer.wrap(TRIPS_BYTES);
        BufferedTrips trips = new BufferedTrips(STRING_TABLE2, buffer);

        assertThrows(IndexOutOfBoundsException.class, () -> trips.routeId(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> trips.routeId(3));
        assertThrows(IndexOutOfBoundsException.class, () -> trips.destination(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> trips.destination(3));
    }


}