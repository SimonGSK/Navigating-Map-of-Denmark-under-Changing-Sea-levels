package org.example;

import java.util.List;

public class TreeNode extends SpatialElement {
    public List<TreeEntry> entries;
    private boolean isLeaf = false;

    public TreeNode() {
    }

    public TreeNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public boolean isOverflowing(int max) {
        return entries.size() > max;
    }
}
