package Elements;

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
    private HashMap<String, String> tags;

    public Element(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    /**
     * Associates the specified value with the specified key in this element's tags.
     * If a tag with the given key already exists, the existing value is preserved
     * and this method returns without modification (put-if-absent semantics).
     * <p>
     * This method will lazily initialize the tags map on first use.
     *
     * @param key   the tag key with which the specified value is to be associated.
     *              Must not be {@code null}.
     * @param value the tag value to be associated with the specified key.
     *              Must not be {@code null}.
     * @return the previous value associated with {@code key}, or
     * {@code null} if there was no mapping for {@code key}.
     * @see HashMap#putIfAbsent(Object, Object)
     */
    protected String addTag(String key, String value) {
        if (tags == null) {
            tags = new HashMap<>();
        }

        return tags.putIfAbsent(key, value);
    }

    /**
     * Modifies the value associated with the specified key in this element's tags.
     * <p>
     * This method will ONLY modify the tag if the key already exists. It will not
     * add new tags.
     *
     * @param key   the tag key to modify. Must not be {@code null}.
     * @param value the new tag value to associate with the specified key. Must not be {@code null}.
     * @return {@code true} if the tag existed and was updated, {@code false} if the key
     * was not found in the tags.
     */
    protected boolean modifyTag(String key, String value) {
        if (tags == null || !tags.containsKey(key)) {
            return false;
        }

        tags.put(key, value);
        return true;
    }

    /**
     * Returns the value associated with the specified tag key.
     * <p>
     * This method safely handles the case where no tags have been added to this element.
     *
     * @param key the tag key to search for. Must not be {@code null}.
     * @return the value associated with {@code key}, or {@code null} if no tag with
     * the given key exists or if no tags have been added to this element.
     */
    protected String getTag(String key) {
        if (tags == null) {
            return null;
        }

        return tags.get(key);
    }

    /**
     * Checks whether a tag with the specified key exists in this element.
     * <p>
     * This method safely handles the case where no tags have been added to this element.
     *
     * @param key the tag key to check for. Must not be {@code null}.
     * @return {@code true} if a tag with the specified key exists, {@code false} otherwise.
     */
    protected boolean contains(String key) {
        if (tags == null) {
            return false;
        }

        return tags.containsKey(key);
    }

    /**
     * Returns a copy of all tags associated with this element.
     * <p>
     * The returned HashMap is a defensive copy, so modifications to it will not
     * affect this element's tags. To add new tags, use {@link #addTag(String, String)}.
     * To modify existing tags, use {@link #modifyTag(String, String)}.
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
