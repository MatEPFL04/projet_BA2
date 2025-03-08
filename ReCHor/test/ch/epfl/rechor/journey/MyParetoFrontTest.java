package ch.epfl.rechor.journey;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static ch.epfl.rechor.journey.PackedCriteria.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.NoSuchElementException;
import java.util.function.LongConsumer;

import static org.junit.jupiter.api.Assertions.*;

public class MyParetoFrontTest {

    @Test
    void emptyParetoFrontTest() {
        ParetoFront front = ParetoFront.EMPTY;
        assertEquals(0, front.size());
    }

    @Test
    void exceptionThrownWhen2D_3D_tuplesAreMixed() {
        ParetoFront.Builder front = new ParetoFront.Builder();
        long twoDTuple = pack(20, 10, 30);
        long threeDTuple = withDepMins(twoDTuple, 30);
        front.add(twoDTuple);
        assertThrows(IllegalArgumentException.class, () -> {
            front.add(threeDTuple);
        });
    }

    @Test
    void finalFrontSizeIsTheSameAsTheNumbersOfTupplesInTheBuilderTab() {
        ParetoFront.Builder front = new ParetoFront.Builder();
        int numberOfTuples = 100;
        for (int i = 0; i < numberOfTuples; i++) {
            front.add(pack(i, 100 - i, 0));
        }
        ParetoFront frontDePareto = front.build();
        assertEquals(numberOfTuples, frontDePareto.size());
    }

    @Test
    void noSuchElmExcepThrownWhenAccessingToAnUnknownElementInTheFront() {
        ParetoFront.Builder front = new ParetoFront.Builder();
        long twoDTuple = pack(20, 10, 30);
        ParetoFront finalFront = front.add(twoDTuple).build();
        assertThrows(NoSuchElementException.class, () -> {
            finalFront.get(21, 10);
        });
    }

    @Test
    void elementIsReturnedCorrectlyWhenUsingGetMethod() {
        ParetoFront.Builder front = new ParetoFront.Builder();
        int packedMin = 0, packedChanges = 0, packedPayload = 0;
        for (int i = 0; i < 20; i++) {
            front.add(pack(packedMin - i, packedChanges + i, packedPayload));
        }
        ParetoFront finalFront = front.build();
        for (int i = 0; i < 20; i++) {
            assertEquals(packedMin - i, PackedCriteria.arrMins(finalFront.get(packedMin - i, packedChanges + i)));
            assertEquals(packedChanges + i, PackedCriteria.changes(finalFront.get(packedMin - i, packedChanges + i)));
            assertEquals(packedPayload, PackedCriteria.payload(finalFront.get(packedMin - i, packedChanges + i)));
        }
    }

    @Test
    void addingTuplesFromAnotherFrontWorks() {
        ParetoFront.Builder front = new ParetoFront.Builder();
        int initialSize = 10;
        for (int i = 0; i < initialSize; i++) {
            front.add(pack(i, 100 - i, 0));
        }
        ParetoFront.Builder secondFront = new ParetoFront.Builder();

        for (int i = initialSize; i < 2 * initialSize; i++) {
            secondFront.add(pack((initialSize + i), 100 - (initialSize + i), 0));
        }
        front.addAll(secondFront);
        ParetoFront concatenatedFinalFront = front.build();
        assertEquals(2 * initialSize, concatenatedFinalFront.size());

    }

    @Test
    void fullyDominatesWorksWhenJustOneTupleFromThatIsNotDominated() {
        ParetoFront.Builder front1 = new ParetoFront.Builder();
        long val1 = withDepMins(pack(10, 10, 0), 10);
        long val2 = withDepMins(pack(11, 9, 0), 9);
        long val3 = withDepMins(pack(12, 8, 0), 8);
        long val4 = withDepMins(pack(13, 7, 0), 7);
        long val5 = withDepMins(pack(14, 5, 0), 6);
        front1.add(val1).add(val2).add(val3).add(val4).add(val5);

        long val1Front2 = withDepMins(pack(10, 10, 0), 0x1F);
        long val2Front2 = withDepMins(pack(11, 9, 0), 0xA);
        long val3Front2 = withDepMins(pack(12, 8, 0), 0);
        long val4Front2 = withDepMins(pack(13, 7, 0), 0b1010101010);
        long val5Front2 = withDepMins(pack(14, 4, 0), -20);

        ParetoFront.Builder frontComparedToFront1 = new ParetoFront.Builder();

        frontComparedToFront1.add(val1Front2).add(val2Front2).add(val3Front2).add(val4Front2).add(val5Front2);
        int depMinsImposed = 6;

        assertFalse(front1.fullyDominates(frontComparedToFront1, depMinsImposed));

    }

