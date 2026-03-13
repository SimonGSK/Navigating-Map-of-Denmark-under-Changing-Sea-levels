package org.example;

import java.util.HashMap;
import java.util.List;

public interface IParser {

    void parse();

    HashMap<Long, Node> getOsmNodeMap();

    HashMap<Long, Way> getOsmWayMap();

    HashMap<Long, Relation> getOsmRelationMap();

    List<Double> getBoundingBox();
}
