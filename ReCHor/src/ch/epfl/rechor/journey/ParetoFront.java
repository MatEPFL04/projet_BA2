package ch.epfl.rechor.journey;

import ch.epfl.rechor.PackedRange;

import java.util.NoSuchElementException;
import java.util.function.LongConsumer;

public final class ParetoFront {
    private long [] frontTuples;
    public static final ParetoFront EMPTY = new ParetoFront(new long [0]);

    private ParetoFront (long [] packedCriteria) {
        this.frontTuples = packedCriteria;

    }

    public int size() {
        return frontTuples.length;
    }

    public long get(int arrMins, int changes) throws NoSuchElementException {
        for (int i = 0; i < size(); i++) {
            if (PackedCriteria.arrMins(frontTuples[i]) == arrMins
                    && PackedCriteria.changes(frontTuples[i]) == changes) {
                return frontTuples[i];
            }
        }
        throw new NoSuchElementException ();
    }

    /*
    public void forEach(LongConsumer action) {
        ParetoFront paretoFront =
        paretoFront.forEach(value -> { forEach(action); });

    }

     */
    public String toString () {
        for (int i = 0; i < size(); i++) {
            if (PackedCriteria.hasDepMins(frontTuples[i])) {
                return " tuple " + i + ": (" +
                        PackedCriteria.depMins(frontTuples[i]) + " , " +
                        PackedCriteria.arrMins(frontTuples[i]) + " , " +
                        PackedCriteria.changes(frontTuples[i]) + ")";
            } else {
                return " tuple " + i + ": (" +
                        PackedCriteria.arrMins(frontTuples[i]) + " , " +
                        PackedCriteria.changes(frontTuples[i]) + ")";
            }
        }
        return null;
    }



}
