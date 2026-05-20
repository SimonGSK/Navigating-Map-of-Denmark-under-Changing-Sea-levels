package benchmark.TreeBenchmark;

import models.parser.*;

import java.io.IOException;

public class TreeBenchmarkUtils {
    public static MapData loadMapData() throws IOException, ClassNotFoundException {
        System.out.println("Starting setup...");
        String binPath = "src/main/Resources/data/benchmarking/benchmark_bornholm.bin";

        try {
            return BinaryReader.loadForBenchmark(binPath);
        } catch (IOException | ClassNotFoundException ioException) {
            System.out.println("Binary file not found. Attempting to load .osm file and create binary file");

            OsmParser osmParser = new OsmParser("bornholm/bornholm.osm");
            OsmData osmData = osmParser.getData();
            BinaryWriter.writeForBenchmark(osmData,binPath);

            System.out.println("Binary file created. Retrying setup...");
            return BinaryReader.loadForBenchmark(binPath);
        }
    }
}