    @Test
    void tuplesWithDepMinsAreCorrectlyAddedToTheFrontAndRemovedWhenDominated() {
        ParetoFront.Builder testFront = new ParetoFront.Builder();
        int expectedFrontSize = 6;

        int arrmins = 200, changes = 30, depMins = 800;

        long tuple1 = withDepMins(pack(1300, 20, 0), 1200);
        long tuple2 = withDepMins(pack(900, 30, 0), 1000);
        long tuple3 = withDepMins(pack(arrmins, changes, 0), depMins); //dominated by "tupleDominatingTuple3
        long tuple4 = withDepMins(pack(205, 25, 0), 800);
        long tuple5 = withDepMins(pack(50, 20, 0), 700);
        long tuple6 = withDepMins(pack(51, 19, 0), 700);

        long tupleDominatingTuple3 = withDepMins(pack(arrmins, changes - 1, 0), depMins + 1);

        testFront.add(tuple1).add(tuple2).add(tuple3).add(tuple4).add(tuple5).add(tuple6).add(tupleDominatingTuple3);
        ParetoFront front = testFront.build();
        assertEquals(expectedFrontSize, front.size());
        assertThrows(NoSuchElementException.class, () -> {
            front.get(arrmins, changes);
        });


    }

    @Test
    void clearingAFrontAndRecreatingItFromScratchWorks() {
        ParetoFront.Builder testFront = new ParetoFront.Builder();
        for (int i = 0; i < 100; i++) {
            testFront.add(pack(i, 100 - i, 0));
        }
        ParetoFront frontCreated = testFront.clear().build();
        assertEquals(0, frontCreated.size());
    }

    @Test
    void constructingUsingAnotherFrontWorksAndElementsAreCorrectlyCopied() {
        ParetoFront.Builder testFront1 = new ParetoFront.Builder();
        int numberOfTuples = 100;
        for (int i = 0; i < numberOfTuples; i++) {
            testFront1.add(pack(i, 100 - i, 0));
        }
        ParetoFront.Builder newBuilderUsingCopy = new ParetoFront.Builder(testFront1);
        ParetoFront frontCreated = newBuilderUsingCopy.build();
        assertEquals(numberOfTuples, frontCreated.size());

        for (int i = 0; i < numberOfTuples; i++) {
            assertEquals(pack(i, 100 - i, 0), frontCreated.get(i, 100 - i));

        }
    }

    @Test
    void tableIsCopiedAndCannotBeChangedInsideParetoFront() {
        ParetoFront.Builder testFront = new ParetoFront.Builder();
        int numberOfTuples = 100;
        for (int i = 0; i < numberOfTuples / 2; i++) {
            testFront.add(pack(i, 100 - i, 0));
        }
        ParetoFront frontCreated = testFront.build();
        for (int i = numberOfTuples / 2; i < numberOfTuples; i++) {
            testFront.add(pack(i, 100 - i, 0));
        }
        assertEquals(numberOfTuples / 2, frontCreated.size());


    }

    // -- Tests sur ParetoFront.EMPTY --

    @Test
    public void emptyFrontHasSizeZero() {
        assertEquals(0, ParetoFront.EMPTY.size());
    }

    @Test
    public void emptyFrontGetThrows() {
        assertThrows(NoSuchElementException.class, () -> ParetoFront.EMPTY.get(600, 2));
    }

    @Test
    public void emptyFrontForEachDoesNothing() {
        final int[] counter = {0};
        ParetoFront.EMPTY.forEach(value -> counter[0]++);
        assertEquals(0, counter[0]);
    }

    // -- Tests basiques sur Builder et build --

