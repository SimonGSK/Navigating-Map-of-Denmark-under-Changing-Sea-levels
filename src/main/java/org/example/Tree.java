package org.example;

import java.util.ArrayList;
import java.util.List;

public class Tree {
    public TreeNode root = new TreeNode();

    public void insert(Element element) {
        root.insert(element);
    }

    public Tree _find(BoundingBox searchArea) {
        /*
        TODO: I need to create utility bounding boxes for all elements (not use their own bounding boxes). When I query the R-tree I need to keep traversing the tree until I hit a leaf node, and then return the leaf node. Therefore, all OSM elements (Node, Way, Relation) must be leaf nodes, in order for the R-tree to work.
        There are two operations
        - Insert:
            1. Start at the root
            2. Choose the MBR who needs the least enlargement to fit the element that will be inserted
            3. Recurse until the element is at a leaf node
            3. If the leaf node overflows after inserting the element, then split the node
        - Search:
            1. Start at root
            2. Test each entry's MBR to see if it overlaps with search area -> If it overlaps then recurse into child
            3.
         */

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
