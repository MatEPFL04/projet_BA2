package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;

import java.util.HexFormat;

public class MyBufferedTransfersTest {
    @Test
    void arrivingAt() {
        HexFormat hexFormat = HexFormat.ofDelimiter(" ");
        byte[] platformArray = hexFormat.parseHex("00 00 00 00 00 01 00 00 00 00 00 01");
    }
}
