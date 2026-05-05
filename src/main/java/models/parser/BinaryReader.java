package models.parser;

import models.geometry.BoundingBox;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;

public class BinaryReader {
    public static MapData load(String resourcePath) throws IOException, ClassNotFoundException {
        InputStream is = BinaryReader.class.getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }
        try(ObjectInputStream in = new ObjectInputStream(BinaryReader.class.getResourceAsStream(resourcePath))) {
            HashMap<Long, Node> nodes = (HashMap<Long, Node>) in.readObject();
            HashMap<Long, Way> ways = (HashMap<Long, Way>) in.readObject();
            HashMap<Long, Relation> relations = (HashMap<Long, Relation>) in.readObject();
            BoundingBox mbr = (BoundingBox) in.readObject();
            HeightCurveData heightCurveData = (HeightCurveData) in.readObject();
            return new MapData(ways, relations, nodes, mbr, heightCurveData);
        }
    }
}
