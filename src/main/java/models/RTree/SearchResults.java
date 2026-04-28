package models.RTree;

import enums.ElementType;
import models.osm.Element;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record SearchResults(List<Node> nodeList, List<Way> wayList, List<Relation> relationList) {
    SearchResults() {
        this(new ArrayList<>(),new ArrayList<>(),new ArrayList<>());
    }

    public void add(ElementType type, Element element) {
        switch (type) {
            case ElementType.node -> nodeList.add((Node) element);
            case ElementType.way -> wayList.add((Way) element);
            case ElementType.relation -> relationList.add((Relation) element);
        }
    }

    public void sort() {
        relationList.sort(Comparator.comparingDouble(Relation::getArea).reversed());
        wayList.sort(Comparator.comparingDouble(Way::getArea).reversed());
    }
}