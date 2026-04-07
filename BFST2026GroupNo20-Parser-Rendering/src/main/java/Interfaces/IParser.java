package Interfaces;

import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;

import java.util.HashMap;
import java.util.List;

public interface IParser {

    void parse();

    HashMap<Long, Node> getOsmNodeMap();

    HashMap<Long, Way> getOsmWayMap();

    HashMap<Long, Relation> getOsmRelationMap();

    List<Double> getBoundingBox();
}
