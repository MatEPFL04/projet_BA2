package ch.epfl.rechor.journey;
import ch.epfl.rechor.Preconditions;

import java.util.Objects;

/**
 * @author Matteo Lazzari (397247)
 * @param name
 *      name of the location of the stop
 * @param platformName
 *      name of the platform, can be a letter, a number. For instance: "3A","1"
 * @param longitude
 * @param latitude
 * @throws NullPointerException
 *      in case the name of the stop is null
 * @throws IllegalArgumentException
 *      in the case the location, consisting of the longitude, latitude is incorrect
 */

public record Stop(String name, String platformName, double longitude, double latitude) {
    /**
     *
     * @param name
     * @param platformName
     * @param longitude
     * @param latitude
     * @throws NullPointerException thrown if no name is given
     * @throws IllegalArgumentException in the case the location, consisting of the longitude, latitude is incorrect
     */

    public Stop {
        Objects.requireNonNull(name);
        Preconditions.checkArgument(Math.abs(longitude) <= 180 || Math.abs(latitude) <= 90);
    }
}

