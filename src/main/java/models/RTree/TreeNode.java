package models.RTree;

import models.geometry.BoundingBox;
import models.geometry.SpatialElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TreeNode implements SpatialElement, Serializable {
    private BoundingBox mbr;
    private double area;
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

    @Override
    public BoundingBox getMbr() {
        return mbr;
    }

    public void setMbr(BoundingBox mbr) {
        this.mbr = mbr;
    }

    @Override
    public double getArea() {
        return area;
    }
}
