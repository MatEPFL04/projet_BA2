package ch.epfl.rechor.journey;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MypackedCriteriaTest {

    @Test
    void packingAndUnpackingArrTimeWorks () {
        int expectedArrTime = 0b100000000111;
        int changes = 0b1000000;
        int payLoad = 0b11111111111111111111111111111111;
        long criteriaPacked = PackedCriteria.pack(expectedArrTime, changes, payLoad);
        int currentArrTimeExtracted = PackedCriteria.arrMins(criteriaPacked);
        assertEquals(expectedArrTime, currentArrTimeExtracted);
    }

    @Test
    void packingAndUnpackingDepTimeWorks () {
        int depmins = 0b101001111000;
        long criteria = PackedCriteria.pack(240,0,0);
        long criteriaWithDepMins = PackedCriteria.withDepMins(criteria,depmins);
        assertEquals(depmins,PackedCriteria.depMins(criteriaWithDepMins));
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

    @Test
    void evaluatingCriteriaWithDepMinsAndDrawWork () {
        int depMins = 0b101010;
        int arrMins = 0b01011010101;
        int changes = 12;
        int payload = 0x7FFAB;
        long criteria = PackedCriteria.pack(arrMins,changes,payload);
        long newCriteria1 = PackedCriteria.withDepMins(criteria,depMins);
        long newCriteria2 = PackedCriteria.withDepMins(criteria,depMins);
        assertTrue(PackedCriteria.dominatesOrIsEqual(newCriteria1,newCriteria2));
    }

    @Test
    void evualatingCriteriaWorksWhenTheFirstLooses () {
        int depMins = 50;
        int arrMins1 = 100;
        int arrMins2 = 90;
        int changes = 5;
        int payload = 2000;
        long criteria1 = PackedCriteria.pack(arrMins1,changes,payload);
        long criteria2 = PackedCriteria.pack(arrMins2,changes,payload);
        long criteria1WithDepMins = PackedCriteria.withDepMins(criteria1,depMins);
        long criteria2WithDepMins = PackedCriteria.withDepMins(criteria2, depMins);

        assertFalse(PackedCriteria.dominatesOrIsEqual(criteria1WithDepMins,criteria2WithDepMins));
    }

    @Test
    void evualatingCriteriaWorksWhenTheFirstLoosesOn1AndWinsOnAnOther() {
        int depMins1 = 50; //gagne
        int depMins2 = 40;
        int arrMins1 = 100; //perd
        int arrMins2 = 90;
        int changes = 5;
        int payload = 2000;
        long criteria1 = PackedCriteria.pack(arrMins1,changes,payload);
        long criteria2 = PackedCriteria.pack(arrMins2,changes,payload);
        long criteria1WithDepMins = PackedCriteria.withDepMins(criteria1,depMins1);
        long criteria2WithDepMins = PackedCriteria.withDepMins(criteria2, depMins2);

        assertFalse(PackedCriteria.dominatesOrIsEqual(criteria2WithDepMins,criteria1WithDepMins));
    }


}
