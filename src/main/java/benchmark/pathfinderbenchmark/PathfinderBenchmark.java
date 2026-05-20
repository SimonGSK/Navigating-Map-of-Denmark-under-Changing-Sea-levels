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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 20, time = 2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class PathfinderBenchmark {

    @Param({"true", "false"})
    boolean isDijkstra;

    private Pathfinder pathfinder;

    private Node longStart,   longTarget;
    private Node mediumStart, mediumTarget;
    private Node shortStart,  shortTarget;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        Map<String, Node[]> routes = PathfinderBenchmarkUtils.loadNodes();
        longStart   = routes.get("long")[0];
        longTarget  = routes.get("long")[1];
        mediumStart = routes.get("medium")[0];
        mediumTarget= routes.get("medium")[1];
        shortStart  = routes.get("short")[0];
        shortTarget = routes.get("short")[1];
        pathfinder  = new Pathfinder();
    }

    // ── Long ────────────────────────────────────────────────────────────────
    @Benchmark
    public int longPath() {
        Pathfinder.Result r = pathfinder._shortestPath(longStart, longTarget, isDijkstra);
        return r.visitedNodes().size() + r.previousNodes().size() + r.distances().size();
    }

    // ── Medium ──────────────────────────────────────────────────────────────
    @Benchmark
    public int mediumPath() {
        Pathfinder.Result r = pathfinder._shortestPath(mediumStart, mediumTarget, isDijkstra);
        return r.visitedNodes().size() + r.previousNodes().size() + r.distances().size();
    }

    // ── Short ───────────────────────────────────────────────────────────────
    @Benchmark
    public int shortPath() {
        Pathfinder.Result r = pathfinder._shortestPath(shortStart, shortTarget, isDijkstra);
        return r.visitedNodes().size() + r.previousNodes().size() + r.distances().size();
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(PathfinderBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}