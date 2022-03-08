package ch.epfl.javelo;

import java.nio.IntBuffer;

public class Main {

    public static void main(String[] args) {
        System.out.println(Bits.extractUnsigned(0b11110000000000000000000000000000, 28, 4));
    }
}
