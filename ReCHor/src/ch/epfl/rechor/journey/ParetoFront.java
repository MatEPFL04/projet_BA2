package ch.epfl.rechor.journey;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.LongConsumer;

/**
 * final class ParetoFront, used to represent a front,
 * composed of tuples that represent the characteristics of a journey,
 * either two characteristics: the arrival time and the changes (in a long), or three criteria, by including the departure time.
 * <p>
 * The front is created by the builder, who creates an instance of a front
 */
public final class ParetoFront {
    private long [] frontTuples;
    /**
     * The constant EMPTY.
     */
    public static final ParetoFront EMPTY = new ParetoFront(new long [0]);

    private ParetoFront (long [] packedCriteria) {
        this.frontTuples = packedCriteria;

    }

    /**
     * Size int.
     *
     * @return the int
     */
    public int size() {
        return frontTuples.length;
    }

    /**
     * Get long.
     *
     * @param arrMins the arr mins
     * @param changes the changes
     * @return the long
     * @throws NoSuchElementException the no such element exception
     */
    public long get(int arrMins, int changes) throws NoSuchElementException {
        for (int i = 0; i < size(); i++) {
            if (PackedCriteria.arrMins(frontTuples[i]) == arrMins
                    && PackedCriteria.changes(frontTuples[i]) == changes) {
                return frontTuples[i];
            }
        }
        throw new NoSuchElementException();
    }

    /**
     * For each.
     *
     * @param action the action
     */
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

    /**
     * The type Builder.
     */
    public static class Builder {
        private long [] frontInConstruction;
        private int effectiveSize;


        /**
         * Instantiates a new Builder.
         */
        public Builder() {
            frontInConstruction = new long[2];
            effectiveSize = 0;

        }

        /**
         * Instantiates a new Builder.
         *
         * @param that the that
         */
        public Builder (Builder that) {
            frontInConstruction = that.frontInConstruction;
            effectiveSize = that.effectiveSize;

        }

        /**
         * Is empty boolean.
         *
         * @return the boolean
         */
        public boolean isEmpty(){
            return (effectiveSize == 0);
        }

        /**
         * Clear builder.
         *
         * @return the builder
         */
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

        private void frontResize() {
            if ((effectiveSize + 1) > frontInConstruction.length) {
                int newCapacity =  (int) (1.5 * frontInConstruction.length);
                frontInConstruction = Arrays.copyOf(frontInConstruction,newCapacity);
            }
        }

        private int compactNonDominated(long[] array, long packedTuple) {
            int insertionIdx = getInsertionIdx(packedTuple);
            int destPos = insertionIdx;

            for (int src = insertionIdx; src < effectiveSize; src += 1) {
                if (PackedCriteria.dominatesOrIsEqual(packedTuple, array[src])) {
                    continue;
                }
                if (destPos != src) {
                    array[destPos] = array[src];
                }
                destPos += 1;
            }

            return destPos; //l'index de fin de notre tableau avec les éléments non-dominés
        }

        /**
         * We first find the correct insertion index,
         * then we check if the new tuple is dominated,
         * after that, we compact the elements that start at insertionIdx all the way to the effectivesize and
         * we bring them back as a compact sequence starting at insertionIdx
         * and lastly we shift the position by once to insert our new tuple
         *
         * @param packedTuple the packed tuple
         * @return the new paretoFront
         */
        public Builder add(long packedTuple) {

            int insertionIdx = getInsertionIdx(packedTuple);
            for (int i = 0; i < insertionIdx; i++) {
                if (PackedCriteria.dominatesOrIsEqual(frontInConstruction[i], packedTuple)) {
                    return this;
                }
            }
            effectiveSize = compactNonDominated(frontInConstruction,packedTuple);
            frontResize();

            System.arraycopy(frontInConstruction, insertionIdx, frontInConstruction, insertionIdx + 1, effectiveSize - insertionIdx);
            frontInConstruction[insertionIdx] = packedTuple;
            effectiveSize++;


            return this;
        }

        /**
         * The entry point of application.
         *
         * @param args the input arguments
         */
        public static void main(String[] args) {
            Builder testFront = new Builder();
            long tuple1 = PackedCriteria.pack(100,100,0);
            long tuple2 = PackedCriteria.pack(120,80,0);
            long tuple4 = PackedCriteria.pack(130,59,0);
            long tuple5 = PackedCriteria.pack(120,79,0);
            long tuple6 = PackedCriteria.pack(40,40,0);
            long tuple7 = PackedCriteria.pack(50,28,0);
            testFront.add(tuple1).add(tuple2).add(tuple4).add(tuple5).add(tuple6).add(tuple7);
            System.out.println(testFront);

        }

        /**
         * Add builder.
         *
         * @param arrMins the arr mins
         * @param changes the changes
         * @param payload the payload
         * @return the builder
         */
        public Builder add (int arrMins, int changes, int payload) {
            long packedTuple = PackedCriteria.pack(arrMins,changes,payload);

            add(packedTuple);
            return this;
        }

        /**
         * Add all builder.
         *
         * @param that the that
         * @return the builder
         */
        public Builder addAll(Builder that) {
            for (int i = 0; i < that.effectiveSize ; i++) {
                add(that.frontInConstruction[i]);
            }
            return this;
        }

        /**
         * Fully dominates boolean.
         *
         * @param that    the that
         * @param depMins the dep mins
         * @return the boolean
         */
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

        /**
         * For each.
         *
         * @param action the action
         */
        public void forEach(LongConsumer action) {
            for (int i = 0; i < effectiveSize; i++) {
                action.accept(frontInConstruction[i]);
            }
        }

        /**
         * Build pareto front.
         *
         * @return the pareto front
         */
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
