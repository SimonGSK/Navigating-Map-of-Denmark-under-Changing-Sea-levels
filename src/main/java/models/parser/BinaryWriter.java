package models.parser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class BinaryWriter {
    public static void write(OsmParser osmParser, HeightCurveData heightCurveData, String outPath) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outPath))) {
            out.writeObject(osmParser.getData().nodeMap());
            out.writeObject(osmParser.getData().wayMap());
            out.writeObject(osmParser.getData().relationMap());
            out.writeObject(osmParser.getData().bounds());
            out.writeObject(heightCurveData);
        }
    }
}
