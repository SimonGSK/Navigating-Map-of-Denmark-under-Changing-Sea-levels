package models.RTree;

import models.geometry.SpatialElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TreeNode extends SpatialElement implements Serializable {
    public List<TreeEntry> entries = new ArrayList<>();
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
