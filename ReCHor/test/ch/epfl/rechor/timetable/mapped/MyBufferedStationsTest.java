package ch.epfl.rechor.timetable.mapped;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.HexFormat;
import org.junit.jupiter.api.Test;

class MyBufferedStationsTest {

    private List<String> stringTable;
    private ByteBuffer buffer;
    private BufferedStations stations;

    @Test
    public void constructorCreatesValidObject() {
        List<String> stringTable = Arrays.asList("Lausanne", "Genève");
        ByteBuffer buffer = ByteBuffer.allocate(10);

        assertDoesNotThrow(() -> new BufferedStations(stringTable, buffer));
    }

    @Test
    public void nameReturnsCorrectValue() {
        List<String> stringTable = Arrays.asList("Lausanne", "Genève", "Berne");
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.putShort(0, (short) 2); // NAME_ID pointe vers "Berne"

        BufferedStations stations = new BufferedStations(stringTable, buffer);
        assertEquals("Berne", stations.name(0));
    }

    @Test
    public void longitudeReturnsCorrectValue() {
        List<String> stringTable = Collections.singletonList("Lausanne");
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.putInt(2, 123456789); // Longitude encodée

        BufferedStations stations = new BufferedStations(stringTable, buffer);
        double expected = 123456789 * (360.0 / Math.pow(2, 32));
        assertEquals(expected, stations.longitude(0), 1e-10);
    }

    @Test
    public void latitudeReturnsCorrectValue() {
        List<String> stringTable = Collections.singletonList("Lausanne");
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.putInt(6, 123456789); // Latitude encodée

        BufferedStations stations = new BufferedStations(stringTable, buffer);
        double expected = 123456789 * (360.0 / Math.pow(2, 32));
        assertEquals(expected, stations.latitude(0), 1e-10);
    }

    @Test
    public void sizeReturnsCorrectValue() {
        List<String> stringTable = Collections.singletonList("Lausanne");
        ByteBuffer buffer = ByteBuffer.allocate(20); // 2 éléments de 10 octets

        BufferedStations stations = new BufferedStations(stringTable, buffer);
        assertEquals(2, stations.size());
    }

    @Test
    public void nameWithNegativeIndexThrowsException() {
        List<String> stringTable = Collections.singletonList("Lausanne");
        ByteBuffer buffer = ByteBuffer.allocate(10);

        BufferedStations stations = new BufferedStations(stringTable, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> stations.name(-1));
    }

    @Test
    public void nameWithTooLargeIndexThrowsException() {
        List<String> stringTable = Collections.singletonList("Lausanne");
        ByteBuffer buffer = ByteBuffer.allocate(10);

        BufferedStations stations = new BufferedStations(stringTable, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> stations.name(1));
    }

    @Test
    public void longitudeWithInvalidIndexThrowsException() {
        List<String> stringTable = Collections.singletonList("Lausanne");
        ByteBuffer buffer = ByteBuffer.allocate(10);

        BufferedStations stations = new BufferedStations(stringTable, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> stations.longitude(2));
    }

    @Test
    public void latitudeWithInvalidIndexThrowsException() {
        List<String> stringTable = Collections.singletonList("Lausanne");
        ByteBuffer buffer = ByteBuffer.allocate(10);

        BufferedStations stations = new BufferedStations(stringTable, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> stations.latitude(-1));
    }

    @Test
    public void emptyBufferHasZeroSize() {
        List<String> stringTable = Collections.singletonList("Lausanne");
        ByteBuffer buffer = ByteBuffer.allocate(0);

        BufferedStations stations = new BufferedStations(stringTable, buffer);
        assertEquals(0, stations.size());
    }

    @Test
    public void multipleElementsAccessCorrectly() {
        List<String> stringTable = Arrays.asList("Lausanne", "Genève", "Berne");
        ByteBuffer buffer = ByteBuffer.allocate(20);
        // Premier élément
        buffer.putShort(0, (short) 0);  // NAME_ID = 0 (Lausanne)
        buffer.putInt(2, 123456789);    // Longitude
        buffer.putInt(6, 987654321);    // Latitude
        // Deuxième élément
        buffer.putShort(10, (short) 1); // NAME_ID = 1 (Genève)
        buffer.putInt(12, 111111111);   // Longitude
        buffer.putInt(16, 222222222);   // Latitude

        BufferedStations stations = new BufferedStations(stringTable, buffer);

        assertEquals("Lausanne", stations.name(0));
        assertEquals(123456789 * (360.0 / Math.pow(2, 32)), stations.longitude(0), 1e-10);
        assertEquals(987654321 * (360.0 / Math.pow(2, 32)), stations.latitude(0), 1e-10);

        assertEquals("Genève", stations.name(1));
        assertEquals(111111111 * (360.0 / Math.pow(2, 32)), stations.longitude(1), 1e-10);
        assertEquals(222222222 * (360.0 / Math.pow(2, 32)), stations.latitude(1), 1e-10);
    }

    @Test
    public void negativeCoordinatesAreCorrectlyConverted() {
        List<String> stringTable = Collections.singletonList("Lausanne");
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.putInt(2, -123456789);   // Longitude négative
        buffer.putInt(6, -987654321);   // Latitude négative

        BufferedStations stations = new BufferedStations(stringTable, buffer);

        double expectedLon = -123456789 * (360.0 / Math.pow(2, 32));
        double expectedLat = -987654321 * (360.0 / Math.pow(2, 32));

        assertEquals(expectedLon, stations.longitude(0), 1e-10);
        assertEquals(expectedLat, stations.latitude(0), 1e-10);
    }

    @Test
    void testPlatformsWithExampleData() {
        List<String> stringTable = List.of("1", "70", "Anet", "Ins", "Lausanne", "Losanna", "Palézieux");

        HexFormat hexFormat = HexFormat.ofDelimiter(" ");
        // Exemple de données pour les quais:
        // [ID Nom Quai, ID Station]
        // Quai "1" (index 0) pour la station d'ID 0 (Anet)
        // Quai "70" (index 1) pour la station d'ID 1 (Lausanne)
        byte[] bytes = hexFormat.parseHex("00 00 00 00 00 01 00 01");
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        BufferedPlatforms platforms = new BufferedPlatforms(stringTable, buffer);

        assertEquals(2, platforms.size());
        assertEquals("1", platforms.name(0));
        assertEquals(0, platforms.stationId(0));
        assertEquals("70", platforms.name(1));
        assertEquals(1, platforms.stationId(1));
    }

    @Test
    void testEmptyPlatformsBuffer() {
        List<String> stringTable = List.of("1", "70", "Anet");
        ByteBuffer buffer = ByteBuffer.allocate(0);

        BufferedPlatforms platforms = new BufferedPlatforms(stringTable, buffer);
        assertEquals(0, platforms.size());
    }

    @Test
    void testInvalidPlatformId() {
        List<String> stringTable = List.of("1", "70", "Anet", "Ins");

        HexFormat hexFormat = HexFormat.ofDelimiter(" ");
        byte[] bytes = hexFormat.parseHex("00 00 00 00");
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        BufferedPlatforms platforms = new BufferedPlatforms(stringTable, buffer);

        assertThrows(IndexOutOfBoundsException.class, () -> platforms.name(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> platforms.name(1));
        assertThrows(IndexOutOfBoundsException.class, () -> platforms.stationId(1));
    }

}