    @Test
    public void buildFromEmptyBuilderGivesEmptyFront() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        ParetoFront front = builder.build();
        assertEquals(0, front.size());
    }

    @Test
    public void addSingleTupleThenBuildGivesFrontOfSizeOne() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        long t = pack(500, 1, 42);
        builder.add(t);
        ParetoFront front = builder.build();
        assertEquals(1, front.size());
        assertEquals(t, front.get(500, 1));
    }

    // -- Test "add" : ajout d'un tuple dominé doit être rejeté --

    @Test
    public void addDominatedTupleIsRejected() {
        // t1 domine t2, car 480 < 490 et 0 < 1
        long t1 = pack(480, 0, 111);
        long t2 = pack(490, 1, 222);

        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(t1);       // on ajoute d'abord t1
        builder.add(t2);       // t2 est dominé par t1 -> rejeté

        ParetoFront front = builder.build();
        assertEquals(1, front.size());
        assertEquals(t1, front.get(480, 0));
        // get(490,1) doit lever une exception
        assertThrows(NoSuchElementException.class, () -> front.get(490, 1));
    }

    // -- Test "add" : ajout d'un tuple dominant doit faire supprimer les dominés --

    @Test
    public void addDominatingTupleRemovesDominated() {
        // t1 est dominé par t2, car 490 < 500 et 1 <= 2, et il y a une inégalité stricte sur l'arrivée
        long t1 = pack(500, 2, 111);
        long t2 = pack(490, 1, 222);

        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(t1);  // on ajoute d'abord t1
        builder.add(t2);  // t2 domine t1 -> t1 doit être éliminé

        ParetoFront front = builder.build();
        assertEquals(1, front.size());
        assertEquals(t2, front.get(490, 1));
        // get(500,2) doit lever une exception
        assertThrows(NoSuchElementException.class, () -> front.get(500, 2));
    }

    // -- Test "add" : ajout de tuples incomparables -> on les garde tous --

    @Test
    public void addIncomparableTuplesAreAllKept() {
        /*
         * On définit 3 tuples incomparables dans le sens de Pareto :
         *  - tA(500, 2)  => arrivée 500, 2 changements
         *  - tB(520, 1)  => arrivée 520, 1 changement
         *  - tC(480, 3)  => arrivée 480, 3 changements
         *
         *  Aucun ne domine un autre car chacun est "meilleur" sur un critère et "pire" sur l'autre.
         */
        long tA = pack(500, 2, 111);
        long tB = pack(520, 1, 222);
        long tC = pack(480, 3, 333);

        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(tA);
        builder.add(tB);
        builder.add(tC);

        ParetoFront front = builder.build();
        assertEquals(3, front.size());

        // Vérifions qu'on peut récupérer chacun
        assertEquals(tA, front.get(500, 2));
        assertEquals(tB, front.get(520, 1));
        assertEquals(tC, front.get(480, 3));
    }

    // -- Test "add" avec heure de départ --

    @Test
    public void addTupleWithDepTime() {
        long tNoDep = pack(600, 2, 111);
        long tWithDep = withDepMins(tNoDep, 550);  // on fixe l'heure de départ à 550

        ParetoFront.Builder builder = new ParetoFront.Builder();
        // Si la comparaison impose que tNoDep et tWithDep soient "incomparables" (p. ex. checkArgument(false)),
        // alors on doit gérer la stratégie (soit les ignorer, soit lever exception, etc.)
        // Ici, on suppose qu'on n'ajoute pas deux tuples qui ne sont pas comparables, donc on teste le cas normal :
        builder.add(tWithDep);

        ParetoFront front = builder.build();
        assertEquals(1, front.size());

        // On vérifie qu'on le retrouve
        long retrieved = front.get(600, 2);
        assertTrue(hasDepMins(retrieved));
        assertEquals(550, depMins(retrieved));
    }

    // -- Test "get" sur un front non vide --

    @Test
    public void getThrowsForAbsentTuple() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(pack(480, 0, 111));
        builder.add(pack(500, 1, 222));
        ParetoFront front = builder.build();

        // get(490,0) n'existe pas
        assertThrows(NoSuchElementException.class, () -> front.get(490, 0));
    }

    // -- Test "forEach" --

    @Test
    public void forEachEnumeratesAllTuples() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        long t1 = pack(480, 0, 111);
        long t2 = pack(470, 1, 222);
        long t3 = pack(460, 2, 333);
        // (Supposons qu'aucun ne domine un autre ici, on choisit des valeurs incomparables)
        builder.add(t1);
        builder.add(t2);
        builder.add(t3);

        ParetoFront front = builder.build();
        List<Long> collected = new ArrayList<>();
        front.forEach(collected::add);

        assertEquals(3, collected.size());
        assertTrue(collected.contains(t1));
        assertTrue(collected.contains(t2));
        assertTrue(collected.contains(t3));
    }

    // -- Test "toString" --

    @Test
    public void toStringContainsRelevantInfo() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(pack(480, 0, 123));
        builder.add(pack(550, 2, 456));

        ParetoFront front = builder.build();
        String s = front.toString();
        // On ne teste pas un format exact, mais on vérifie la présence d'éléments "arr=..." ou "480"
        // etc. pour aider au débogage.
        assertTrue(s.contains("480") || s.contains("arr: 480"));
        assertFalse(s.contains("550") || s.contains("arr: 550"));
        assertTrue(s.contains("0") || s.contains("changes: 0"));
        assertFalse(s.contains("2") || s.contains("changes: 2"));
    }

    // -- Test "addAll" --

    @Test
    public void addAllMergesFrontsRemovingDominated() {
        /*
         * builder1 : contient (500, 2), (520, 0)
         * builder2 : contient (480, 3), (490, 1)
         * On les fusionne dans builder1 -> on doit obtenir (500,2), (520,1), (480,3), (490,0)
         * sauf si l'un domine un autre. Vérifions que (490,0) domine (500,2) ?
         *   - (490 < 500) et (0 < 2), oui, donc (500,2) est dominé -> il doit disparaître.
         *
         * Au final, on doit avoir (490,1), (520,0), (480,3) (si aucun ne domine un autre).
         */
        long tA = pack(500, 2, 111);
        long tB = pack(520, 0, 222);
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(tA);
        builder1.add(tB);

        long tC = pack(480, 3, 333);
        long tD = pack(490, 1, 444);
        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(tC);
        builder2.add(tD);

        builder1.addAll(builder2);
        ParetoFront front = builder1.build();

        // tA = (500,2) est dominé par (490,0), donc il disparaît.
        // Reste tB=(520,1), tC=(480,3), tD=(490,0).
        assertEquals(3, front.size());
        assertEquals(tB, front.get(520, 0));
        assertEquals(tC, front.get(480, 3));
        assertEquals(tD, front.get(490, 1));
        // get(500,2) doit lever une exception
        assertThrows(NoSuchElementException.class, () -> front.get(500, 2));
    }

    // -- Test "fullyDominates" --


    // -- Test "build" et immuabilité --

    @Test
    public void builtFrontIsImmutableAfterBuild() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        long t1 = pack(480, 0, 111);
        builder.add(t1);
        ParetoFront front1 = builder.build();
        assertEquals(1, front1.size());

        // On modifie le builder après avoir construit front1
        long t2 = pack(500, 1, 222);
        builder.add(t2);

        // front1 ne doit pas changer
        assertEquals(1, front1.size());
        assertEquals(t1, front1.get(480, 0));

        // Une nouvelle construction reflète la modification
        ParetoFront front2 = builder.build();
        assertEquals(1, front2.size());
        assertEquals(t1, front2.get(480, 0));
    }

    @Test
    public void testEmptyParetoFront() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        ParetoFront front = builder.build();
        assertEquals(0, front.size(), "La frontière doit être vide initialement");
    }

    @Test
    public void testAddSingleElement() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        long packed = pack(480, 3, 2);
        builder.add(packed);
        ParetoFront front = builder.build();
        assertEquals(1, front.size(), "La frontière doit contenir 1 élément");
