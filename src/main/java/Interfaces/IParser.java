package Interfaces;

import models.geometry.BoundingBox;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;

import java.util.HashMap;

public interface IParser {

    void parse();

    HashMap<Long, Node> getOsmNodeMap();

    HashMap<Long, Way> getOsmWayMap();

    HashMap<Long, Relation> getOsmRelationMap();

    BoundingBox getBoundingBox();
}
