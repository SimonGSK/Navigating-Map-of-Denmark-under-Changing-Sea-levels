package models.osm;

import models.Element;
import models.geometry.Coordinate;

/**
 * A point on the earth's surface defined by its latitude and longitude.
 * <p>
 * Each {@code Node} comprises at least an id number and a pair of {@link Coordinate}s
 * as defined by the World Geodetic System 1984 (WGS84).
 * <p>
 * Nodes serve two primary purposes in OpenStreetMap:
 * <ul>
 *   <li><b>Standalone point features:</b> A node can represent a discrete point of interest,
 *       such as a park bench, water well, traffic signal, or shop. These nodes typically have tags
 *       that describe the feature (e.g., {@code amenity=bench} or {@code shop=bakery}).</li>
 *   <li><b>Shape points in ways:</b> Nodes define the vertices of a {@link Way}. For example,
 *       nodes form the centerline of a road or the boundary of a building. Nodes used this way
 *       usually have no tags, but may have tags describing specific features along the way
 *       (e.g., {@code highway=traffic_signals} for a traffic light on a road).</li>
 * </ul>
 * <p>
 * A node can also be a member of a {@link Relation}, where it may have a specific role
 * describing its function within that relation.
 * <p>
 * <a href="https://wiki.openstreetmap.org/wiki/Node"><i>Source: OpenStreetMap Wiki; Node</i></a>
 */
public class Node extends Element {
    private final Coordinate coord;

    /**
     * Constructs a node at the specified geographic location.
     *
     * @param id  the unique identifier for this node in the OSM database.
     * @param lat the latitude of this node in degrees, ranging from -90° (South Pole) to +90° (North Pole).
     * @param lon the longitude of this node in degrees, ranging from -180° to +180°.
     */
    public Node(long id, double lat, double lon) {
        super(id);
        this.coord = new Coordinate(lat, lon);
    }

    /**
     * Returns the coordinate of this node.
     *
     * @return the {@link Coordinate} (latitude and longitude) of this node. Never {@code null}.
     */
    public Coordinate getCoord() {
        return coord;
    }
}
