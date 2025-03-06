package ch.epfl.rechor.journey;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class MyParetoFrontTest {

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

}
