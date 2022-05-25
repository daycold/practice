package com.practice.practice.logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2021 ChinaMobile Info. Tech Ltd. All rights reserved.
 *
 * @author liuwang
 * @date 2022年05月09日
 */
public class ZigzapLevelOrder {
    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        List<Integer> rootValues = new ArrayList<>(1);
        rootValues.add(root.val);
        result.add(rootValues);
        List<TreeNode> current = new ArrayList<>(1);
        current.add(root);
        int level = 1;
        while (true) {
            List<TreeNode> next = new ArrayList<>(1 << level);
            current.forEach(item -> {
                if (item.left != null) {
                    next.add(item.left);
                }
                if (item.right != null) {
                    next.add(item.right);
                }
            });

            if (next.isEmpty()) {
                break;
            }
            int size = next.size();
            List<Integer> values = new ArrayList<>(size);
            if ((level & 1) == 1) {
                for (int i = size - 1; i >= 0; i--) {
                    values.add(next.get(i).val);
                }
            } else {
                next.forEach(item -> values.add(item.val));
            }
            level++;
            result.add(values);
            current = next;
        }
        return result;
    }

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }
}
