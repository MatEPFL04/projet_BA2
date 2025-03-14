package ch.epfl.rechor.timetable.mapped;


import ch.epfl.rechor.journey.Vehicle;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
                "Route A", "Route B", "Route C", "Route D","Route E", "Route F"
        ));

        BufferedRoutes bufferedRoutes = new BufferedRoutes(stringTable,buffer);
        for (int i = 0; i < Vehicle.ALL.size(); i++) {
            Vehicle expectedVehicle = Vehicle.ALL.get(i);
            assertEquals(expectedVehicle, bufferedRoutes.vehicle(i));
        }
    }


}
