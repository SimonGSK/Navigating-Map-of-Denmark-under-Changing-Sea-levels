package models.RTree;

import enums.ElementType;
import models.osm.Element;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;

import java.io.Serializable;
import java.util.*;

/**
 * Accumulates the OSM elements returned by an R-Tree spatial query,
 * bucketed by type for efficient rendering passes.
 */
public record SearchResults(ArrayList<Node> nodeList, ArrayList<Way> wayList, ArrayList<Relation> relationList) implements Serializable {
    public SearchResults() {
        this(new ArrayList<>(0),new ArrayList<>(0),new ArrayList<>(0));
    }

    /** Trims and clears all lists, releasing excess capacity. */
    public void clear() {
        nodeList.trimToSize();
        wayList.trimToSize();
        relationList.trimToSize();

        nodeList.clear();
        wayList.clear();
        relationList.clear();
    }

    /** Total count across all element types. */
    public int size() {
        return nodeList.size() + wayList().size() + relationList.size();
    }

    /** Routes {@code element} into the correct typed list based on its runtime type. */
    public void add(ElementType type, Element element) {
        switch (element) {
            case Node node -> {
                nodeList.add(node);
            }
            case Way way -> {
                wayList.add(way);
            }
            case Relation relation -> {
                relationList.add(relation);
            }
            default -> {}
        }
    }

    /**
     * Sorts relations and ways largest-first by area so they are drawn before smaller elements.
     * Uses parallel sort for way lists exceeding 1000 elements.
     */
    public void sort() {
        if (relationList.size() > 8192) {
            Relation[] sorted = relationList.toArray(new Relation[0]);
            Arrays.parallelSort(sorted, Comparator.comparingDouble(r -> -r.getArea()));
            relationList.clear();
            Collections.addAll(relationList, sorted);
        } else {
            relationList.sort(Comparator.comparingDouble(r -> -r.getMbr().area()));
        }

        if (wayList.size() > 8192) {
            Way[] sorted = wayList.toArray(new Way[0]);
            Arrays.parallelSort(sorted, Comparator.comparingDouble(w -> -w.getArea()));
            wayList.clear();
            Collections.addAll(wayList, sorted);
        } else {
            wayList.sort(Comparator.comparingDouble(w -> -w.getArea()));
        }
    }
}