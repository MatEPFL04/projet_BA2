package ch.epfl.rechor.journey;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class MyParetoFrontTest {

    @Test
    void emptyParetoFrontTest() {
        ParetoFront front = ParetoFront.EMPTY;
        assertEquals(0, front.size());
    }


    @Test
    void exceptionThrownWhen2D_3D_tuplesAreMixed(){
        ParetoFront.Builder front = new ParetoFront.Builder();
        long twoDTuple = PackedCriteria.pack(20,10,30);
        long threeDTuple = PackedCriteria.withDepMins(twoDTuple,30);
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
            front.add(PackedCriteria.pack(i,100 - i,0));
        }
        ParetoFront frontDePareto = front.build();
        assertEquals(numberOfTuples,frontDePareto.size());
    }

    @Test
    void noSuchElmExcepThrownWhenAccessingToAnUnknownElementInTheFront (){
        ParetoFront.Builder front = new ParetoFront.Builder();
        long twoDTuple = PackedCriteria.pack(20,10,30);
        ParetoFront finalFront = front.add(twoDTuple).build();
        assertThrows(NoSuchElementException.class, () -> {
            finalFront.get(21,10);
        });
    }

    @Test
    void elementIsReturnedCorrectlyWhenUsingGetMethod() {
        ParetoFront.Builder front = new ParetoFront.Builder();
        int packedMin = 0,packedChanges = 0,packedPayload = 0;
        for (int i = 0; i < 20; i++) {
            front.add(PackedCriteria.pack(packedMin-i,packedChanges+i,packedPayload));
        }
        ParetoFront finalFront = front.build();
        for (int i = 0; i < 20; i++) {
            assertEquals(packedMin - i,PackedCriteria.arrMins(finalFront.get(packedMin-i,packedChanges+i)));
            assertEquals(packedChanges + i,PackedCriteria.changes(finalFront.get(packedMin-i,packedChanges+i)));
            assertEquals(packedPayload,PackedCriteria.payload(finalFront.get(packedMin-i,packedChanges+i)));
        }
    }

    @Test
    void addingTuplesFromAnotherFrontWorks () {
        ParetoFront.Builder front = new ParetoFront.Builder();
        int initialSize = 10;
        for (int i = 0; i < initialSize; i++) {
            front.add(PackedCriteria.pack(i,100 - i,0));
        }
        ParetoFront.Builder secondFront = new ParetoFront.Builder();

        for (int i = initialSize; i < 2*initialSize; i++) {
            secondFront.add(PackedCriteria.pack((initialSize + i),100 - (initialSize + i),0));
        }
        front.addAll(secondFront);
        ParetoFront concatenatedFinalFront = front.build();
        assertEquals(2*initialSize, concatenatedFinalFront.size());

    }

    @Test
    void fullyDominatesWorksWhenJustOneTupleFromThatIsNotDominated () {
        ParetoFront.Builder front1 = new ParetoFront.Builder();
        long val1 = PackedCriteria.withDepMins(PackedCriteria.pack(10,10,0),10);
        long val2 = PackedCriteria.withDepMins(PackedCriteria.pack(11,9,0),9);
        long val3 = PackedCriteria.withDepMins(PackedCriteria.pack(12,8,0),8);
        long val4 = PackedCriteria.withDepMins(PackedCriteria.pack(13,7,0),7);
        long val5 = PackedCriteria.withDepMins(PackedCriteria.pack(14,5,0),6);
        front1.add(val1).add(val2).add(val3).add(val4).add(val5);

        long val1Front2 = PackedCriteria.withDepMins(PackedCriteria.pack(10,10,0),0x1F);
        long val2Front2 = PackedCriteria.withDepMins(PackedCriteria.pack(11,9,0),0xA);
        long val3Front2 = PackedCriteria.withDepMins(PackedCriteria.pack(12,8,0),0);
        long val4Front2 = PackedCriteria.withDepMins(PackedCriteria.pack(13,7,0),0b1010101010);
        long val5Front2 = PackedCriteria.withDepMins(PackedCriteria.pack(14,4,0),-20);

        ParetoFront.Builder frontComparedToFront1 = new ParetoFront.Builder();

        frontComparedToFront1.add(val1Front2).add(val2Front2).add(val3Front2).add(val4Front2).add(val5Front2);
        int depMinsImposed = 6;

        assertFalse(front1.fullyDominates(frontComparedToFront1,depMinsImposed));

    }

    @Test
    void tuplesWithDepMinsAreCorrectlyAddedToTheFrontAndRemovedWhenDominated () {
        ParetoFront.Builder testFront = new ParetoFront.Builder();
        int expectedFrontSize = 6;

        int arrmins = 200, changes = 30, depMins = 800;

        long tuple1 = PackedCriteria.withDepMins(PackedCriteria.pack(1300,20,0),1200);
        long tuple2 = PackedCriteria.withDepMins(PackedCriteria.pack(900,30,0),1000);
        long tuple3 = PackedCriteria.withDepMins(PackedCriteria.pack(arrmins,changes,0),depMins); //dominated by "tupleDominatingTuple3
        long tuple4 = PackedCriteria.withDepMins(PackedCriteria.pack(205,25,0),800);
        long tuple5 = PackedCriteria.withDepMins(PackedCriteria.pack(50,20,0),700);
        long tuple6 = PackedCriteria.withDepMins(PackedCriteria.pack(51,19,0),700);

        long tupleDominatingTuple3 = PackedCriteria.withDepMins(PackedCriteria.pack(arrmins,changes - 1,0),depMins + 1);

        testFront.add(tuple1).add(tuple2).add(tuple3).add(tuple4).add(tuple5).add(tuple6).add(tupleDominatingTuple3);
        ParetoFront front = testFront.build();
        assertEquals(expectedFrontSize,front.size());
        assertThrows(NoSuchElementException.class, () -> {
            front.get(arrmins,changes);
        });


    }

    @Test
    void clearingAFrontAndRecreatingItFromScratchWorks () {
        ParetoFront.Builder testFront = new ParetoFront.Builder();
        for (int i = 0; i < 100; i++) {
            testFront.add(PackedCriteria.pack(i,100 - i,0));
        }
        ParetoFront frontCreated = testFront.clear().build();
        assertEquals(0,frontCreated.size());
    }

    @Test
    void constructingUsingAnotherFrontWorksAndElementsAreCorrectlyCopied () {
        ParetoFront.Builder testFront1 = new ParetoFront.Builder();
        int numberOfTuples = 100;
        for (int i = 0; i < numberOfTuples; i++) {
            testFront1.add(PackedCriteria.pack(i,100 - i,0));
        }
        ParetoFront.Builder newBuilderUsingCopy = new ParetoFront.Builder(testFront1);
        ParetoFront frontCreated = newBuilderUsingCopy.build();
        assertEquals(numberOfTuples,frontCreated.size());

        for (int i = 0; i < numberOfTuples; i++) {
            assertEquals(PackedCriteria.pack(i,100 - i,0), frontCreated.get(i,100-i));

        }
    }

    @Test
    void tableIsCopiedAndCannotBeChangedInsideParetoFront () {
        ParetoFront.Builder testFront = new ParetoFront.Builder();
        int numberOfTuples = 100;
        for (int i = 0; i < numberOfTuples/2; i++) {
            testFront.add(PackedCriteria.pack(i,100 - i,0));
        }
        ParetoFront frontCreated = testFront.build();
        for (int i = numberOfTuples/2; i < numberOfTuples; i++) {
            testFront.add(PackedCriteria.pack(i,100 - i,0));
        }
        assertEquals(numberOfTuples/2, frontCreated.size());


    }


}

