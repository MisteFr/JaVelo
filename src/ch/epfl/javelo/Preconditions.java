package ch.epfl.javelo;

/**
 * Preconditions Class
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public final class Preconditions {
    private Preconditions() {
    }

    /**
     * That method is used to check the validity of the shouldBeTrue argument.
     *
     * @param shouldBeTrue the condition that needs to be checked.
     * @throws IllegalArgumentException if shouldBeTrue is false
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
