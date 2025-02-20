package ch.epfl.rechor;

/**
 * @author Matteo Lazzari (397247)
 * class used in various files of the project to throw exceptions
 * @
 */

public final class Preconditions {
    /**
     * private constructor, as the class is final and also not instantiable.
     */
    private Preconditions(){}

    /**
     *
     * @param shouldBeTrue
     *      the boolean evaluated by the method, if the boolean is false, an exception is thrown
     * @exception IllegalArgumentException
     *      thrown exception by the method if the argument is incorrect
     */
    public static void checkArgument(boolean shouldBeTrue){
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
