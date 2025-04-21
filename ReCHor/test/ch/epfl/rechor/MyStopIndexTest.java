package ch.epfl.rechor;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class MyStopIndexTest {
    @Test
    public void scoringIsCorrect() {
        String query = "mez vil";
        String stopName1 = "Mézières FR, village";
        String stopName2 = "Mézières VD, village";
        String stopName3 = "Mézery-près-Donneloye, village";
        String stopName4 = "Charleville-Mézières";

        List <Pattern> listPatterns = List.of(StopIndex.pattern("mez"), StopIndex.pattern("vil"));
        assertEquals(120, StopIndex.stopScore(listPatterns, stopName1));
        assertEquals(120, StopIndex.stopScore(listPatterns, stopName2));
        assertEquals(80, StopIndex.stopScore(listPatterns, stopName3));
        assertEquals(75, StopIndex.stopScore(listPatterns, stopName4));
    }

    @Test
    public void stopIndexWorksWithAliases() {
        StopIndex stopIndex = new StopIndex(List.of("Losanna"),
                Map.of("Losanna", "Lausanne"));
        assertEquals("[Lausanne]",(stopIndex.stopsMatching("sann",6)).toString());
    }

    @Test
    public void stopIndexCorrectlySortsElementsBasedOnScore() {
        StopIndex stopIndex = new StopIndex(List.of("a","ab", "abc", "abcd", "bcd"),
                Map.of());
        System.out.println();
        assertEquals("bcd", (stopIndex.stopsMatching("b",6)).getFirst());
        assertEquals("ab", (stopIndex.stopsMatching("b",6)).get(1));
        assertEquals("abc", (stopIndex.stopsMatching("b",6)).get(2));
        assertEquals("abcd", (stopIndex.stopsMatching("b",6)).get(3));
    }

    @Test
    public void stopsMatchingCorrectlyWorksWithUpperCasesAndAccents() {
        StopIndex stopIndex = new StopIndex(List.of("Mézières FR, village"), Map.of());

        assertTrue(stopIndex.stopsMatching("meZ vil", 2).isEmpty());
        assertTrue(stopIndex.stopsMatching("mèz vil", 2).isEmpty());
        assertFalse(stopIndex.stopsMatching("mez vil", 2).isEmpty());
    }
 }
