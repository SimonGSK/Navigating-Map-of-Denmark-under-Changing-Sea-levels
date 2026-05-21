package benchmark.pathfinderbenchmark;

import models.osm.Node;
import models.parser.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Shared setup utilities for {@link PathfinderBenchmark}, providing hardcoded
 * OSM node IDs and a loader that parses the map and resolves them into
 * {@link Node} instances.
 */
public class PathfinderBenchmarkUtils {

    // --- Node IDs for long route ---
    public static final long LONG_START_ID   = 1271128810L;
    public static final long LONG_TARGET_ID  = 1175056665L;

    // --- Node IDs for medium route ---
    public static final long MEDIUM_START_ID  = 5718626495L;
    public static final long MEDIUM_TARGET_ID = 789443168L;

    // --- Node IDs for short route ---
    public static final long SHORT_START_ID  = 775535614L;
    public static final long SHORT_TARGET_ID = 1243354307L;

    /**
     * Parses {@code bornholm/bornholm.osm} and resolves the hardcoded node IDs
     * into a map of route name → {@code [start, target]} node pairs.
     *
     * @return a map with keys {@code "long"}, {@code "medium"}, and
     *         {@code "short"}, each holding a two-element {@link Node} array
     * @throws IOException if the OSM file cannot be read
     */
    public static Map<String, Node[]> loadNodes() throws IOException {
        System.out.println("Starting setup...");
        OsmParser parser = new OsmParser("bornholm/bornholm.osm");
        OsmData osmData = parser.getData();

        Map<String, Node[]> routes = new HashMap<>();
        routes.put("long",   new Node[]{ osmData.nodeMap().get(LONG_START_ID),   osmData.nodeMap().get(LONG_TARGET_ID) });
        routes.put("medium", new Node[]{ osmData.nodeMap().get(MEDIUM_START_ID), osmData.nodeMap().get(MEDIUM_TARGET_ID) });
        routes.put("short",  new Node[]{ osmData.nodeMap().get(SHORT_START_ID),  osmData.nodeMap().get(SHORT_TARGET_ID) });

        routes.forEach((name, nodes) ->
                System.out.printf("%-6s start=%-25s target=%s%n", name, nodes[0], nodes[1])
        );

        return routes;
    }
}