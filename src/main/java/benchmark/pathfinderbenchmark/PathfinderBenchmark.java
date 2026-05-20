package benchmark.pathfinderbenchmark;

import models.osm.Node;
import models.parser.OsmData;
import models.parser.OsmParser;
import models.pathfinding.Pathfinder;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashMap;

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 20, time = 2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(java.util.concurrent.TimeUnit.NANOSECONDS)
public class PathfinderBenchmark {

    private Pathfinder pathfinder;
    private Node startNode;
    private Node targetNode;

    @Setup
    public void setup() throws Exception {
        OsmParser parser = new OsmParser("Bornholm.osm");
        OsmData osmData = parser.getData();
        HashMap<Long, Node> nodeMap = osmData.nodeMap();

        startNode  = nodeMap.get(1271128810L);
        targetNode = nodeMap.get(1175056665L);
        pathfinder = new Pathfinder();
    }

    @Benchmark
    public void dijkstra(Blackhole bh) {
        Pathfinder.Result result = pathfinder._shortestPath(startNode, targetNode, true);
        consume(result, bh);
    }

    @Benchmark
    public void aStar(Blackhole bh) {
        Pathfinder.Result result = pathfinder._shortestPath(startNode, targetNode, false);
        consume(result, bh);
    }

    private void consume(Pathfinder.Result result, Blackhole bh) {
        bh.consume(result.distances().getOrDefault(targetNode, Double.POSITIVE_INFINITY));
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(PathfinderBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}