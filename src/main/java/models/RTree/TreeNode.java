package models.RTree;

import models.geometry.BoundingBox;
import models.geometry.SpatialElement;

import java.util.ArrayList;
import java.util.List;

public class TreeNode extends SpatialElement {
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

    public void _getMBR(List<BoundingBox> mbrList) {
        mbrList.add(this.getMbr());
        for (TreeEntry entry : entries) {
            mbrList.add(entry.getMbr());
        }
    }
}
