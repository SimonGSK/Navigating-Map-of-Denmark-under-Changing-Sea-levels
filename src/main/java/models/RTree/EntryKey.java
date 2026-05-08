package models.RTree;

import enums.ElementType;
import models.osm.Element;

import java.io.Serializable;

public record EntryKey(Element element, ElementType type) implements Serializable {
}
