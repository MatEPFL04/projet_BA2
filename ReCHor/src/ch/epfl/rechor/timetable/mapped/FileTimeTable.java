package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.nio.file.Path;

/**
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 * @param directory the directory where all the flattened files are located
 * @param stringTable a list of Strings for the ReChor project
 * @param stations a list of stations
 * @param stationAliases a list of stationAliases
 * @param platforms a list of platforms
 * @param routes a list of routes
 * @param transfers a list of transfers
 */

public record FileTimeTable (Path directory,
                            List<String> stringTable, Stations stations,
                            StationAliases stationAliases, Platforms platforms,
                            Routes routes, Transfers transfers) implements TimeTable {

    /**
     *
     * @param directory the directory in which the files are located
     * @return an instance of fileTimeTable with the buffered lists (for instance stations or platforms)
     * @throws IOException if accessing the files in the directory cause a problem
     */

    public static TimeTable in(Path directory) throws IOException {

        Path timetableDirectory = Path.of("timetable");

        Path strings = timetableDirectory.resolve("strings.txt");
        Path stationsPath = timetableDirectory.resolve("stations.bin");
        Path stationsAliasesPath = timetableDirectory.resolve("station-aliases.bin");
        Path platformsPath = timetableDirectory.resolve("platforms.bin");
        Path routesPath = timetableDirectory.resolve("routes.bin");
        Path transfersPath = timetableDirectory.resolve("transfers.bin");
        List <String> immutableStringList = List.copyOf(Files.readAllLines(strings,StandardCharsets.ISO_8859_1));

        ByteBuffer stationsBuffer = getBufferFromPath(stationsPath);
        ByteBuffer stationsAliasesBuffer = getBufferFromPath(stationsAliasesPath);
        ByteBuffer platformsBuffer = getBufferFromPath(platformsPath);
        ByteBuffer routesBuffer = getBufferFromPath(routesPath);
        ByteBuffer transfersBuffer = getBufferFromPath(transfersPath);

        BufferedStations stationsBuffered = new BufferedStations(immutableStringList, stationsBuffer);
        BufferedStationAliases stationsAliasesBuffered = new BufferedStationAliases(immutableStringList, stationsAliasesBuffer);
        BufferedPlatforms platformsBuffered = new BufferedPlatforms(immutableStringList, platformsBuffer);
        BufferedRoutes routesBuffered = new BufferedRoutes(immutableStringList, routesBuffer);
        BufferedTransfers transfersBuffered = new BufferedTransfers(transfersBuffer);

        return new FileTimeTable(directory, immutableStringList, stationsBuffered,
                stationsAliasesBuffered, platformsBuffered, routesBuffered, transfersBuffered);

    }

    private static ByteBuffer getBufferFromPath (Path path) throws IOException {
        try(FileChannel channel = FileChannel.open(path)) {
            return channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }
    }

    /**
     * method used to use binary files to get buffered trips for a given date
     * @param date the date for which trips should be retrieved.
     * @return buffered trips corresponding to a given date
     */

    @Override
    public Trips tripsFor(LocalDate date) {
        try {
            Path timetableDirectory = directory.resolve(date.toString());
            Path pathToSearchIn = timetableDirectory.resolve("trips.bin");
            return new BufferedTrips(stringTable, getBufferFromPath(pathToSearchIn));
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    /**
     * method used to use binary files to get buffered connections for a given date
     * @param date the date for which connections should be retrieved.
     * @return buffered connections corresponding to a given date
     */

    @Override
    public Connections connectionsFor(LocalDate date) {
        try {
            Path timetableDirectory = directory.resolve(date.toString());
            Path connectionsPath = timetableDirectory.resolve("connections.bin");
            Path connections_succPath = timetableDirectory.resolve("connections-succ.bin");
            return new BufferedConnections(getBufferFromPath(connectionsPath),getBufferFromPath(connections_succPath));

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
}
