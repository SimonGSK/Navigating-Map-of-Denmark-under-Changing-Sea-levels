package models.pathfinding;

import models.osm.Node;
import models.parser.OsmData;
import models.parser.OsmParser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.util.HashMap;
import java.util.List;
import java.util.function.IntToDoubleFunction;


public class Benchmark {
    public static void main(String[] args) throws IOException {
        new Benchmark();
    }

    public Benchmark() throws IOException {
        SystemInfo();
        System.out.println("Starting benchmark...");

        try {
            System.setOut(new PrintStream(new FileOutputStream("benchmark_output.txt", true)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Load OSM data
        OsmParser parser = new OsmParser("Bornholm.osm");
        OsmData osmData = parser.getData();
        HashMap<Long, Node> nodeMap = osmData.nodeMap();

        // Get two nodes from the map (you may need to adjust these node IDs based on your Bornholm.osm data)
        // For demonstration, using the first two valid nodes found
        List<Node> nodes = nodeMap.values().stream()
                .filter(n -> !n.isSubmerged())  // Only use non-submerged nodes
                .limit(2)
                .toList();

        if (nodes.size() < 2) {
            System.out.println("Not enough valid nodes found for benchmarking");
            return;
        }

        long startId = 1271128810;
        long endId = 1175056665;

        Node startNode = nodeMap.get(startId);
        Node targetNode = nodeMap.get(endId);

        Pathfinder pathfinder = new Pathfinder();

        System.out.println("\n=== Pathfinding Benchmark ===");
        System.out.printf("Start Node: ID=%d, Lat=%.4f, Lon=%.4f%n",
                startNode.getId(), startNode.getLat(), startNode.getLon());
        System.out.printf("Target Node: ID=%d, Lat=%.4f, Lon=%.4f%n",
                targetNode.getId(), targetNode.getLat(), targetNode.getLon());
        System.out.println();

        // ── Warmup ──────────────────────────────────────────────────────────────
        int WARMUP_ROUNDS = 5;
        System.out.println("Warming up...");
        for (int i = 0; i < WARMUP_ROUNDS; i++) {
            pathfinder._shortestPath(startNode, targetNode, true);  // Dijkstra
            pathfinder._shortestPath(startNode, targetNode, false); // A*
        }
        System.out.println("Warmup complete.\n");

// ── Benchmarks ───────────────────────────────────────────────────────────
        Mark8("Dijkstra", "",
                i -> {
                    Pathfinder.Result result = pathfinder._shortestPath(startNode, targetNode, true);
                    return result.distances().getOrDefault(targetNode, Double.POSITIVE_INFINITY);
                },
                20, 2.0);  // 20 repetitions, minimum 2 seconds

        Mark8("A*", "",
                i -> {
                    Pathfinder.Result result = pathfinder._shortestPath(startNode, targetNode, false);
                    return result.distances().getOrDefault(targetNode, Double.POSITIVE_INFINITY);
                },
                20, 2.0);
}

    public static double Mark8(String msg, String info, IntToDoubleFunction f,
                               int n, double minTime) {
        int count = 1, totalCount = 0;
        double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
        do {
            count *= 2;
            st = sst = 0.0;
            for (int j=0; j<n; j++) {
                Timer t = new Timer();
                for (int i=0; i<count; i++)
                    dummy += f.applyAsDouble(i);
                runningTime = t.check();
                double time = runningTime * 1e9 / count;
                st += time;
                sst += time * time;
                totalCount += count;
            }
        } while (runningTime < minTime && count < Integer.MAX_VALUE/2);
        double mean = st/n, sdev = Math.sqrt((sst - mean*mean*n)/(n-1));
        System.out.printf("%-25s %s%15.1f ns %10.2f %10d%n", msg, info, mean, sdev, count);
        return dummy / totalCount;
    }

    public static void SystemInfo() {
        System.out.printf("# OS:   %s; %s; %s%n",
                System.getProperty("os.name"),
                System.getProperty("os.version"),
                System.getProperty("os.arch"));
        System.out.printf("# JVM:  %s; %s%n",
                System.getProperty("java.vendor"),
                System.getProperty("java.version"));
        // The processor identifier works only on MS Windows:
        System.out.printf("# CPU:  %s; %d \"cores\"%n",
                System.getenv("PROCESSOR_IDENTIFIER"),
                Runtime.getRuntime().availableProcessors());
        java.util.Date now = new java.util.Date();
        System.out.printf("# Date: %s%n",
                new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(now));
    }

}