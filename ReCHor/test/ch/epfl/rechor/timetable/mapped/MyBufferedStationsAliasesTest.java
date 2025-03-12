package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MyBufferedStationAliasesTest {

    @Test
    public void testAliasRetrieval() {
        List<String> stringTable = List.of("Losanna", "Lausanne");
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putShort(0, (short) 0);
        buffer.putShort(2, (short) 1);
        BufferedStationAliases aliases = new BufferedStationAliases(stringTable, buffer);
        assertEquals("Losanna", aliases.alias(0));
        assertEquals("Lausanne", aliases.stationName(0));
    }
}

