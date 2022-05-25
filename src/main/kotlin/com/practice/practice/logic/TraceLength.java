package com.practice.practice.logic;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright 2021 ChinaMobile Info. Tech Ltd. All rights reserved.
 *
 * @author liuwang
 * @date 2022年03月21日
 */
public class TraceLength {

    public void doMain() throws IOException {
        int[] mn = inputMN();
        int[][] traces = inputTraces(mn[0], mn[1]);
        printLength(0, 32, traces);
    }

    private int[] inputMN() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入交点个数与连线个数，逗号隔开");
        String input = scanner.next();
        Pattern pattern = Pattern.compile("\\d+,\\s?\\d+");
        do {
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                String[] splits = input.split(",");
                int m = Integer.parseInt(splits[0].trim());
                int n = Integer.parseInt(splits[1].trim());
                scanner.close();
                return new int[]{m, n};
            }
            System.out.println("格式有误。请输入交点个数与连线个数，逗号隔开");
        } while (true);
    }

    private int[][] inputTraces(int n, int m) throws IOException {
        int[][] traces = new int[m][];
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入连线");
        String input = scanner.next();
        Pattern pattern = Pattern.compile("\\d+,\\s?\\d+\\s?,\\s?\\d+");
        int index = 0;
        do {
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                String[] splits = input.split(",");
                int x = Integer.parseInt(splits[0].trim());
                int y = Integer.parseInt(splits[1].trim());
                int length = Integer.parseInt(splits[2].trim());
                traces[index++] = new int[]{x, y, length};
            }
            System.out.println("格式有误。请输入连线，逗号隔开");
        } while (index < m);
        scanner.close();
        return traces;
    }

    public void printLength(int n, int m, int[][] trace) {
        MapNode[] nodes = new MapNode[trace.length];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new MapNode(trace[i][0], trace[i][1], trace[i][2]);
        }
        Stack stack = new Stack();
        RecordNode node = new RecordNode();
        stack.head = node;
        stack.tail = stack.head;
        findTrace(n, m, nodes, new TraceRecord(trace.length), stack);
        TraceRecord minRecord = new TraceRecord(0);
        minRecord.traceLength = Integer.MAX_VALUE;
        TraceRecord maxRecord = new TraceRecord(0);
        while (node.next != null) {
            node = node.next;
            if (node.value.traceLength < minRecord.traceLength) {
                minRecord = node.value;
            }
            if (node.value.traceLength > maxRecord.traceLength) {
                maxRecord = node.value;
            }
        }
        System.out.println("min length " + minRecord.traceLength);
        System.out.println("max length" + maxRecord.traceLength);
    }

    private void findTrace(int start, int end, MapNode[] map, TraceRecord record, Stack stack) {
        for (int index = 0; index < map.length; index++) {
            if (record.traces[index] < 0 && map[index].head == start) {
                MapNode startNode = map[index];
                TraceRecord newRecord = new TraceRecord(record.traces);
                newRecord.traces[index] = record.size;
                newRecord.size = record.size + 1;
                newRecord.traceLength = record.traceLength + startNode.length;
                if (startNode.tail == end) {
                    RecordNode node = new RecordNode();
                    node.value = record;
                    stack.tail.next = node;
                    stack.tail = node;
                } else {
                    findTrace(startNode.tail, end, map, newRecord, stack);
                }
            }
        }
    }

    static class TraceRecord {
        final int[] traces;
        int traceLength;
        int size;

        TraceRecord(int size) {
            this.traces = new int[size];
            for (int i = 0; i < size; i++)
                traces[i] = -1;
        }

        TraceRecord(int[] traces) {
            this.traces = Arrays.copyOf(traces, traces.length);
        }
    }

    static class Stack {
        RecordNode head;
        RecordNode tail;
    }

    static class RecordNode {
        TraceRecord value;
        RecordNode next;
    }

    static class MapNode {
        final int head, tail, length;

        MapNode(int head, int tail, int length) {
            this.head = head;
            this.tail = tail;
            this.length = length;
        }
    }
}
