package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MyBufferedPlatformsTest {

    @Test
    public void platformFieldsAreAccessedCorrectly () {
        HexFormat hexFormat = HexFormat.ofDelimiter(" ");
        byte[] platformArray = hexFormat.parseHex("00 00 00 00 00 01 00 00 00 00 00 01");
        byte[] stationArray = hexFormat.parseHex("00 04 04 b6 ca 14 21 14 1f a1 00 06 04 dc cc 12 21 18 da 03");

        List <String> stringList = new ArrayList<>(Arrays.asList("1","70","Anet","Ins","Lausanne","Losanna","Palézieux"));
        BufferedPlatforms bufferedPlatforms = new BufferedPlatforms(stringList,ByteBuffer.wrap(platformArray));
        BufferedStations bufferedStations = new BufferedStations(stringList,ByteBuffer.wrap(stationArray));
        assertEquals("1", bufferedPlatforms.name(0));
        int idxParentStationInStationArray = bufferedPlatforms.stationId(2);
        String nameOfParentStation = bufferedStations.name(idxParentStationInStationArray);
        assertEquals("Palézieux", nameOfParentStation);


    }

    @Test
    public void testPlatformRetrieval() {
        List<String> stringTable = List.of("1", "70");
        ByteBuffer buffer = ByteBuffer.allocate(4 * 2);
        buffer.putShort(0, (short) 0);
        buffer.putShort(2, (short) 0);
        BufferedPlatforms platforms = new BufferedPlatforms(stringTable, buffer);
        assertEquals("1", platforms.name(0));
        assertEquals(0, platforms.stationId(0));
    }

    @Test
    public void constructorCreatesValidObject() {
        List<String> stringTable = Arrays.asList("Quai A", "Quai B");
        ByteBuffer buffer = ByteBuffer.allocate(4);

        assertDoesNotThrow(() -> new BufferedPlatforms(stringTable, buffer));
    }

    @Test
    public void nameReturnsCorrectValue() {
        List<String> stringTable = Arrays.asList("Quai A", "Quai B", "Quai C");
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putShort(0, (short) 2); // NAME_ID pointe vers "Quai C"

        BufferedPlatforms platforms = new BufferedPlatforms(stringTable, buffer);
        assertEquals("Quai C", platforms.name(0));
    }

    @Test
    public void stationIdReturnsCorrectValue() {
        List<String> stringTable = Collections.singletonList("Quai");
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putShort(2, (short) 42); // STATION_ID vaut 42

        BufferedPlatforms platforms = new BufferedPlatforms(stringTable, buffer);
        assertEquals(42, platforms.stationId(0));
    }

    @Test
    public void sizeReturnsCorrectValue() {
        List<String> stringTable = Collections.singletonList("Quai");
        ByteBuffer buffer = ByteBuffer.allocate(8); // Contient 2 éléments de 4 octets

        BufferedPlatforms platforms = new BufferedPlatforms(stringTable, buffer);
        assertEquals(2, platforms.size());
    }

    @Test
    public void multipleElementsAccessCorrectly() {
        List<String> stringTable = Arrays.asList("Quai A", "Quai B", "Quai C");
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putShort(0, (short) 0); // Première plateforme, NAME_ID = 0
        buffer.putShort(2, (short) 10); // Première plateforme, STATION_ID = 10
        buffer.putShort(4, (short) 1); // Deuxième plateforme, NAME_ID = 1
        buffer.putShort(6, (short) 20); // Deuxième plateforme, STATION_ID = 20

        BufferedPlatforms platforms = new BufferedPlatforms(stringTable, buffer);
        assertEquals("Quai A", platforms.name(0));
        assertEquals(10, platforms.stationId(0));
        assertEquals("Quai B", platforms.name(1));
        assertEquals(20, platforms.stationId(1));
    }

    @Test
    public void emptyBufferHasZeroSize() {
        List<String> stringTable = Collections.singletonList("Vide");
        ByteBuffer buffer = ByteBuffer.allocate(0);

        BufferedPlatforms platforms = new BufferedPlatforms(stringTable, buffer);
        assertEquals(0, platforms.size());
    }

    @Test
    public void nameWithNegativeIndexThrowsException() {
        List<String> stringTable = Collections.singletonList("Quai");
        ByteBuffer buffer = ByteBuffer.allocate(4);

        BufferedPlatforms platforms = new BufferedPlatforms(stringTable, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> platforms.name(-1));
    }

    @Test
    public void nameWithTooLargeIndexThrowsException() {
        List<String> stringTable = Collections.singletonList("Quai");
        ByteBuffer buffer = ByteBuffer.allocate(4);

        BufferedPlatforms platforms = new BufferedPlatforms(stringTable, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> platforms.name(1));
    }

    @Test
    public void stationIdWithNegativeIndexThrowsException() {
        List<String> stringTable = Collections.singletonList("Quai");
        ByteBuffer buffer = ByteBuffer.allocate(4);

        BufferedPlatforms platforms = new BufferedPlatforms(stringTable, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> platforms.stationId(-1));
    }

    @Test
    public void stationIdWithTooLargeIndexThrowsException() {
        List<String> stringTable = Collections.singletonList("Quai");
        ByteBuffer buffer = ByteBuffer.allocate(4);

        BufferedPlatforms platforms = new BufferedPlatforms(stringTable, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> platforms.stationId(1));
    }
}

