package ch.epfl.rechor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * This class represents an index of stop names in which it's possible to do researches.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public final class StopIndex {
     private final List <String> indexedStopNames;
     private final Map <String, String> alternativeNamesMap;

    /**
     *
     * @param stopName list of the stop's names to index
     * @param map map associating the alternative stop's names to their main one's
     */
    public StopIndex(List<String> stopName, Map<String, String> map){
        indexedStopNames = stopName;
        alternativeNamesMap = map;
    }

    /**
     * Method used to concert a query into a list of corresponding stops.
     *
     * @param query user's query when searching for an arrival station
     * @param maxNumberOfStops the maximum number of stations suggested for a query
     * @return list of the stop's names corresponding to the query
     */
    public List<String> stopsMatching(String query, int maxNumberOfStops) {
        Pattern space = Pattern.compile(" ");
        String[] subQueries = space.split(query);
        List<Pattern> subQueriesPatterns = new ArrayList<>();
        for (String subQuery : subQueries) {
            subQueriesPatterns.add(pattern(subQuery));
        }

        Stream <String> streamOfStops = indexedStopNames.stream();
        Stream <String> streamAliaseNames = alternativeNamesMap.keySet().stream();
        List <String> concatenedListOfStops =
                Stream.concat(streamOfStops, streamAliaseNames)
                        .filter(stop -> stopScore(subQueriesPatterns, stop) != -1)
                        .sorted((stopName1, stopName2) -> {
                            return Integer.compare(stopScore(subQueriesPatterns, stopName2),
                                    stopScore(subQueriesPatterns, stopName1));
                        })
                        .map(stopName -> alternativeNamesMap.getOrDefault(stopName,stopName))
                        .distinct()
                        .limit(maxNumberOfStops)
                        .toList();

        return concatenedListOfStops;
    }

    public static int stopScore (List <Pattern> patternList, String stopName) {
        int totalScore = 0;
        for (Pattern pattern : patternList) {
            Matcher matcher = pattern.matcher(stopName);
            if (matcher.find()) {
                int sequenceFound = matcher.end() - matcher.start();
                int currentSubQueryScore = (int) ((double) sequenceFound / stopName.length() * 100);
                if (matcher.start() == 0 || !Character.isLetter(stopName.charAt(matcher.start() - 1))) {
                    currentSubQueryScore *= 4;
                }
                if (matcher.end() == stopName.length() ||
                        !Character.isLetter(stopName.charAt(matcher.end()))) {
                    currentSubQueryScore *= 2;
                }
                totalScore += currentSubQueryScore;
            }
            else {
                return -1;
            }
        }
        return totalScore;
    }

    public static Pattern pattern(String subquery) {
        boolean caseSensitive = !subquery.equals(subquery.toLowerCase());
        Pattern pattern  =  Pattern.compile("[aeiouc]");
        String [] chunksOfSubRequest = pattern.splitWithDelimiters(subquery,0);

        StringBuilder sb = new StringBuilder();

        for (String chunk : chunksOfSubRequest) {
            switch (chunk) {
                case "a" -> sb.append("[aàâäá]");
                case "e" -> sb.append("[eéèêë]");
                case "i" -> sb.append("[iîïìí]");
                case "o" -> sb.append("[oôöòó]");
                case "u" -> sb.append("[uûùü]");
                case "c" -> sb.append("[cç]");
                default -> sb.append(Pattern.quote(chunk));
            }
        }

        return caseSensitive ? Pattern.compile(sb.toString()) :
                Pattern.compile(sb.toString(),
                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }



}

