package ch.epfl.rechor.journey;

import java.util.List;


/**
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 * enum used to list all the vehicules that can be used for a transport
 */

public enum Vehicle {
    TRAM,
    METRO,
    TRAIN,
    BUS,
    FERRY,
    AERIAL_LIFT,
    FUNICULAR;
    /**
     * an immutable list representing all the vehicules in ReCHOR.
     */
    public static final List<Vehicle> ALL = List.of(values());

}
