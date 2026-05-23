package models.parser;

import models.geometry.BoundingBox;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;

import java.util.HashMap;

/**
 * Parsed OSM data containers.
 * @param bounds bounding box of the dataset
 * @param nodeMap nodes by id
 * @param wayMap ways by id
 * @param relationMap relations by id
 */
public record OsmData(BoundingBox bounds, HashMap<Long, Node> nodeMap, HashMap<Long, Way> wayMap, HashMap<Long, Relation> relationMap) {
}
