package ch.epfl.rechor.journey;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MypackedCriteriaTest {

    @Test
    void packingAndUnpackingArrTimeWorks () {
        int expectedArrTime = 0b110101010;
        long criteriaPacked = PackedCriteria.pack(expectedArrTime, 0, 0);
        int currentArrTimeExtracted = PackedCriteria.arrMins(criteriaPacked);
        assertEquals(expectedArrTime, currentArrTimeExtracted);
    }

    @Test
    void packingAndUnpackingDepTimeWorks () {
        long criteria = PackedCriteria.pack(240,0,0);
        long criteriaWithDepMins = PackedCriteria.withDepMins(criteria,1237);
        int expectedMin = 1237;
        assertEquals(expectedMin,PackedCriteria.depMins(criteriaWithDepMins));
    }

    @Test
    void packingAndUnpackingChangesWork () {
        int expectedChanges = 0b1101101;
        long criteria = PackedCriteria.pack(240,expectedChanges,0);
        long changesFromCriteria = PackedCriteria.changes(criteria);
        assertEquals(expectedChanges,changesFromCriteria);
    }

    @Test
    void packingAndUnpackingPayloadWorks () {
        int expectedPayload = 0b11001110011000111;
        long criteria = PackedCriteria.pack(240,0,expectedPayload);
        long extractedPayload = PackedCriteria.payload(criteria);
        assertEquals(expectedPayload,extractedPayload);
    }

    @Test
    void exceptionThrownWhenNoDepMinsAndAccessingDepMins() {
        long criteria = PackedCriteria.pack(240,120,0b11010);
        long criteriaWithDepMins = PackedCriteria.withDepMins(criteria, 0b110100111);
        long criteriaWithoutDepMins = PackedCriteria.withoutDepMins(criteriaWithDepMins);
        assertThrows(IllegalArgumentException.class, () -> {
            PackedCriteria.depMins(criteriaWithoutDepMins);
        });

    }

    @Test
    void incrementingChangesWork () {
        int expectedFinalChange = 0b1011011;
        long criteria = PackedCriteria.pack(240,expectedFinalChange -1,0b11010);
        assertEquals(expectedFinalChange, PackedCriteria.changes(PackedCriteria.withAdditionalChange(criteria)));
    }

    @Test
    void modifyingPayloadWorks () {
        int initialPayload = 0b110101010101010011111111111;
        long criteria = PackedCriteria.pack(20,10,initialPayload);
        int finalPayLoad = 0b1101010101000010111100001111100;
        long criteriaWithFinalPayLoad = PackedCriteria.withPayload(criteria,finalPayLoad);

        assertEquals(finalPayLoad,PackedCriteria.payload(criteriaWithFinalPayLoad));
    }

    @Test
    void evaluatingCriteriaWithoutDepMinsAndDrawWork () {
        long criteria1 = PackedCriteria.pack(240,2,1010);
        long criteria2 = PackedCriteria.pack(240,2,1010);
        assertTrue(PackedCriteria.dominatesOrIsEqual(criteria1,criteria2));
    }







}
