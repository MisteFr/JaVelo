package ch.epfl.javelo;

import java.io.IOException;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        float f = Float.NEGATIVE_INFINITY;
        System.out.println(f == Float.NEGATIVE_INFINITY);
    }
}
