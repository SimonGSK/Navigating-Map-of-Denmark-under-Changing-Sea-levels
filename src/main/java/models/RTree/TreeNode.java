package models.RTree;

import models.geometry.BoundingBox;
import models.geometry.SpatialElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A TreeNode used to build a spatial indexing R-Tree.
 * TreeNode contains a list of entries (TreeEntry), and has a minimum-bounding-rectangle used for search-queries in the R-Tree.
 */
public class TreeNode implements SpatialElement, Serializable {
    private BoundingBox mbr;
    private double area;
    public List<TreeEntry> entries = new ArrayList<>();
    private boolean isLeaf = false;
    private double subtreeMinZoom = Double.NaN;

    /**
     * Used to update the subtreeMinZoom of this node. Should be called after inserting a new TreeEntry into a TreeNode.
     * subtreeMinZoom is used to track the minimum zoom level of all entries in the TreeNode.
     * Allows easy filtering of whole subtrees if no entries in the subtree would be visible at current zoom level.
     * @param minZoomLevel The minZoomLevel of the TreeEntry that has been inserted into TreeNode
     * @return True if subtreeMinZoom was updated, false otherwise.
     */
    public boolean updateSubtreeMinZoom(double minZoomLevel) {
        if (Double.isNaN(subtreeMinZoom) || minZoomLevel < subtreeMinZoom) {
            subtreeMinZoom = minZoomLevel;
            return true;
        }
        return false;
    }

    /**
     * Resets the value of subtreeMinZoom to NaN in cases where it's necessary to recompute the subtreeMinZoom from scratch,
     * such as when a new TreeNode has been created in an R-Tree as a result of splitting.
     */
    public void resetSubtreeMinZoom() {
        subtreeMinZoom = Double.NaN;
    }

    /**
     * Used to get the value of subtreeMinZoom of this TreeNode. Used by parent TreeNodes to when they need to update the
     * @return
     */
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
