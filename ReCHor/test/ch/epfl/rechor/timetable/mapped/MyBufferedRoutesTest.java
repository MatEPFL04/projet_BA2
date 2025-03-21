package ch.epfl.rechor.timetable.mapped;


import ch.epfl.rechor.journey.Vehicle;
import ch.epfl.rechor.timetable.Routes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MyBufferedRoutesTest {
    private static final int NAME_ID = 0;
    private static final int KIND = 1;

    @Test
    void kindsAreCorrectlyAccessed () {
        ByteBuffer buffer = ByteBuffer.allocate(30);
        for (int i = 0; i < 7; i++) {
            buffer.putShort((short) i); // Index "Route A"
            buffer.put((byte) i);
        }

        buffer.flip();

        List<String> stringTable = new ArrayList<>(List.of(
                "Route A", "Route B", "Route C", "Route D","Route E", "Route F", "Route G"
        ));

        BufferedRoutes bufferedRoutes = new BufferedRoutes(stringTable,buffer);
        for (int i = 0; i < Vehicle.ALL.size(); i++) {
            Vehicle expectedVehicle = Vehicle.ALL.get(i);
            assertEquals(expectedVehicle, bufferedRoutes.vehicle(i));
        }
    }

    @Test
    public void testBufferedRoutesNominal() {
        List<String> stringTable = List.of("Ligne 1", "Ligne 2", "Ligne 3");

        HexFormat hexFormat = HexFormat.ofDelimiter(" ");
        byte[] bytes = hexFormat.parseHex(
                "00 00 00 " +  // Ligne 1 → TRAM (0)
                        "00 01 01 " +  // Ligne 2 → METRO (1)
                        "00 02 03"     // Ligne 3 → BUS (3)
        );

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        BufferedRoutes routes = new BufferedRoutes(stringTable, buffer);

        assertEquals(3, routes.size());

        assertEquals("Ligne 1", routes.name(0));
        assertEquals(Vehicle.TRAM, routes.vehicle(0));

        assertEquals("Ligne 2", routes.name(1));
        assertEquals(Vehicle.METRO, routes.vehicle(1));

        assertEquals("Ligne 3", routes.name(2));
        assertEquals(Vehicle.BUS, routes.vehicle(2));
    }

    @Test
    public void testBufferedRoutesInvalidIndex() {
        List<String> stringTable = List.of("Ligne A", "Ligne B");

        HexFormat hexFormat = HexFormat.ofDelimiter(" ");
        byte[] bytes = hexFormat.parseHex("00 00 00 01 00 02");

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        BufferedRoutes routes = new BufferedRoutes(stringTable, buffer);

        assertThrows(IndexOutOfBoundsException.class, () -> routes.name(2));
        assertThrows(IndexOutOfBoundsException.class, () -> routes.vehicle(2));
    }

    @Test
    public void testBufferedRoutesEmpty() {
        List<String> stringTable = List.of();
        ByteBuffer buffer = ByteBuffer.allocate(0);

        BufferedRoutes routes = new BufferedRoutes(stringTable, buffer);
        assertEquals(0, routes.size());
    }

    @Test
    public void testAllVehicleTypes() {
        List<String> stringTable = List.of("Tramway", "Métro", "Bus", "Train", "Bateau", "Funiculaire", "Téléphérique");

        HexFormat hexFormat = HexFormat.ofDelimiter(" ");
        byte[] bytes = hexFormat.parseHex(
                "00 00 00 " +  // TRAM
                        "00 01 01 " +  // METRO
                        "00 02 02 " +  // BUS
                        "00 03 03 " +  // TRAIN
                        "00 04 04 " +  // BOAT
                        "00 05 05 " +  // FUNICULAR
                        "00 06 06"     // CABLE_CAR
        );

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        BufferedRoutes routes = new BufferedRoutes(stringTable, buffer);

        assertEquals(7, routes.size());

        for (int i = 0; i < 7; i++) {
            assertEquals(stringTable.get(i), routes.name(i));
            assertEquals(Vehicle.values()[i], routes.vehicle(i));
        }
    }

    @Test
    public void testBufferedRoutesInvalidIndex2() {
        List<String> stringTable = List.of("Ligne A", "Ligne B");

        HexFormat hexFormat = HexFormat.ofDelimiter(" ");
        byte[] bytes = hexFormat.parseHex("00 00 00 01 00 02");

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        BufferedRoutes routes = new BufferedRoutes(stringTable, buffer);

        assertThrows(IndexOutOfBoundsException.class, () -> routes.name(2));
        assertThrows(IndexOutOfBoundsException.class, () -> routes.vehicle(2));
    }

    @Test
    public void testLastElementIsAccessible() {
        List<String> stringTable = List.of("Ligne Alpha", "Ligne Beta", "Ligne Gamma");

        HexFormat hexFormat = HexFormat.ofDelimiter(" ");
        byte[] bytes = hexFormat.parseHex(
                "00 00 00 " +  // Ligne Alpha → TRAM
                        "00 01 01 " +  // Ligne Beta → METRO
                        "00 02 06"     // Ligne Gamma → FUNICULAR
        );

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        BufferedRoutes routes = new BufferedRoutes(stringTable, buffer);

        assertEquals("Ligne Gamma", routes.name(2));
        assertEquals(Vehicle.FUNICULAR, routes.vehicle(2));
    }

    @Test

    void testBufferedRoutes3() {

        List<String> stringTable = List.of("Ligne 1", "Ligne 2");
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0, 0,  0,   // Ligne 1 → TRAM (0)
                0, 1,  3,   // Ligne 2 → BUS (3)
                0, 2,  5    // Ligne 3 → BOAT (5)
        });

        BufferedRoutes routes = new BufferedRoutes(stringTable, buffer);

        assertEquals(3, routes.size());

        assertEquals("Ligne 1", routes.name(0));
        assertEquals(Vehicle.TRAM, routes.vehicle(0));

        assertEquals("Ligne 2", routes.name(1));
        assertEquals(Vehicle.BUS, routes.vehicle(1));



        // Cas limites : Vérification des exceptions pour des indices invalides
        assertThrows(IndexOutOfBoundsException.class, () -> routes.name(2));
        assertThrows(IndexOutOfBoundsException.class, () -> routes.vehicle(4));
    }

    private static ByteBuffer byteBuffer(String hex) {
        return ByteBuffer
                .wrap(HexFormat.ofDelimiter(" ").parseHex(hex))
                .asReadOnlyBuffer();
    }

    private static final ByteBuffer ROUTES_1 =
            byteBuffer("00 00 00 01 00 02 00 03 00");

    private static final List<String> STRING_TABLE_1 =
            List.of("Ligne 1", "Ligne 2", "Ligne 3");

    @Test
    void bufferedRoutesSizeWorks() {
        var routes = new BufferedRoutes(STRING_TABLE_1, ROUTES_1);
        assertEquals(3, routes.size());
    }

    @Test
    void bufferedRoutesThrowsOnInvalidIndex() {
        var routes = new BufferedRoutes(STRING_TABLE_1, ROUTES_1);
        assertThrows(IndexOutOfBoundsException.class, () -> routes.name(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> routes.vehicle(3));
    }

   /* @Test
    void bufferedRoutesNameWorks() {
        var routes = new BufferedRoutes(STRING_TABLE_1, ROUTES_1);
        assertEquals("Ligne 1", routes.name(0));
        assertEquals("Ligne 2", routes.name(1));
        assertEquals("Ligne 3", routes.name(2));
    }*/

    @Test
    void bufferedRoutesVehicleWorks() {
        var routes = new BufferedRoutes(STRING_TABLE_1, ROUTES_1);
        assertEquals(Vehicle.TRAM, routes.vehicle(0));
        assertEquals(Vehicle.TRAIN, routes.vehicle(1));
        assertEquals(Vehicle.TRAM, routes.vehicle(2));
    }

    private BufferedRoutes bufferedRoutes;
    private ByteBuffer buffer;
    private List<String> stringTable;

    @BeforeEach
    void setUp() {
        // Création d'une table de chaînes simulée
        stringTable = Arrays.asList("Bus 1", "Tram A", "Train X", "Metro 3", "Trolley B");

        // Allocation d'un buffer pour stocker les noms et types de véhicules (U16 + U8)
        buffer = ByteBuffer.allocate(10 * 3); // Simule 10 routes

        // Remplissage des données pour les tests
        for (int i = 0; i < 10; i++) {
            buffer.putShort((short) (i % 5)); // NAME_ID (index dans stringTable)
            buffer.put((byte) (i % Vehicle.values().length)); // KIND (index du véhicule)
        }

        // Création de l'objet BufferedRoutes
        bufferedRoutes = new BufferedRoutes(stringTable, buffer);
    }

    @Test
    void testVehicle() {
        assertEquals(Vehicle.values()[0], bufferedRoutes.vehicle(0));
        assertEquals(Vehicle.values()[1], bufferedRoutes.vehicle(1));
        assertEquals(Vehicle.values()[2], bufferedRoutes.vehicle(2));
    }

    @Test
    void testName() {
        assertEquals("Bus 1", bufferedRoutes.name(0));
        assertEquals("Tram A", bufferedRoutes.name(1));
        assertEquals("Train X", bufferedRoutes.name(2));
        assertEquals("Metro 3", bufferedRoutes.name(3));
        assertEquals("Trolley B", bufferedRoutes.name(4));
        assertEquals("Bus 1", bufferedRoutes.name(5)); // Vérification du cycle
    }

    @Test
    void testSize() {
        assertEquals(10, bufferedRoutes.size());
    }

    @Test
    void vehicleReturnsCorrectVehicleType() {
        ByteBuffer buffer = ByteBuffer.allocate(9)
                .putShort((short) 0).put((byte) 0) // TRAM
                .putShort((short) 1).put((byte) 1) // METRO
                .putShort((short) 2).put((byte) 2); // RAIL
        buffer.rewind();
        Routes routes = new BufferedRoutes(stringTable, buffer);

        assertEquals(Vehicle.TRAM, routes.vehicle(0));
        assertEquals(Vehicle.METRO, routes.vehicle(1));
        assertEquals(Vehicle.TRAIN, routes.vehicle(2));
    }

    @Test
    void nameThrowsExceptionForInvalidIndex() {
        ByteBuffer buffer = ByteBuffer.allocate(9)
                .putShort((short) 0).put((byte) 0)
                .putShort((short) 1).put((byte) 1)
                .putShort((short) 2).put((byte) 2);

        buffer.rewind();
        Routes routes = new BufferedRoutes(stringTable, buffer);

        assertThrows(IndexOutOfBoundsException.class, () -> routes.name(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> routes.name(3)); // Taille = 3
    }

    @Test
    void vehicleThrowsExceptionForInvalidIndex() {
        ByteBuffer buffer = ByteBuffer.allocate(9)
                .putShort((short) 0).put((byte) 0)
                .putShort((short) 1).put((byte) 1)
                .putShort((short) 2).put((byte) 2);

        buffer.rewind();
        Routes routes = new BufferedRoutes(stringTable, buffer);

        assertThrows(IndexOutOfBoundsException.class, () -> routes.vehicle(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> routes.vehicle(3)); // Taille = 3
    }


    // Exemple de données de routes
    // Format: NAME_ID (U16) | KIND (U8)
    private static final byte[] ROUTES_BYTES = {
            0x00, 0x05, 0x01,  // Ligne "Losanna" (index 5), METRO (1)
            0x00, 0x06, 0x00,  // Ligne "Palézieux" (index 6), TRAM (0)
            0x00, 0x03, 0x02   // Ligne "Ins" (index 3), TRAIN (2)
    };

    private static final List<String> STRING_TABLE = List.of(
            "1", "70", "Anet", "Ins", "Lausanne", "Losanna", "Palézieux"
    );

    @Test
    void sizeReturnsCorrectNumberOfRoutes() {
        ByteBuffer buffer = ByteBuffer.wrap(ROUTES_BYTES);
        BufferedRoutes routes = new BufferedRoutes(STRING_TABLE, buffer);

        assertEquals(3, routes.size());
    }

    @Test
    void nameReturnsCorrectRouteName1() {
        ByteBuffer buffer = ByteBuffer.wrap(ROUTES_BYTES);
        BufferedRoutes routes = new BufferedRoutes(STRING_TABLE, buffer);

        assertEquals("Losanna", routes.name(0));
        assertEquals("Palézieux", routes.name(1));
        assertEquals("Ins", routes.name(2));
    }

    @Test
    void vehicleReturnsCorrectVehicleType1() {
        ByteBuffer buffer = ByteBuffer.wrap(ROUTES_BYTES);
        BufferedRoutes routes = new BufferedRoutes(STRING_TABLE, buffer);

        assertEquals(Vehicle.METRO, routes.vehicle(0));
        assertEquals(Vehicle.TRAM, routes.vehicle(1));
        assertEquals(Vehicle.TRAIN, routes.vehicle(2));
    }

    @Test
    void throwsExceptionForInvalidIndex() {
        ByteBuffer buffer = ByteBuffer.wrap(ROUTES_BYTES);
        BufferedRoutes routes = new BufferedRoutes(STRING_TABLE, buffer);

        assertThrows(IndexOutOfBoundsException.class, () -> routes.name(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> routes.name(3));
        assertThrows(IndexOutOfBoundsException.class, () -> routes.vehicle(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> routes.vehicle(3));
    }


    @Test
    void constructorHandlesValidData() {
        ByteBuffer buffer = ByteBuffer.allocate(3 * 3); // 3 lignes, 3 octets chacune (U16 + U8)
        buffer.putShort(0, (short) 0);
        buffer.put(2, (byte) 1);
        buffer.putShort(3, (short) 2);
        buffer.put(5, (byte) 3);
        buffer.putShort(6, (short) 4);
        buffer.put(8, (byte) 5);


        List<String> stringTable = List.of("RouteA", "RouteB", "RouteC", "RouteD", "RouteE");
        BufferedRoutes routes = new BufferedRoutes(stringTable, buffer);


        assertEquals(3, routes.size());
    }


    @Test
    void nameReturnsCorrectValues() {
        ByteBuffer buffer = ByteBuffer.allocate(3 * 3);
        buffer.putShort(0, (short) 0);
        buffer.put(2, (byte) 1);
        buffer.putShort(3, (short) 2);
        buffer.put(5, (byte) 3);
        buffer.putShort(6, (short) 4);
        buffer.put(8, (byte) 5);


        List<String> stringTable = List.of("RouteA", "RouteB", "RouteC", "RouteD", "RouteE");
        BufferedRoutes routes = new BufferedRoutes(stringTable, buffer);


        assertEquals("RouteA", routes.name(0));
        assertEquals("RouteC", routes.name(1));
        assertEquals("RouteE", routes.name(2));
    }


    @Test
    void vehicleReturnsCorrectValues() {
        ByteBuffer buffer = ByteBuffer.allocate(3 * 3);
        buffer.putShort(0, (short) 0);
        buffer.put(2, (byte) 0); // TRAM
        buffer.putShort(3, (short) 1);
        buffer.put(5, (byte) 2); // TRAIN
        buffer.putShort(6, (short) 2);
        buffer.put(8, (byte) 5); // AERIAL_LIFT


        List<String> stringTable = List.of("RouteA", "RouteB", "RouteC");
        BufferedRoutes routes = new BufferedRoutes(stringTable, buffer);


        assertEquals(Vehicle.TRAM, routes.vehicle(0));
        assertEquals(Vehicle.TRAIN, routes.vehicle(1));
        assertEquals(Vehicle.AERIAL_LIFT, routes.vehicle(2));
    }


    @Test
    void nameThrowsExceptionForInvalidIndex1() {
        ByteBuffer buffer = ByteBuffer.allocate(3 * 3);
        List<String> stringTable = List.of("RouteA", "RouteB", "RouteC");
        BufferedRoutes routes = new BufferedRoutes(stringTable, buffer);


        assertThrows(IndexOutOfBoundsException.class, () -> routes.name(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> routes.name(3));
    }


    @Test
    void vehicleHandlesAllEnumCases() {
        ByteBuffer buffer = ByteBuffer.allocate(7 * 3);
        for (int i = 0; i < 7; i++) {
            buffer.putShort(i * 3, (short) i);
            buffer.put(i * 3 + 2, (byte) i);
        }


        List<String> stringTable = List.of("RouteA", "RouteB", "RouteC", "RouteD", "RouteE", "RouteF", "RouteG");
        BufferedRoutes routes = new BufferedRoutes(stringTable, buffer);


        assertEquals(Vehicle.TRAM, routes.vehicle(0));
        assertEquals(Vehicle.METRO, routes.vehicle(1));
        assertEquals(Vehicle.TRAIN, routes.vehicle(2));
        assertEquals(Vehicle.BUS, routes.vehicle(3));
        assertEquals(Vehicle.FERRY, routes.vehicle(4));
        assertEquals(Vehicle.AERIAL_LIFT, routes.vehicle(5));
        assertEquals(Vehicle.FUNICULAR, routes.vehicle(6));
    }

}
