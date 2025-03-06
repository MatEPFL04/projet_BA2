package ch.epfl.rechor.timetable;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

class MyTimeTableTest {
    private final int stationCount = 1000;
    private final int platformCount = 2000;

    private final TimeTable timeTable = new TimeTable() {
        @Override
        public Stations stations() {
            return new Stations() {
                @Override
                public int size() {
                    return stationCount;
                }

                @Override
                public String name(int id) {
                    return null;
                }

                @Override
                public double longitude(int id) {
                    return 0;
                }

                @Override
                public double latitude(int id) {
                    return 0;
                }
            };
        }

        @Override
        public Platforms platforms() {
            return new Platforms() {
                @Override
                public int size() {
                    return platformCount;
                }

                @Override
                public String name(int id) {
                    return "Platform " + (id - stationCount);
                }

                @Override
                public int stationId(int id) {
                    return id - stationCount;
                }
            };
        }


        @Override
        public StationAliases stationAliases() {
            return null;
        }

        @Override
        public Routes routes() {
            return null;
        }

        @Override
        public Transfers transfers() {
            return null;
        }

        @Override
        public Trips tripsFor(LocalDate date) {
            return null;
        }

        @Override
        public Connections connectionsFor(LocalDate date) {
            return null;
        }
    };

    @Test
    void testIsStationId() {
        for (int i = 0; i < stationCount; i++) {
            assertTrue(timeTable.isStationId(i));
        }

        for (int i = stationCount; i < stationCount + platformCount; i++) {
            assertFalse(timeTable.isStationId(i));
        }

        assertFalse(timeTable.isStationId(-1));
    }

    @Test
    void testIsPlatformId() {
        for (int i = 0; i < stationCount; i++) {
            assertFalse(timeTable.isPlatformId(i));
        }

        for (int i = stationCount; i < stationCount + platformCount; i++) {
            assertTrue(timeTable.isPlatformId(i));
        }

        assertFalse(timeTable.isPlatformId(-1));
        assertFalse(timeTable.isPlatformId(stationCount + platformCount));
    }

    @Test
    void testStationId() {
        for (int i = 0; i < stationCount; i++) {
            assertEquals(i, timeTable.stationId(i));
        }

        for (int i = stationCount; i < stationCount + platformCount; i++) {
            assertEquals(i - stationCount, timeTable.stationId(i));
        }
    }

    @Test
    void testPlatformName() {
        for (int i = 0; i < stationCount; i++) {
            assertNull(timeTable.platformName(i));
        }

        for (int i = stationCount; i < stationCount + platformCount; i++) {
            assertEquals("Platform " + (i - stationCount), timeTable.platformName(i));
        }
    }

}