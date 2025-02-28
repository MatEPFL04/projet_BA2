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
        private long [] frontInConstruction;
        private int effectiveSize;


        public Builder() {
            frontInConstruction = new long[10];
            effectiveSize = 0;

        }

        public Builder (Builder that) {
            frontInConstruction = that.frontInConstruction;
            effectiveSize = that.effectiveSize;

        }

        boolean isEmpty(){
            return (effectiveSize == 0);
        }

        Builder clear(){
            frontInConstruction = new long[10];
            effectiveSize = 0;

            return this;
        }



        Builder add(long PackedTuple) {
            for (int i = 0; i < frontInConstruction.length; i++) {


            }
        }


    }



}
