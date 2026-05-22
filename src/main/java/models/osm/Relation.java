package models.osm;

import enums.ElementType;
import models.geometry.AdaptivePath;
import models.geometry.BoundingBox;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

import static enums.ElementType.relation;


public class Relation extends OsmElement implements Iterable<Member>, Serializable {
    private List<Member> members;
    private List<AdaptivePath> ringShapes;

    /**
     * Constructs a Relation element consisting of members (e.g. nodes, ways and other relations) with an ID and tags.
     *
     * @param id The ID of the relation.
     * @param tags A set of key-value tags providing metadata.
     * @param members A list of members comprising the relation.
     */
    public Relation(long id, HashMap<String, String> tags, List<Member> members) {
        super(id, ElementType.relation, tags, computeMbr(members));
        this.members = members;
    }

    /**
     * Computes the bounding box covering all members inside the list.
     *
     * @param members The members forming the relation.
     * @return The combined minimum bounding rectangle.
     */
    static private BoundingBox computeMbr(List<Member> members) {
        return BoundingBox.computeMbr(members.stream().map(Member::getElement).toList());
    }

    /**
     * Validates whether the relation currently holds any members.
     *
     * @return {@code true} if there are no members, otherwise {@code false}.
     */
    public boolean isEmpty() {
        return members.isEmpty();
    }

    /**
     * Safely retrieves all members in the relation as a new list instance.
     *
     * @return A copied list of members, or {@code null} if missing.
     */
    public List<Member> getMembers() {
        if (members == null) {
            return null;
        }

        return new ArrayList<>(members);
    }

    /**
     * Sets a comprehensive list of members directly replacing previous contents
     * and recalibrates the bounding box bounds instantly.
     *
     * @param members The list of members substituting.
     */
    public void setMembers(List<Member> members) {
        this.members = members;
        setMbr(updateMbr(this.members));
    }

    /**
     * Generates a new bounding box from a list of members.
     *
     * @param members The list of members.
     * @return A BoundingBox object.
     */
    private BoundingBox updateMbr(List<Member> members) {
        return BoundingBox.computeMbr(members.stream().map(Member::getElement).toList());
    }

    /**
     * Exposes an iterator processing sequentially all child members inside this relation.
     *
     * @return An iterator representing the members of the relation.
     */
    @Override
    public Iterator<Member> iterator() {
        if (members == null) {
            return Collections.emptyIterator();
        }
        return members.iterator();
    }

    /**
     * Gets all nodes from the relation and its members.
     *
     * @return An independent Set containing distinctive nodes.
     */
    public Set<Node> getNodes() {
        Set<Node> nodes = new HashSet<>();
        for (Member m : members) {
            switch (m.getType()) {
                case node -> nodes.add((Node) m.getElement());
                case way -> nodes.addAll(((Way) m.getElement()).getNodes());
                case relation -> nodes.addAll(((Relation) m.getElement()).getNodes());
            }
        }
        return nodes;
    }

    /**
     * Sets the ringShapes of the relation.
     *
     * @param rings A list of Adaptive paths.
     */
    public void setRingShapes(List<AdaptivePath> rings){
        ringShapes = rings;
    }

    /**
     * Gets the ringShapes of the relation.
     *
     * @return A list of Adaptive paths.
     */
    public List<AdaptivePath> getRingShapes(){
        return ringShapes;
    }

    /**
     * Returns an approximate area of the relation, based on its minimum bounding rectangle (MBR).
     *
     * @return The area of the relation as a double.
     */
    @Override
    public double getArea() {
        return getMbr().area();
    }
}
