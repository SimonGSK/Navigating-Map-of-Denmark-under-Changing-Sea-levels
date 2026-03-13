package org.example;

import java.util.HashMap;

/**
 * Elements are the basic components of OpenStreetMap's conceptual data model of the physical world.
 * <p>
 * <b>There are three types of elements:</b>
 * <li> nodes (defining points in space),
 * <li> ways (defining linear features and area boundaries), and
 * <li> relations (defining how other elements work together).
 * <p>
 * <a href="https://wiki.openstreetmap.org/wiki/Elements#Elements"><i>Source: OpenStreetMap Wiki; Elements</i></a>
 */
public abstract class Element {
    final private long id;
    private final HashMap<String, String> tags;
    protected MBR mbr;

    public Element(long id, HashMap<String, String> tags) {
        this.id = id;
        this.tags = tags;
    }

    public long getId() {
        return id;
    }

    /**
     * Returns a copy of all tags associated with this element.
     * <p>
     * The returned HashMap is a defensive copy, so modifications to it will not
     * affect this element's tags.
     * <p>
     * If no tags have been added to this element, this method returns {@code null}.
     * This allows callers to easily distinguish between "no tags added yet" and
     * "tags exist but the map is empty".
     *
     * @return a copy of the HashMap containing all key-value tag pairs, or {@code null}
     * if no tags have been added to this element.
     */
    protected HashMap<String, String> getTags() {
        if (tags == null) {
            return null;
        }
        return new HashMap<>(tags);
    }
}
