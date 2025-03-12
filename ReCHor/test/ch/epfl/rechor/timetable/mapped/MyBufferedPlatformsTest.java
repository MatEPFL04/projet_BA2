package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;

class MyBufferedPlatformsTest {

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
}

