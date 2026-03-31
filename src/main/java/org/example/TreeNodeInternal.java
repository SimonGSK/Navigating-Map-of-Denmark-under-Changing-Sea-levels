package org.example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class TreeNodeInternal extends TreeNode<TreeNode> {
    public TreeNodeInternal(TreeNode element) {
        super();
        children.add(element);
        calcMBR();
    }

    @Override
    public void insert(TreeNode element) {
        double minDist = Double.MAX_VALUE;
        TreeNode minDistNode = null;

        for (TreeNode nd : children) {
            double dist = nd.mbr.distanceFromOrigo();
            if (dist < minDist) {
                minDist = dist;
                minDistNode = nd;
            }
        }


    }

    @Override
    public void split() {
        if (children.size() < OVERFLOW_LIMIT) {
            throw new IllegalSplitException("Children is not overflowing in this TreeNode");
        }

        double minDist = Double.MAX_VALUE;
        double maxDist = Double.MIN_VALUE;
        TreeNode minDistNode = null;
        TreeNode maxDistNode = null;

        for (TreeNode nd : children) {
            double dist = nd.mbr.distanceFromOrigo();
            if (dist < minDist && !Objects.equals(nd, maxDistNode)) {
                minDist = dist;
                minDistNode = nd;
            }
            if (dist > maxDist && !Objects.equals(nd, minDistNode)) {
                maxDist = dist;
                maxDistNode = nd;
            }
        }

        TreeNodeInternal left = new TreeNodeInternal(minDistNode);
        TreeNodeInternal right = new TreeNodeInternal(maxDistNode);

        List<TreeNode> nodesToDistribute = new ArrayList<>(children);
        nodesToDistribute.remove(minDistNode);
        nodesToDistribute.remove(maxDistNode);

        for (TreeNode nd : nodesToDistribute) {
            double distToLeft = left.mbr.distanceFromOther(nd.mbr);
            double distToRight = right.mbr.distanceFromOther(nd.mbr);

            if (distToLeft < distToRight) {
                left.insert(nd);
            } else {
                right.insert(nd);
            }
        }

        children = new ArrayList<>();
        children.add(left);
        children.add(right);
    }
}
