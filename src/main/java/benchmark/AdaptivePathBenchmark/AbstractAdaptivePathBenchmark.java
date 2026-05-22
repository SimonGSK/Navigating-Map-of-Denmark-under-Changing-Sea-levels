package benchmark.AdaptivePathBenchmark;


import models.parser.MapData;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.io.IOException;

@State(Scope.Benchmark)
public abstract class AbstractAdaptivePathBenchmark {
    public static volatile MapData mapData;

    @Setup(Level.Trial)
    public void setup() throws IOException, ClassNotFoundException {
        if (mapData == null) {
            mapData = AdaptivePathBenchmarkUtils.loadMapData();
        }
        System.out.println("MapData is loaded....");
    }
}
