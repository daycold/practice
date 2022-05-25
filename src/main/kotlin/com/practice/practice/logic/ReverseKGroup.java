package com.practice.practice.logic;

import com.practice.practice.Mainable;

/**
 * Copyright 2021 ChinaMobile Info. Tech Ltd. All rights reserved.
 *
 * @author liuwang
 * @date 2022年05月07日
 */
public class ReverseKGroup implements Mainable {
    @Override
    public void doMain() {
        printNode(reverseKGroup(buildNode(new int[]{1, 2, 3, 4, 5}), 2));
        printNode(reverseKGroup(buildNode(new int[]{1, 2, 3, 4, 5}), 3));
    }

    private ListNode buildNode(int[] nums) {
        ListNode head = new ListNode(-1);
        ListNode current = head;
        for (int num : nums) {
            ListNode node = new ListNode(num);
            current.next = node;
            current = node;
        }
        current = head.next;
        head.next = null;
        return current;
    }

    private void printNode(ListNode node) {
        StringBuilder builder = new StringBuilder();
        ListNode current = node;
        while (current != null) {
            builder.append(current.val).append(' ');
            current = current.next;
        }
        System.out.println(builder);
    }

    /**
     * Definition for singly-linked list.
     */
    private static class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

    public ListNode reverseKGroup(ListNode head, int k) {
        ListNode start = new ListNode(-1);
        start.next = head;
        ListNode[] nodes = new ListNode[k];
        ListNode node = start;
        while (node != null) {
            node = reverseLink(node, nodes);
        }
        return start.next;
    }

    private ListNode reverseLink(ListNode head, ListNode[] nodes) {
        ListNode node = head.next;
        int i = 0;
        while (node != null && i < nodes.length) {
            nodes[i++] = node;
            node = node.next;
        }
        if (i == nodes.length) {
            for (int j = i - 1; j > 0; j--) {
                nodes[j].next = nodes[j - 1];
            }
            nodes[0].next = node;
            head.next = nodes[i - 1];
            return nodes[0];
        }
        return null;
    }
}