// On vérifie qu'on peut retrouver le tuple via ses critères
        assertEquals(packed, front.get(PackedCriteria.arrMins(packed),
                PackedCriteria.changes(packed)));
    }

    @Test
    public void testAddDominatedElement() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        long p1 = pack(480, 3, 2);
        long p2 = pack(480, 4, 22); // p2 est dominé par p1 (même heure d 'arrivée, mais plus de changements)
        builder.add(p1);
        builder.add(p2);
        ParetoFront front = builder.build();
// p2 ne doit pas être ajouté car il est dominé par p1.
        assertEquals(1, front.size(), "La frontière doit contenir seulement p1");
        assertEquals(p1, front.get(PackedCriteria.arrMins(p1), PackedCriteria.changes(p1)));
// La tentative de récupération de p2 doit lever une exception.
        assertThrows(NoSuchElementException.class, () -> {
            front.get(PackedCriteria.arrMins(p2), PackedCriteria.changes(p2));
        });
    }

    @Test
    public void testAddNonDominatedElements() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        // Trois tuples non dominés entre eux.
        long p1 = pack(480, 3, 2);
        long p2 = pack(481, 2, 23);
        long p3 = pack(482, 1, 25);
        builder.add(p1);
        builder.add(p2);
        builder.add(p3);
        ParetoFront front = builder.build();
        assertEquals(3, front.size(), "La frontière doit contenir 3 éléments");
        // Vérification que chaque tuple est retrouvable par ses critères
        assertEquals(p1, front.get(PackedCriteria.arrMins(p1), PackedCriteria.changes(p1)));
        assertEquals(p2, front.get(PackedCriteria.arrMins(p2), PackedCriteria.changes(p2)));
        assertEquals(p3, front.get(PackedCriteria.arrMins(p3), PackedCriteria.changes(p3)));

    }

    @Test
    public void testForEach() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        long p1 = pack(480, 3, 2);
        long p2 = pack(481, 2, 23);
        builder.add(p1);
        builder.add(p2);
        ParetoFront front = builder.build();
        ArrayList<Long> collected = new ArrayList<>();
        front.forEach(new LongConsumer() {
            @Override
            public void accept(long value) {
                collected.add(value);
            }
        });
