package ch.epfl.javelo.data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.List;

public final class Graph {

    Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets){
        System.out.println("called");
    }

    static Graph loadFrom(Path basePath) throws IOException {
        Path basePath = Path.of("lausanne");

        //initialize nodes
        Path filePathNodes = basePath.resolve("nodes.bin");
        IntBuffer nodesBuffer;
        try (FileChannel channel = FileChannel.open(filePathNodes)) {
            nodesBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asIntBuffer();
        }

        //initialize GraphEdge - edges
        Path filePathEdges = basePath.resolve("edges.bin");
        ByteBuffer edgesBuffer;
        try (FileChannel channel = FileChannel.open(filePathEdges)) {
            edgesBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asReadOnlyBuffer();
        }

        //initialize GraphEdge - profiles
        Path filePathProfiles = basePath.resolve("profiles_ids.bin");
        IntBuffer profilesBuffer;
        try (FileChannel channel = FileChannel.open(filePathProfiles)) {
            profilesBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asIntBuffer();
        }

        //initialize GraphEdge - elevations
        Path filePathElevations = basePath.resolve("elevations.bin");
        ShortBuffer elevationsBuffer;
        try (FileChannel channel = FileChannel.open(filePathElevations)) {
            elevationsBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asShortBuffer();
        }

        //initialize sectors
        Path filePathSectors = basePath.resolve("sectors.bin");
        ByteBuffer sectorsBuffer;
        try (FileChannel channel = FileChannel.open(filePathElevations)) {
            sectorsBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asReadOnlyBuffer();
        }

        //initialize attributes
        Path filePathAttributes = basePath.resolve("attributes.bin");
        LongBuffer sectorsAttributes;
        try (FileChannel channel = FileChannel.open(filePathElevations)) {
            sectorsAttributes = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();


        }

        return new Graph(new GraphNodes(nodesBuffer), new GraphSectors(sectorsBuffer), new GraphEdges(edgesBuffer, profilesBuffer, elevationsBuffer));
    }
}
