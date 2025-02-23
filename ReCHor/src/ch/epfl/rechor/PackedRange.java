package ch.epfl.rechor;

/**
 * Utility class used to work with intervals, creating intervals,
 * as well as extracting the data out of an interval.
 */

public final class PackedRange {
    /**
     * private constructor, as PackedRange cannot be instantiated
     */
    private PackedRange () {}

    /**
     * a function used to pack an interval inside an integer value
     * @param startInclusive the first integer in the interval
     * @param endExclusive the integer marking the end of the interval (excluded)
     * @throws IllegalArgumentException if:
     *      - startInclusive cannot be represented with 24 bits
     *      - representing values in the interval takes more than 8 bits
     * @return the integer containing the information about the interval
     */

    public static int pack(int startInclusive, int endExclusive) {
        Preconditions.checkArgument(((startInclusive >> 24) == 0)
                && ((endExclusive - startInclusive) >> 8 == 0));
        return (startInclusive << 8) | (endExclusive - startInclusive);

    }

    /**
     * function used to extract the length of the interval,
     *      i.e. the cardinality of the interval
     * @param interval integer from which we extract the length of the interval
     * @return  lhe length of the interval
     */

    public static int length (int interval) {
        return interval & 0xFF;
    }

    /**
     * function used to extract the start of the interval, who was previously packed into an integer
     * @param interval integer from which we extract the start of the interval
     * @return the start of the interval
     */

    public static int startInclusive(int interval) {
        int shiftedStart = interval >> 8;
        return shiftedStart & 0xFF;

    }

    /**
     * function used to extract the end of the interval (excluded), who was previously packed into an integer
     * @param interval integer from which we extract the end of the interval
     * @return the end of the interval
     */

    public static int endExclusive(int interval) {
        int shiftedStart = interval >> 8;
        return shiftedStart + length(interval);

    }

}
