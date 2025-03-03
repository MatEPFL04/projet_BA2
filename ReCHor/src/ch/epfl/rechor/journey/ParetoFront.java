package ch.epfl.rechor.journey;

import java.util.Arrays;
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
            frontInConstruction = new long[2];
            effectiveSize = 0;

        }

        public Builder (Builder that) {
            frontInConstruction = that.frontInConstruction;
            effectiveSize = that.effectiveSize;

        }

        public boolean isEmpty(){
            return (effectiveSize == 0);
        }

        public Builder clear(){
            frontInConstruction = new long[10];
            effectiveSize = 0;

            return this;
        }

        private long withoutPayLoad (long criteria) {
            long mask = (0xFFFFFFFFL << 32);
            return criteria & mask;
        }

        private int getInsertionIdx (long packedTuple) {
            int insertionIdx = 0;

            for (int i = 0; i < effectiveSize; i++) {
                if (withoutPayLoad(packedTuple) > withoutPayLoad(frontInConstruction[i])) {
                    insertionIdx++;
                }
                else{
                    break;
                }
            }
            return insertionIdx;
        }

        private boolean isTupleInTheFront (long packedTuple) {
            boolean isPartOfTheFront = true;
            for (int i = 0; i < getInsertionIdx(packedTuple); i++) {
                if (PackedCriteria.dominatesOrIsEqual(frontInConstruction[i],packedTuple)) {
                    isPartOfTheFront = false;
                }
            }
            return isPartOfTheFront;
        }

        private void frontResize() {
            if ((effectiveSize + 1) > frontInConstruction.length) {
                int newCapacity =  (int) (1.5 * frontInConstruction.length);
                frontInConstruction = Arrays.copyOf(frontInConstruction,newCapacity);
            }
        }

        private int compactNonDominated(long[] array, long packedTuple) {
            int nbrOfNonDominatedTupples = 0;

            for (int i = 0; i < effectiveSize; i += 1) {
                if (PackedCriteria.dominatesOrIsEqual(packedTuple, array[i])) {
                    continue;
                }
                if (nbrOfNonDominatedTupples != i) {
                    array[nbrOfNonDominatedTupples] = array[i];
                }
                nbrOfNonDominatedTupples += 1;
            }

            return nbrOfNonDominatedTupples;
        }

        /**
         * We first find the correct insertion index,
         * then we check if the new tuple is dominated,
         * after we shift elements right from the insertion index
         * and lastly we remove the dominated tuples with the compact method.
         * @param packedTuple
         * @return the new paretoFront
         */

        public Builder add(long packedTuple) {

            if (!isTupleInTheFront(packedTuple))
                return this;

            int insertionIdx = getInsertionIdx(packedTuple);
            frontResize();

            for (int i = 0; i < insertionIdx; i++) {
                if (PackedCriteria.dominatesOrIsEqual(frontInConstruction[i], packedTuple)) {
                    return this;
                }
            }

            System.arraycopy(frontInConstruction, insertionIdx, frontInConstruction, insertionIdx + 1, effectiveSize - insertionIdx);
            frontInConstruction[insertionIdx] = packedTuple;
            effectiveSize++;

            effectiveSize = compactNonDominated(frontInConstruction, packedTuple);

            return this;
        }

        public static void main(String[] args) {
            Builder testFront = new Builder();
            long tuple1 = PackedCriteria.pack(100,100,0);
            long tuple2 = PackedCriteria.pack(120,80,0);
            long tuple3 = PackedCriteria.pack(140,60,0);
            long tuple4 = PackedCriteria.pack(130,59,0);
            long tuple5 = PackedCriteria.pack(120,79,0);
            long tuple6 = PackedCriteria.pack(40,40,0);

        }

        public Builder add (int arrMins, int changes, int payload) {
            long packedTuple = PackedCriteria.pack(arrMins,changes,payload);

            /*int changes2  = PackedCriteria.
            add(packedTuple);

             */
            return this;
        }

        public Builder addAll(Builder that) {
            for (int i = 0; i < that.effectiveSize ; i++) {
                add(that.frontInConstruction[i]);
            }
            return this;
        }

        public boolean fullyDominates(Builder that, int depMins) {
            for (int i = 0; i < that.effectiveSize; i++) { //itération sur tous les tuples de that
                boolean isTupleFromThatDominatedOrEqualInThis = false;
                long criteriaWithDepMins = PackedCriteria.withDepMins(that.frontInConstruction[i],depMins);
                // au début, chaque tuple de that n'a pas trouvé de dominant ou égal
                for (int j = 0; j < effectiveSize; j++) {
                    if (PackedCriteria.dominatesOrIsEqual(frontInConstruction[j],criteriaWithDepMins)){
                        isTupleFromThatDominatedOrEqualInThis = true;
                    }
                }
                if (!isTupleFromThatDominatedOrEqualInThis) {
                    return false;
                }
            }
            return true;

        }

        public void forEach(LongConsumer action) {
            for (int i = 0; i < effectiveSize; i++) {
                action.accept(frontInConstruction[i]);
            }
        }

        public ParetoFront build () {
            return new ParetoFront(Arrays.copyOf(frontInConstruction,effectiveSize));
        }

        @Override
        public String toString () {
            StringBuilder sb = new StringBuilder();
            sb.append("Front: { ");
            for (int i = 0; i < effectiveSize; i++) {
                if (PackedCriteria.hasDepMins(frontInConstruction[i])) {
                    sb.append("(" +
                            PackedCriteria.depMins(frontInConstruction[i]) + "," +
                            PackedCriteria.arrMins(frontInConstruction[i]) + ","  +
                            PackedCriteria.changes(frontInConstruction[i]) + ") ");
                } else {
                    sb.append("(" +
                            PackedCriteria.arrMins(frontInConstruction[i]) + "," +
                            PackedCriteria.changes(frontInConstruction[i]) + ") ");
                }

            }
            sb.append(" }");
            return sb.toString();
        }

    }



}
