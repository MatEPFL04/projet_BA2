package ch.epfl.rechor;

import ch.epfl.rechor.timetable.Stations;

import java.util.ArrayList;
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

    private StopIndex(List<String> stopName, Map<String, String> map){

        // call stopsMatching() pr associer query a un stop
    }

    public List<Stations> stopsMatching(String query, int maxNumberOfStops) {

        List<Stations> stopsQuery = new ArrayList<>();

        if(stopsQuery.size() > maxNumberOfStops) {
            throw new IndexOutOfBoundsException();
        }

        Pattern e = Pattern.compile("[eéèêë]");
        Pattern c = Pattern.compile("[cç]");
        Pattern a = Pattern.compile("[aàâäá]");
        Pattern i = Pattern.compile("[iîïìí]");
        Pattern o = Pattern.compile("[oôöòó]");
        Pattern u = Pattern.compile("[uûùü]");

        return null;
    }
}