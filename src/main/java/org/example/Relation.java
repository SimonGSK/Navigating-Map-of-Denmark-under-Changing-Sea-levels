package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A structured collection of related objects (nodes, ways, and other relations).
 * <p>
 * Relations represent logical or geographic relationships between different OSM elements.
 * The members of a relation form an ordered list, which is critical for relations like
 * bus routes where the order represents the direction of travel, or multipolygons where
 * the order helps define geometry.
 * <p>
 * Each member can optionally have a role that describes its function within the relation.
 * For example, in a multipolygon relation, members can have roles "outer" or "inner" to
 * specify whether they form the outer boundary or inner holes.
 * <p>
 * <a href="https://wiki.openstreetmap.org/wiki/Relation"><i>Source: OpenStreetMap Wiki; Relation</i></a>
 */
public class Relation extends Element {
    private List<Member> members;

    public Relation(long id, HashMap<String, String> tags, List<Member> members) {
        super(id, tags);
        this.members = members;
    }

    /**
     * Adds a member to this relation's ordered list of members.
     * <p>
     * The order of members is significant in OSM relations. For example, in a route relation,
     * the order defines the sequence of travel; in a multipolygon, the order helps define
     * the geometry.
     * <p>
     * While it's technically possible in OSM data for the same member to appear multiple times
     * (such as a bus station visited twice on a loop route), this should be intentional and
     * rare. Duplicate members are often treated as data errors and should be cleaned up.
     * <p>
     * This method lazily initializes the members list on first use.
     *
     * @param member the {@link Member} to be added to this relation. Must not be {@code null}.
     * @return {@code true} if the member was added successfully, {@code false} otherwise.
     */
    public boolean addMember(Member member) {
        if (members == null) {
            members = new ArrayList<>();
        }

        return members.add(member);
    }

    /**
     * Returns a copy of this relation's ordered list of members.
     * <p>
     * The returned list is a defensive copy, so modifications to it will not
     * affect this relation's members. To add members to this relation, use {@link #addMember(Member)}.
     *
     * @return a copy of the list containing all members in this relation in order, or {@code null}
     * if no members have been added to this relation.
     */
    public List<Member> getMembers() {
        if (members == null) {
            return null;
        }

        return new ArrayList<>(members);
    }
}
