package benchmark.TreeBenchmark;

import models.RTree.Tree;
import models.geometry.BoundingBox;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 10, time = 2)

/**
 * Measures how Tree.search() and Tree.getNearestNode() scales with dataset size N.
 */
public class TreeScalingBenchmark extends AbstractTreeBenchmark {
    private final double ZOOM = 15.0;

    @Param({"0.05", "0.10", "0.25", "0.50", "0.75", "1.00"})
    public double fraction;

    private Tree tree;

    BoundingBox small = new BoundingBox(55.13871011899183,14.956436602662002,55.141324186563665,14.965137232798007);


    @Setup
    public void setup() throws IOException, ClassNotFoundException {
        super.setup();

        Map<Long, Node> nodeSubset = sliceMap(mapData.nodeMap,fraction);
        Map<Long, Way> waySubset = sliceMap(mapData.wayMap,fraction);
        Map<Long, Relation> relationSubset = sliceMap(mapData.relationMap,fraction);

        tree = new Tree(mapData.mbr, nodeSubset,waySubset,relationSubset);
        tree.setZoomLevel(ZOOM);

    }

    private static <K, V> Map<K,V> sliceMap(Map<K,V> source, double fraction) {
        int target = (int) Math.floor(source.size() * fraction);
        Map<K,V> out = new HashMap<>(target);
        int i = 0;
        for (Map.Entry<K,V> e : source.entrySet()) {
            if (i++ >= target) {
                break;
            }
            out.put(e.getKey(), e.getValue());
        }
        return out;
    }

    @Benchmark
    public void searchHalfIsland(Blackhole bh) {
        consumeSearchResults(tree.search(halfIslandViewport), bh);
    }

    @Benchmark
    public void searchSmall(Blackhole bh) {
        consumeSearchResults(tree.search(small),bh);
    }

    @Benchmark
    public void getNearestNode_townCenter(Blackhole bh) {
        bh.consume(tree.getNearestNode(townViewport.getCenter()));
    }
}
