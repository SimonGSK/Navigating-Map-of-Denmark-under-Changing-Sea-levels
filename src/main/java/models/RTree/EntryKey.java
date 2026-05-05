package models.RTree;

import enums.ElementType;
import models.osm.Element;

public record EntryKey(Element element, ElementType type) {
}
