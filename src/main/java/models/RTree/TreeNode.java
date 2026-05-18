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
    private double subtreeMinZoom = Double.NaN;

    public TreeNode() {
    }

    public boolean updateSubtreeMinZoom(double minZoomLevel) {
        if (Double.isNaN(subtreeMinZoom) || minZoomLevel < subtreeMinZoom) {
            subtreeMinZoom = minZoomLevel;
            return true;
        }
        return false;
    }

    public void resetSubtreeMinZoom() {
        subtreeMinZoom = Double.NaN;
    }

    public double getSubtreeMinZoom() {
        return subtreeMinZoom;
    }

    public boolean isVisibleOnZoom(double currentZoomLevel) {
        return currentZoomLevel >= subtreeMinZoom;
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

    public void _getMBR(List<BoundingBox> mbrList) {
        mbrList.add(this.getMbr());
        for (TreeEntry entry : entries) {
            mbrList.add(entry.getMbr());
        }
    }
}
