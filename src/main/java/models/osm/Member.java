package models.osm;

/**
 * Represents a member of a relation.
 * <p>
 * A {@code Member} is an immutable pairing of an OSM {@link Element} and an optional {@code role}
 * string that describes the function of that element within a {@link Relation}.
 * <p>
 * <b>Record Components:</b>
 * <ul>
 *   <li><b>element</b> – an {@link Element} (either a {@link Node}, {@link Way}, or {@link Relation})
 *                        that participates in the relation. Never {@code null}.</li>
 *   <li><b>role</b> – a string describing this member's function within the relation,
 *                     or {@code null}/{@code ""} if this member has no specific role.</li>
 * </ul>
 * <p>
 * <b>Examples:</b>
 * <ul>
 *   <li>In a multipolygon relation: a way member with role "outer" forms the outer boundary,
 *       or role "inner" forms a hole.</li>
 *   <li>In a bus route relation: nodes have role "stop" to mark stops along the route,
 *       while ways form the path with no role.</li>
 * </ul>
 * <p>
 * <a href="https://wiki.openstreetmap.org/wiki/Relation#Roles"><i>Source: OpenStreetMap Wiki; Relation Roles</i></a>
 */
public record Member(Element element, String role) {
}
