package ch.epfl.rechor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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

     private static final Pattern SPACE_PATTERN = Pattern.compile(" ");
     private static final Pattern SPLITTING_PATTERN = Pattern.compile("[aeiouc]");

     private static final Map <String, String> CHAR_REPLACEMENT =
             new HashMap<>(Map.of("a", "[aàâäá]", "e",
                     "[eéèêë]", "i", "[iîïìí]", "o", "[oôöòó]",
                     "u", "[uûùü]", "c","[cç]" ));



     private record StopScores (String stopName, int score) { }

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

        /*

        String[] subQueries = SPACE_PATTERN.split(query);
        List<Pattern> subQueriesPatterns = new ArrayList<>();
        for (String subQuery : subQueries) {
            subQueriesPatterns.add(pattern(subQuery));
        }

        Stream <String> originalStopsStream = indexedStopNames.stream();
        Stream <String> aliaseNamesStream = alternativeNamesMap.keySet().stream();
        Stream <StopScores> namesToTakeIntoAccount = Stream.concat(originalStopsStream, aliaseNamesStream)
                .map(stopName -> stopScore(subQueriesPatterns, stopName))
                .filter(Objects::nonNull)
                .sorted((firstPair, secondPair))




                new ArrayList<>();
        indexedStopNames.forEach(nameStop -> {
            associatedScores.add(stopScore(subQueriesPatterns, nameStop));
        });
        alternativeNamesMap.keySet().forEach(aliasStopName -> {
            associatedScores.add(stopScore(subQueriesPatterns,aliasStopName));
        });

        Stream <String> stopScoresStream = associatedScores.stream()
                        .filter(Objects::nonNull)
                        .sorted((scorePair1, scorePair2) -> {
                            return Integer.compare(scorePair2.score(), scorePair1.score());
                        })
                        .map(StopScores::stopName)
                        .map(scorePair -> alternativeNamesMap.getOrDefault(scorePair, scorePair))
                        .distinct()
                        .limit(maxNumberOfStops);

        return stopScoresStream.toList();

         */

        return null;

    }

    public static StopScores stopScore (List <Pattern> patternList, String stopName) {
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
                return null;
            }
        }
        return new StopScores(stopName, totalScore);
    }

    public static Pattern pattern(String subquery) {
        boolean caseSensitive = !subquery.equals(subquery.toLowerCase());
        String toBeCompiled =
                Arrays.stream(SPLITTING_PATTERN.splitWithDelimiters(subquery,0))
                        .map(chunkOfLetters -> {
                            return CHAR_REPLACEMENT.getOrDefault
                                    (chunkOfLetters, Pattern.quote(chunkOfLetters));
                        })
                        .collect(Collectors.joining());


        return caseSensitive ? Pattern.compile(toBeCompiled) :
                Pattern.compile(toBeCompiled,
                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }



}

