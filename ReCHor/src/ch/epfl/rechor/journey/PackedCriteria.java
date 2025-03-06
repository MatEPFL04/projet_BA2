package ch.epfl.rechor.journey;

import ch.epfl.rechor.PackedRange;
import ch.epfl.rechor.Preconditions;

/**
 * non-instantiable class used to extract information concerning departure time, changes  and so on
 *      out of a long value, a criteria
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */

public final class PackedCriteria {
    /**
     * private constructor as the class is not instantiable
     */
    private PackedCriteria () { }

    /**
     * method used to pack the arrival time, changes, and a payload
     *      inside a long value, without any information inside the criteria about the departure time
     * @param arrMins the arrival time
     * @param changes the number of changes
     * @param payload the payload packed in the least-significant bits of the long
     * @return the (long) packed-value containing the information given by the method
     */

    public static long pack(int arrMins, int changes, int payload) {
        Preconditions.checkArgument(-240 <= arrMins && arrMins < 2880 && changes >= 0 && changes < 128);
        //Preconditions. checkArgument((changes >> 7) == 0  && ((arrMins+240) >> 12) == 0);
        long payLoadAdjusted = Integer.toUnsignedLong(payload);
        long arrMinsShifted = ((long) ((arrMins + 240))) << 39; // 39 (intervalle "bas" h.d'arr 2.2.5) - 1
        long changesShifted = ((long) changes) << 32;

        return arrMinsShifted | changesShifted | payLoadAdjusted;

    }

    /**
     * function returning true if the (complement) of the departure time is in the criteria,
     *      false otherwise
     * @param criteria the criteria (long value)
     * @return true if the (complement) of the departure time is in the criteria,
     *      false otherwise
     */

    public static boolean hasDepMins(long criteria) {
        return ((criteria >> 50) != 0);
    }

    /**
     * function returning the (complement of) departure time (in minutes)
     * @param criteria the given criteria
     * @throws IllegalArgumentException if:
     *      - the criteria is not strictly positive
     *      - the criteria doesn't contain any information about the departure time
     * @return the (complement of) departure time (in minutes)
     */

    public static int depMins(long criteria) {
        Preconditions.checkArgument(hasDepMins(criteria));
        return (int)  (4095 - ((criteria >> 51) + 240));

    }

    /**
     * function returning the arrival time (in minutes)
     * @param criteria the given criteria
     * @throws IllegalArgumentException if:
     *      - the criteria is not strictly positive
     * @return the (complement of) arrival time (in minutes)
     */

    public static int arrMins(long criteria) {
        return (int) (((criteria >> 39) & 0xFFF)) -240;

    }

    /**
     * function returning the number of changes (extracted from the criteria)
     * @param criteria the given criteria
     * @throws IllegalArgumentException if:
     *      - the criteria is not strictly positive
     * @return the number of changes
     */

    public static int changes(long criteria) {
        return (int) ((criteria) >> 32) & 0x7F;

    }

    /**
     * function returning the payload of a criteria
     * @param criteria the given criteria
     * @throws IllegalArgumentException if:
     *      - the criteria is not strictly positive
     * @return the payload (32 least-significant bits)
     */

    public static int payload(long criteria) {
        return (int) criteria;

    }

    /**
     * function returning true if criteria dominates or is equal to criteria2,
     *      false otherwise
     * @param criteria1 the first given criteria
     * @param criteria2 the second given criteria
     * @throws IllegalArgumentException if:
     *      - the criteria is not strictly positive
     *      - one of the criteria has information about departure time and not the other one
     * @return true if criteria dominates or is equal to criteria2
     */

    public static boolean dominatesOrIsEqual(long criteria1, long criteria2) {

        Preconditions.checkArgument(hasDepMins(criteria1)==hasDepMins(criteria2));
        if (hasDepMins(criteria1)) {


            return (depMins(criteria1) >= depMins(criteria2))
                    && (arrMins(criteria1) <= arrMins(criteria2))
                    && (changes(criteria1) <= changes(criteria2));
        }
        else {
            return (arrMins(criteria1) <= arrMins(criteria2))
                    && (changes(criteria1) <= changes(criteria2));
        }

    }
    /**
     * function used to remove the information in bits 51-62 about the departure time
     * @param criteria given criteria
     * @return a (long) criteria without information about the departure time
     */

    public static long withoutDepMins(long criteria) {
        return (criteria & 0x3FFFFFFFFFFFFL);

    }

    /**
     *
     * @param criteria given criteria
     * @param depMins1 departure time in minutes that needs to be embedded in the criteria
     * @return a (long) criteria with information about the departure time, given by depMins1
     */

    public static long withDepMins(long criteria, int depMins1) {
        Preconditions.checkArgument(-240 <= depMins1 && depMins1 < 2880);
        int positiveComplementedMinutes = 4095 - (depMins1 + 240);

        long cleanedCriteria = withoutDepMins(criteria);
        return cleanedCriteria | ((long) positiveComplementedMinutes << 51);
    }

    /**
     * function used to increment by 1 the changes embedded in the criteria
     * @param criteria given criteria
     * @return  a new criteria with a 1-increment in the bits concerning the changes
     */

    public static long withAdditionalChange(long criteria) {
        Preconditions.checkArgument(changes(criteria) < 128);
        long clearedCriteria = criteria & ~(0b1111111L << 32);
        int newChange = changes(criteria) + 1;
        return clearedCriteria | ((long) newChange << 32);

    }

    /**
     *  function used to embed a specific payload inside a criteria (long value)
     * @param criteria given criteria
     * @param payload1 new payload that needs to be added
     * @return criteria with the new payload
     */


    public static long withPayload(long criteria, int payload1) {
        long payLoadAdjusted = Integer.toUnsignedLong(payload1);
        long criteriaWithoutPreviousPayload = criteria & 0xFFFFFFFF00000000L;
        return (criteriaWithoutPreviousPayload) | payLoadAdjusted;


    }




}
