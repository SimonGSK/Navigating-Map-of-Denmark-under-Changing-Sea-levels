package benchmark.pathfinderbenchmark;

import models.osm.Node;
import models.parser.OsmData;
import models.parser.OsmParser;
import models.pathfinding.Algorithm;
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

/**
 * JMH benchmark comparing Dijkstra and A* across three route lengths.
 *
 * <p>Each benchmark method returns a combined size value to prevent the JVM
 * from optimising away the pathfinding call as dead code.
 */
@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 20, time = 2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class PathfinderBenchmark {
    /**
     * Selects the algorithm. JMH runs the full benchmark suite once per value.
     */
    @Param({"DIJKSTRA", "A_STAR"})
    Algorithm algorithm;

    private Pathfinder pathfinder;

    private Node longStart,   longTarget;
    private Node mediumStart, mediumTarget;
    private Node shortStart,  shortTarget;

    /**
     * Loads the pre-defined start/target node pairs for each route length from
     * {@link PathfinderBenchmarkUtils} once per trial.
     *
     * @throws IOException if the underlying OSM data cannot be read
     */
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
    /**
     * Benchmarks pathfinding over a long route.
     *
     * @return a combined result size to prevent dead-code elimination
     */
    @Benchmark
    public int longPath() {
        Pathfinder.Result r = pathfinder._shortestPath(longStart, longTarget, algorithm);
        return r.visitedNodes().size() + r.previousNodes().size() + r.distances().size();
    }

    // ── Medium ──────────────────────────────────────────────────────────────
    /**
     * Benchmarks pathfinding over a medium route.
     *
     * @return a combined result size to prevent dead-code elimination
     */
    @Benchmark
    public int mediumPath() {
        Pathfinder.Result r = pathfinder._shortestPath(mediumStart, mediumTarget, algorithm);
        return r.visitedNodes().size() + r.previousNodes().size() + r.distances().size();
    }

    // ── Short ───────────────────────────────────────────────────────────────
    /**
     * Benchmarks pathfinding over a short route.
     *
     * @return a combined result size to prevent dead-code elimination
     */
    @Benchmark
    public int shortPath() {
        Pathfinder.Result r = pathfinder._shortestPath(shortStart, shortTarget, algorithm);
        return r.visitedNodes().size() + r.previousNodes().size() + r.distances().size();
    }

    /**
     * Entry point for running the benchmark directly from the IDE or command line.
     *
     * @param args command-line arguments (unused)
     * @throws Exception if the JMH runner fails
     */
    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(PathfinderBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}