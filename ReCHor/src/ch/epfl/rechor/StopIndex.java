package ch.epfl.rechor;

import ch.epfl.rechor.timetable.StationAliases;
import ch.epfl.rechor.timetable.Stations;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This class represents an index of stop names in which it's possible to do researches.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public class StopIndex {

    private StopIndex(List<String> stopNameIndex, Map<String, String> map){}

    public List<Stations> stopsMatching(String request, int maxNumberOfStops) {

        Pattern vowels = Pattern.compile("[aeiou]");

        return null;
    }
}