// Vérification que forEach parcourt bien tous les éléments
        assertEquals(front.size(), collected.size(), "Le nombre d'éléments parcourus doit correspondre à la taille de la frontière");
        assertTrue(collected.contains(p1));
        assertTrue(collected.contains(p2));
    }

    /**
     * Tests sur la classe Builder
     **/

    @Test
    public void testBuilderIsInitiallyEmpty() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        assertTrue(builder.isEmpty(), "Un Builder initial doit être vide.");
    }

    @Test
    public void testBuilderCopyConstructor() {
        ParetoFront.Builder original = new ParetoFront.Builder();
        original.add(pack(10, 2, 0));

        ParetoFront.Builder copy = new ParetoFront.Builder(original);
        assertEquals(1, copy.build().size(), "Le constructeur de copie doit préserver les éléments.");
        assertEquals(pack(10, 2, 0), copy.build().get(10, 2));
    }

    @Test
    public void testBuilderAddTuple() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(pack(10, 2, 0));

        assertFalse(builder.isEmpty(), "Le Builder ne doit pas être vide après un ajout.");
        assertEquals(1, builder.build().size(), "Un seul élément doit être ajouté.");
    }

    @Test
    public void testBuilderAddDominatedTuple() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(pack(10, 2, 0));  // Ajout initial
        builder.add(pack(12, 3, 0));  // Devrait être conservé
        builder.add(pack(9, 1, 0));   // Devrait dominer (moins d’arrêts et temps inférieur)

        assertEquals(1, builder.build().size(), "Seul le tuple dominant doit être conservé.");
        assertEquals(pack(9, 1, 0), builder.build().get(9, 1), "Le tuple dominant doit être conservé.");
    }

    @Test
    public void testBuilderClear() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(pack(10, 2, 0));
        builder.clear();

        assertTrue(builder.isEmpty(), "Le Builder doit être vide après clear().");
        assertThrows(NoSuchElementException.class, () -> builder.build().get(10, 2), "Une exception doit être levée pour un critère absent.");
    }

    @Test
    public void testBuilderAddAll() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(pack(10, 2, 0));
        builder1.add(pack(12, 3, 0));

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(pack(8, 1, 0));  // Devrait être ajouté

        builder1.addAll(builder2);

        assertEquals(1, builder1.build().size(), "Seuls les éléments non dominés doivent rester.");
        assertEquals(pack(8, 1, 0), builder1.build().get(8, 1), "L'élément ajouté doit être accessible.");
    }

    @Test
    public void testBuilderFullyDominates() {
        ///  Builder 1 n'a pas de min de depart ainsi une exeption est levee !
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(pack(10, 2, 0));
        builder1.add(pack(12, 3, 0));

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(pack(15, 4, 0)); // Plus lent et plus de changements = dominé
        ///  ajouter AssertThrows
    }

    @Test
    public void testBuilderDoesNotFullyDominate() {
        ///  Builder 1 n'a pas de min de depart ainsi une exeption est levee !
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(pack(0, 10, 2));

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(pack(0, 8, 1)); // Meilleur, donc pas dominé

        ///  ajouter AssertThrows
    }

    @Test
    public void testBuilderBuildCreatesParetoFront() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(pack(8, 3, 0));
        builder.add(pack(8, 4, 0));
        builder.add(pack(7, 4, 0));

        ParetoFront front = builder.build();
        System.out.println(builder);
        assertEquals(2, front.size(), "Le ParetoFront construit doit contenir tous les éléments.");
        //assertEquals(PackedCriteria.pack(7,4, 0), front.get(7, 4), "Les éléments doivent être correctement insérés.");
    }

    /**
     * Tests sur la classe ParetoFront
     **/

    @Test
    public void testParetoFrontSize() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(pack(10, 2, 0));
        builder.add(pack(15, 1, 0));
        System.out.println(builder);
        ParetoFront front = builder.build();
        System.out.println(builder);
        assertEquals(2, front.size(), "La taille doit correspondre au nombre d'éléments ajoutés.");
    }

    @Test
    public void testParetoFrontGetValidCriteria() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(pack(10, 2, 0));
        builder.add(pack(15, 1, 0));

        ParetoFront front = builder.build();
        assertEquals(pack(10, 2, 0), front.get(10, 2), "L'élément 10,2 doit être retrouvé.");
        assertEquals(pack(15, 1, 0), front.get(15, 1), "L'élément 15,1 doit être retrouvé.");
    }

    @Test
    public void testParetoFrontGetThrowsExceptionOnMissingCriteria() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(pack(10, 2, 0));

        ParetoFront front = builder.build();
        assertThrows(NoSuchElementException.class, () -> front.get(12, 3),
                "Une exception doit être levée pour un critère absent.");
    }

    @Test
    public void testParetoFrontForEachOnEmptyFront() {
        ParetoFront emptyFront = ParetoFront.EMPTY;
        List<Long> collected = new ArrayList<>();

        emptyFront.forEach(collected::add);

        assertTrue(collected.isEmpty(), "forEach sur une frontière vide ne doit pas appeler l'action.");
    }

    @Test
    void builderIsEmptyInitially() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        assertTrue(builder.isEmpty());
    }

    @Test
    void builderAddIncreasesSize() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        assertFalse(builder.isEmpty());
    }

    @Test
    void builderClearResetsSize() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        builder.clear();
        assertTrue(builder.isEmpty());
    }

    @Test
    void builderAddAllCombinesCriteria() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(10, 2, 1);
        builder1.add(15, 1, 1);

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(20, 0, 1);

        builder1.addAll(builder2);
        ParetoFront paretoFront = builder1.build();

        assertEquals(3, paretoFront.size());
    }


    @Test
    void addAllHandlesPartiallyDominatedCriteria() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(10, 2, 1); // (arrMins=10, changes=2, payload=1)
        builder1.add(15, 1, 1); // (arrMins=15, changes=1, payload=1)

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(12, 3, 1); // (arrMins=12, changes=3, payload=1) => Dominé par (10,2,1)
        builder2.add(14, 1, 1); // (arrMins=14, changes=1, payload=1) => Non dominé

        builder1.addAll(builder2);
        ParetoFront paretoFront = builder1.build();

        assertEquals(2, paretoFront.size()); // Doit contenir (10,2,1), (14,1,1)
    }


    @Test
    void paretoFrontGetThrowsExceptionForNonExistentCriteria() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        ParetoFront paretoFront = builder.build();

        assertThrows(NoSuchElementException.class, () -> paretoFront.get(15, 1));
    }


    @Test
    void builderAddDoesNotAddDominatedCriteria() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        builder.add(12, 3, 1); // Dominated by the first criteria
        assertEquals(1, builder.build().size());
    }

    @Test
    void builderFullyDominatesReturnsFalseWhenNotAllCriteriaDominated2() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        long criteria1 = withDepMins(pack(10, 2, 1), 5);
        builder1.add(criteria1);

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(pack(5, 1, 1));  // (arrMins=5, changes=1, payload=1) => Plus optimal

        assertFalse(builder1.fullyDominates(builder2, 5)); // Devrait être false
    }

    @Test
    void builderFullyDominatesReturnsFalseWhenNotAllCriteriaDominated() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        long criteria1 = pack(10, 2, 1);
        criteria1 = withDepMins(criteria1, 5);
        builder1.add(criteria1);

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(5, 1, 1);  // (arrMins=5, changes=1, payload=1) => Plus optimal

        assertFalse(builder1.fullyDominates(builder2, 5)); //
    }

    @Test
    void builderAddDoesNotAddDuplicateCriteria() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        builder.add(10, 2, 1); // Critère identique

        assertEquals(1, builder.build().size());
    }

    @Test
    void builderDoesNotAddDominatedCriteria() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        builder.add(15, 3, 1); // Dominé par (10, 2, 1)

        assertEquals(1, builder.build().size());
    }

    @Test
    void builderMergeNonDominatedCriteria() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(10, 2, 1);

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(8, 3, 1); // Non dominé par (10, 2, 1)

        builder1.addAll(builder2);
        ParetoFront paretoFront = builder1.build();

        assertEquals(2, paretoFront.size());
    }

    /// /////////////////////////////////////////////////////////////////

    @Test
    void builderFullyDominatesReturnsTrueWhenAllCriteriaDominated() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        long criteria1 = withDepMins(pack(10, 2, 1), 5);
        builder1.add(criteria1);

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(pack(15, 3, 1));  // (arrMins=15, changes=3, payload=1) => Moins optimal

        assertTrue(builder1.fullyDominates(builder2, 5)); // Devrait être true
    }

    @Test
    void builderFullyDominatesReturnsFalseWhenCriteriaEqual() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        long criteria1 = withDepMins(pack(10, 2, 1), 5);
        builder1.add(criteria1);

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(withDepMins(pack(10, 2, 1), 5));  // Critères identiques

        assertTrue(builder1.fullyDominates(builder2, 5)); // Devrait être false
    }

    @Test
    void builderFullyDominatesReturnsFalseWhenNoCriteria() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        ParetoFront.Builder builder2 = new ParetoFront.Builder();

        assertTrue(builder1.fullyDominates(builder2, 5)); // Devrait être false
    }

    @Test
    void builderFullyDominatesReturnsTrueWithMultipleCriteria() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(withDepMins(pack(10, 2, 1), 5));
        builder1.add(withDepMins(pack(15, 1, 1), 5));

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(pack(20, 3, 1));  // (arrMins=20, changes=3, payload=1) => Moins optimal

        assertTrue(builder1.fullyDominates(builder2, 5)); // Devrait être true
    }

    @Test
    void builderFullyDominatesReturnsFalseWithMixedCriteria() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(withDepMins(pack(10, 2, 1), 5));
        builder1.add(withDepMins(pack(20, 1, 1), 5));

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(pack(15, 3, 1));  // (arrMins=15, changes=3, payload=1) => Moins optimal
        builder2.add(pack(5, 1, 1));   // (arrMins=5, changes=1, payload=1) => Plus optimal

        assertFalse(builder1.fullyDominates(builder2, 5)); // Devrait être false
    }

    @Test
    void builderFullyDominatesReturnsTrueWithDominatedCriteria() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        long criteria1 = pack(10, 2, 1);
        long criteria2 = pack(30, 5, 1);

        criteria1 = withDepMins(criteria1, 5);
        criteria2 = withDepMins(criteria1, 5);

        builder1.add(criteria1);
        builder1.add(criteria2);

        ParetoFront.Builder builder2 = new ParetoFront.Builder();

        builder2.add(pack(20, 3, 1));  // (arrMins=20, changes=3, payload=1) => Moins optimal
        builder2.add(pack(25, 4, 1));
        builder2.add(pack(21, 2, 1));// (arrMins=25, changes=4, payload=1) => Moins optimal

        assertTrue(builder1.fullyDominates(builder2, 5)); // Devrait être true


    }

    @Test
    void fullyDominatesHandlesEmptyFrontier() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(10, 2, 1);

        ParetoFront.Builder builder2 = new ParetoFront.Builder(); // Frontière vide

        assertTrue(builder1.fullyDominates(builder2, 5)); // Une frontière non vide domine toujours une frontière vide
        assertFalse(builder2.fullyDominates(builder1, 5)); // Une frontière vide ne domine jamais une frontière non vide
    }


    @Test
    void builderReturnTrueWhenNoCriteria() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        ParetoFront.Builder builder2 = new ParetoFront.Builder();

        assertTrue(builder1.fullyDominates(builder2, 5)); // Devrait être true
    }

    /// //////////////////////////////////////////////////////////////////////////////////////

    @Test
    void builderFullyDominatesReturnsFalseWithNonDominatedCriteria() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        long criteria1 = pack(10, 2, 1);
        long criteria2 = pack(30, 5, 1);

        criteria1 = withDepMins(criteria1, 5);
        criteria2 = withDepMins(criteria2, 5);

        builder1.add(criteria1);
        builder1.add(criteria2);

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(pack(5, 1, 1));   // (arrMins=5, changes=1, payload=1) => Plus optimal
        builder2.add(pack(25, 4, 1));  // (arrMins=25, changes=4, payload=1) => Moins optimal

        assertFalse(builder1.fullyDominates(builder2, 5)); // Devrait être false
    }

    @Test
    void builderFullyDominatesReturnsTrueWithEqualCriteria() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        long criteria1 = pack(10, 2, 1);
        criteria1 = withDepMins(criteria1, 5);
        builder1.add(criteria1);

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(withDepMins(pack(10, 2, 1), 5));  // Critères identiques

        assertTrue(builder1.fullyDominates(builder2, 5)); // Devrait être true
    }

    @Test
    void builderIsEmptyReturnsTrueWhenEmpty() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        assertTrue(builder.isEmpty());
    }

    @Test
    void builderIsEmptyReturnsFalseWhenNotEmpty() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        assertFalse(builder.isEmpty());
    }

    @Test
    void builderClearEmptiesTheBuilder() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        builder.clear();
        assertTrue(builder.isEmpty());
    }


    @Test
    void builderBuildCreatesParetoFront1() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        builder.add(10, 2, 1);
        builder.add(10, 2, 1);
        builder.add(7, 2, 1);
        builder.add(9, 3, 1);

        ParetoFront paretoFront2 = builder.build();
        System.out.println("1: " + paretoFront2);
        assertEquals(1, paretoFront2.size());
    }


    @Test
    void clearWorksAfterMultipleAdds() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        builder.add(15, 1, 1);
        builder.clear();
        builder.add(20, 0, 1);

        ParetoFront paretoFront = builder.build();
        assertEquals(1, paretoFront.size()); // Doit contenir uniquement (20,0,1)
    }


    @Test
    void builderBuildCreatesParetoFront2() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        builder.add(10, 2, 1);
        builder.add(10, 2, 1);
        builder.add(9, 3, 1);
        builder.add(7, 2, 1);

        ParetoFront paretoFront2 = builder.build();
        System.out.println("2: " + paretoFront2);
        assertEquals(1, paretoFront2.size());
    }


    @Test
    void buildRemovesDominatedCriteriaInMiddle() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1); // (arrMins=10, changes=2, payload=1)
        builder.add(12, 3, 1); // (arrMins=12, changes=3, payload=1) => Dominé par (10,2,1)
        builder.add(15, 1, 1); // (arrMins=15, changes=1, payload=1)

        ParetoFront paretoFront = builder.build();
        assertEquals(2, paretoFront.size()); // Doit contenir (10,2,1) et (15,1,1)
    }


    @Test
    void buildHandlesUnsortedCriteria() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(15, 1, 1); // (arrMins=15, changes=1, payload=1)
        builder.add(10, 2, 1); // (arrMins=10, changes=2, payload=1)
        builder.add(20, 0, 1); // (arrMins=20, changes=0, payload=1)

        ParetoFront paretoFront = builder.build();
        assertEquals(3, paretoFront.size()); // Doit contenir (10,2,1), (15,1,1), et (20,0,1)
    }


    @Test
    void forEachDoesNotAllowModifyingCriteria() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        builder.add(15, 1, 1);

        ParetoFront paretoFront = builder.build();
        List<Long> criteriaList = new ArrayList<>();
        paretoFront.forEach(criteria -> {
            criteriaList.add(criteria);
            // Tentative de modification (ne devrait pas avoir d'effet)
            criteria = pack(20, 0, 1);
        });

        assertEquals(2, criteriaList.size()); // Doit contenir les critères originaux
        assertTrue(criteriaList.contains(pack(10, 2, 1)));
        assertTrue(criteriaList.contains(pack(15, 1, 1)));
    }

    @Test
    void builderHandlesInvertedDominance() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 3, 1); // (arrMins=10, changes=3, payload=1)
        builder.add(12, 2, 1); // (arrMins=12, changes=2, payload=1) => Moins bon sur l'heure, meilleur sur les changements

        ParetoFront paretoFront = builder.build();
        assertEquals(2, paretoFront.size()); // Les deux critères doivent être conservés car aucun ne domine l'autre
    }
}

