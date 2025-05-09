package ch.epfl.rechor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class MyStopIndexTest {
    private static StopIndex index;
    @BeforeAll
    static void setup() {
        List<String> stops = List.of(
                "Renens VD",
                "Renens EPFL",
                "Mézières FR, village",
                "Mézières VD, village",
                "Charleville-Mézières",
                "Gare Centrale"
        );
        Map<String, String> alternates = Map.of(
                "Losanna", "Lausanne",
                "Grueyères", "Gruyères"
        );
        index = new StopIndex(stops, alternates);
    }

    @Test
    void emptyWhenNoMatch() {
        List<String> result = index.stopsMatching("foobar", 5);
        assertTrue(result.isEmpty(), "Doit être vide s'il n'y a aucune correspondance");
    }

    @Test
    void partialMatchesLessThanLimit() {
        // "Renens" correspond à 2 arrêts, limite fixée à 10
        List<String> result = index.stopsMatching("Renens", 10);
        assertEquals(2, result.size(), "Doit retourner exactement les 2 arrêts Renens");
        assertTrue(result.contains("Renens VD") && result.contains("Renens EPFL"));
        assertEquals(2, new HashSet<>(result).size(), "Pas de doublons");
    }

    @Test
    void limitIsRespectedWhenManyMatches() {
        // 'e' est présent dans tous les arrêts (6), limite = 3
        List<String> result = index.stopsMatching("e", 3);
        assertEquals(3, result.size(), "Ne doit pas dépasser la limite de 3");
        assertEquals(3, new HashSet<>(result).size(), "Pas de doublons");
    }

    @Test
    void complexSubqueryOrder() {
        // sous-requêtes ren et epfl (ordre inversé)
        List<String> r1 = index.stopsMatching("ren epfl", 5);
        List<String> r2 = index.stopsMatching("epfl ren", 5);
        assertEquals(r1, r2, "Ordre des sous-requêtes ne change pas le résultat");
        assertFalse(r1.isEmpty(), "Doit au moins trouver Renens EPFL");
        assertEquals("Renens EPFL", r1.get(0), "Renens EPFL doit être en tête pour cette requête");
    }

    @Test
    void accentAndCaseInsensitivity() {
        // test ignore accents et casse
        List<String> r1 = index.stopsMatching("mezieres fr", 5);
        List<String> r2 = index.stopsMatching("MÉZIERES FR", 5);
        assertTrue(r1.contains("Mézières FR, village"));
    }
    @Test
    void testUppercaseQueryIsCaseSensitive() {
        // Query with all uppercase should become case-sensitive: no matches if stops don't have uppercase sequence
        List<String> resultAllUpper = index.stopsMatching("RENENS", 5);
        assertTrue(resultAllUpper.isEmpty(), "Requête entièrement en majuscules doit être sensible à la casse et ne pas matcher Renens");
        // Query with initial uppercase only should match (case-sensitive respects exact pattern)
        List<String> resultInitUpper = index.stopsMatching("Renens", 5);
        assertFalse(resultInitUpper.isEmpty(), "Requête avec majuscule initiale devrait matcher exactement Renens");
        assertEquals("Renens VD", resultInitUpper.get(0), "Premier résultat doit être Renens VD");
    }

    @Test
    void testMixedCaseSubquery() {
        // Mixed case with uppercase in middle should be case-sensitive
        List<String> resultMixed = index.stopsMatching("ReNens", 5);
        assertTrue(resultMixed.isEmpty(), "Majuscule inattendue au milieu doit bloquer le match");
    }
    @Test
    void testAlternateNameLowerCaseAndUppercaseBehavior() {
        // lowercase alternate name query (insensitive)
        List<String> lower = index.stopsMatching("losanna", 5);
        assertFalse(lower.isEmpty(), "Lausanne doit être trouvé en lowercase");
        assertEquals("Lausanne", lower.get(0));

        // uppercase alternate name query (case-sensitive -> no match)
        List<String> upperAll = index.stopsMatching("LOSANNA", 5);
        assertTrue(upperAll.isEmpty(), "Pas de match si la requête est en majuscules complètes pour alternates");

        // mixed-case alternate name query (case-sensitive -> no match)
        List<String> mixed = index.stopsMatching("LoSaNNa", 5);
        assertTrue(mixed.isEmpty(), "Pas de match si la requête contient des majuscules inattendues");
    }

    @Test
    void testDoubleSpacesAndTrimming() {
        // double spaces should be treated like single spaces
        List<String> doubleSpace = index.stopsMatching("Renens  EPFL", 5);
        List<String> singleSpace = index.stopsMatching("Renens EPFL", 5);
        assertEquals(singleSpace, doubleSpace, "Les doubles espaces doivent être ignorés");

        // leading/trailing spaces should be trimmed
        List<String> padded = index.stopsMatching("  Renens EPFL  ", 5);
        assertEquals(singleSpace, padded, "Les espaces en début/fin doivent être ignorés");
    }

    @Test
    void testEmptyAndWhitespaceOnlyQuery() {

        // query with only spaces returns empty
        List<String> onlySpaces = index.stopsMatching("   ", 5);
        assertTrue(onlySpaces.isEmpty(), "Requête ne contenant que des espaces doit renvoyer vide");


        // empty string query returns empty
        List<String> empty = index.stopsMatching("", 7);
        System.out.println(empty.size());
        assertTrue(empty.isEmpty(), "Requête vide doit renvoyer vide");
    }

}

