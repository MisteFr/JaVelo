package ch.epfl.javelo;

public final class Preconditions {
    private Preconditions() {
    }

    /**
     * That method is used to check the validity of the shouldBeTrue argument.
     * @param shouldBeTrue the condition that needs to be checked.
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
