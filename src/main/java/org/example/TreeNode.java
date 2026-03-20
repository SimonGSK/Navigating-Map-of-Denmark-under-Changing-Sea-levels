package org.example;

import java.util.List;

public record TreeNode(List<TreeNode> nodes) {
    public List<TreeNode> find(BoundingBox searchArea) {
        for (TreeNode n : nodes) {

        }
    }
}
