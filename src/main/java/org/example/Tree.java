package org.example;

import java.util.ArrayList;
import java.util.List;

public class Tree {
    public Element element;
    public List<Tree> nodes;

    public Tree _find(BoundingBox searchArea) {
        if (searchArea.isOverlappingOther(element.mbr.box())) {
            return this;
        }
            for (Tree n : nodes) {

            }

        for (Tree n : nodes) {
            BoundingBox nBox = n.element.mbr.box();
            if (!searchArea.isOverlappingOther(nBox)) {
                return null;
            }
        }
    }
}
