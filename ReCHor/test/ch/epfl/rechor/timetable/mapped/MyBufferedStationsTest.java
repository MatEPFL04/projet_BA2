package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;

class MyBufferedStationsTest {

    @Test
    public void testStationRetrieval() {
        List<String> stringTable = List.of("Lausanne", "Palézieux");
        ByteBuffer buffer = ByteBuffer.allocate(10 * 2);
        buffer.putShort(0, (short) 0);
        buffer.putInt(2, 7340032);
        buffer.putInt(6, 1073741824);
        BufferedStations stations = new BufferedStations(stringTable, buffer);
        assertEquals("Lausanne", stations.name(0));
        assertEquals(6.629092, stations.longitude(0), 1e-6);
        assertEquals(46.516792, stations.latitude(0), 1e-6);
    }
}