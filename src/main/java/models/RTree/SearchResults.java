package models.RTree;

import enums.ElementType;
import models.osm.Element;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record SearchResults(List<Node> nodeList, List<Way> wayList, List<Relation> relationList) implements Serializable {
    public SearchResults() {
        this(new ArrayList<>(0),new ArrayList<>(0),new ArrayList<>(0));
    }

    public void clear() {
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
        wayList.sort(Comparator.comparingDouble(w -> -w.getMbr().area()));
    }
}