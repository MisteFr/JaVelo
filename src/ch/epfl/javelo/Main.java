package ch.epfl.javelo;

import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        System.out.println(Bits.extractUnsigned(0b11110000000000000000000000000000, 28, 4));

        Path filePath = Path.of("lausanne/nodes_osmid.bin");
        LongBuffer osmIdBuffer;
        try (FileChannel channel = FileChannel.open(filePath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
            System.out.println(osmIdBuffer.get(2022));
        }catch (IOException e){
            System.out.println(e);
        }
    }
}
