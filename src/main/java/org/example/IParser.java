package dk.itu.parser.interfaces;

import java.util.HashMap;
import java.util.List;

import dk.itu.parser.models.OsmNode;
import dk.itu.parser.models.OsmRelation;
import dk.itu.parser.models.OsmWay;

public interface IParser {

    void parse();

    HashMap<Long, OsmNode> getOsmNodeMap();

    HashMap<Long, OsmWay> getOsmWayMap();

    HashMap<Long, OsmRelation> getOsmRelationMap();

    List<Double> getBoundingBox();
}
