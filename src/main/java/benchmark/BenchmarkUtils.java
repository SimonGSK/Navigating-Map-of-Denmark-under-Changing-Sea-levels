package benchmark;

import models.parser.*;

import java.io.IOException;

public class BenchmarkUtils {
    public static MapData loadMapData() throws IOException, ClassNotFoundException {
        String binPath = "/data/bornholm/bornholm.bin";

        System.out.println("Starting setup...");
        try {
            return BinaryReader.load(binPath);
        } catch (IOException | ClassNotFoundException ioException) {
            System.out.println("Binary file not found. Attempting to load .osm and .hc files and create binary file");

            OsmParser osmParser = new OsmParser("bornholm/bornholm.osm");
            OsmData osmData = osmParser.getData();
            double meanLat = (osmData.bounds().maxLat() + osmData.bounds().minLat()) / 2.0;
            BinaryWriter.write(osmData,heightCurveParser.getData(),binPath);

            System.out.println("Binary file created. Retrying setup...");
            return BinaryReader.load(binPath);
        }
    }
}
