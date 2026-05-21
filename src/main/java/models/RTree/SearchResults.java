package models.RTree;

import enums.ElementType;
import models.osm.Element;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;

public record SearchResults(ArrayList<Node> nodeList, ArrayList<Way> wayList, ArrayList<Relation> relationList) implements Serializable {
    public SearchResults() {
        this(new ArrayList<>(0),new ArrayList<>(0),new ArrayList<>(0));
    }

    public void clear() {
        nodeList.trimToSize();
        wayList.trimToSize();
        relationList.trimToSize();

        nodeList.clear();
        wayList.clear();
        relationList.clear();
    }

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

    public void sort() {
        relationList.sort(Comparator.comparingDouble(r -> -r.getMbr().area()));

        if (wayList.size() > 1000) {
            Way[] sorted = wayList.toArray(new Way[0]);
            Arrays.parallelSort(sorted, Comparator.comparingDouble(w -> -w.getArea()));
            wayList.clear();
            Collections.addAll(wayList, sorted);
        } else {
            wayList.sort(Comparator.comparingDouble(w -> -w.getArea()));
        }
    }
}