package com.practice.practice.logic;

/**
 * Copyright 2021 ChinaMobile Info. Tech Ltd. All rights reserved.
 *
 * @author liuwang
 * @date 2022年05月07日
 */
public class MaxPoint {
    public int maxPoints(int[][] points) {
        int len = points.length;
        if (len <= 2) {
            return len;
        }
        int max = 2;
        for (int i = 0; i < len - 2; i++) {
            int size = maxPoints(points, i);
            if (size > max) {
                max = size;
            }
        }
        return max;
    }

    private int maxPoints(int[][] points, int start) {
        int max = 2;
        for (int j = start + 1; j < points.length - 1; j++) {
            Node node = new Node(points[start], points[j]);
            int size;
            if (node.height == 0 && node.width == 0) {
                size = maxPoints(points, j) + 1;
            } else {
                size = maxPoints(points, node, j + 1);
            }
            if (size > max) {
                max = size;
            }
        }
        return max;
    }

    private int maxPoints(int[][] points, Node node, int start) {
        for (int k = start; k < points.length; k++) {
            node.addNode(points[k]);
        }
        return node.size;
    }

    private static class Node {
        int[] header;
        int[] liner;
        int size;
        int height;
        int width;

        Node(int[] header, int[] liner) {
            this.header = header;
            this.liner = liner;
            this.size = 2;
            this.height = liner[1] - header[1];
            this.width = liner[0] - header[0];
        }

        void addNode(int[] node) {
            int height = node[1] - header[1];
            int width = node[0] - header[0];
            // 与起点是同一点
            if (height == 0 && width == 0) {
                size++;
                return;
            }
            if (this.width == 0 && width == 0) {
                size++;
                return;
            }
            if (this.height == 0 && height == 0) {
                size++;
                return;
            }
            int product = height * this.width;
            if (product != 0 && product == width * this.height) {
                size++;
            }
        }
    }
}
