package org.example;

import java.util.ArrayList;
import java.util.List;

public class TreeNodeLeaf extends TreeNode<Element> {
    public TreeNodeLeaf(Element element) {
        super();
        children.add(element);
        calcMBR();
    }

    @Override
    public void insert(Element element) {
        children.add(element);

        if (!isInsideMBR(element)) {
            this.mbr = calcMBR();
        }

        if (children.size() > OVERFLOW_LIMIT) {
            split();
        }
    }

    @Override
    public void split() {

    }
}
