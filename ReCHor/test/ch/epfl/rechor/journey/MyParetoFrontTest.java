package ch.epfl.rechor.journey;
import org.junit.jupiter.api.Test;

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




}
