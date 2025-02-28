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
        throw new NoSuchElementException();
    }


    public void forEach(LongConsumer action) {
        for (int i = 0; i < size(); i++) {
            action.accept(frontTuples[i]);
        }
    }


    public String toString () {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            if (PackedCriteria.hasDepMins(frontTuples[i])) {
                sb.append(" tuple " + i + ": (" +
                        PackedCriteria.depMins(frontTuples[i]) + " , " +
                        PackedCriteria.arrMins(frontTuples[i]) + " , " +
                        PackedCriteria.changes(frontTuples[i]) + ")");
            } else {
                sb.append(" tuple " + i + ": (" +
                        PackedCriteria.arrMins(frontTuples[i]) + " , " +
                        PackedCriteria.changes(frontTuples[i]) + ")");
            }
        }
        return sb.toString();
    }

    public static class Builder {
        private ParetoFront frontInConstruction;


        public Builder() {
            frontInConstruction = EMPTY;

        }

        public Builder (Builder that) {
            frontInConstruction = that.frontInConstruction;

        }

        boolean isEmpty(){
            return (frontInConstruction.frontTuples.length == 0);
        }

        Builder clear(){
            frontInConstruction = EMPTY;
            return this;
        }


    }



}
