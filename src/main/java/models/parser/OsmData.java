package models.parser;

import models.geometry.BoundingBox;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;

import java.util.HashMap;

public record OsmData(BoundingBox bounds, HashMap<Long, Node> nodeMap, HashMap<Long, Way> wayMap, HashMap<Long, Relation> relationMap) {
}
