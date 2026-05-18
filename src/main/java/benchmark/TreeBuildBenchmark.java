package benchmark;

import models.RTree.SearchResults;
import models.RTree.Tree;
import models.geometry.BoundingBox;
import models.parser.*;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(0)
public class TreeBuildBenchmark {
    private static volatile MapData mapData;

    @Setup(Level.Trial)
    public void setup() throws IOException, ClassNotFoundException {
        if (mapData == null) {
            synchronized (TreeBuildBenchmark.class) {
                if (mapData == null) {
                    mapData = loadMapData();
                }
            }
        }
        System.out.println("MapData is loaded....");
    }

    private MapData loadMapData() throws IOException, ClassNotFoundException {
        String binPath = "/data/bornholm/bornholm.bin";

        System.out.println("Starting setup...");
        try {
            return BinaryReader.load(binPath);
        } catch (IOException | ClassNotFoundException ioException) {
            System.out.println("Binary file not found. Attempting to load .osm and .hc files and create binary file");

            OsmParser osmParser = new OsmParser("/bornholm/bornholm.osm");
            OsmData osmData = osmParser.getData();
            double meanLat = (osmData.bounds().maxLat() + osmData.bounds().minLat()) / 2.0;
            HeightCurveParser heightCurveParser = new HeightCurveParser("/bornholm/bornholm.hc", meanLat, osmData);
            BinaryWriter.write(osmData, heightCurveParser.getData(), binPath);

            System.out.println("Binary file created. Retrying setup...");
            return BinaryReader.load(binPath);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 3)
    @Measurement(iterations = 5)
    public void BuildTree(Blackhole bh) {
        bh.consume(new Tree(mapData.mbr, mapData.nodeMap, mapData.wayMap, mapData.relationMap));
    }
}