package com.practice.practice.logic;

import com.practice.practice.Mainable;

import java.util.Stack;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SortJ implements Mainable {
    public int[] bubbleSort(int[] array) {
        int len = array.length;
        int tmp;
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len - i - 1; j++) {
                if ((array[j] > array[j + 1])) {
                    tmp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = tmp;
                }
            }
        }
        return array;
    }

    public int[] selectionSort(int[] array) {
        int len = array.length;
        int tmp;
        int idx;

        for (int i = 0; i < len - 1; i++) {
            tmp = array[i];
            idx = i;
            for (int j = i + 1; j < len; j++) {
                if (array[j] < tmp) {
                    tmp = array[j];
                    idx = j;
                }
            }
            array[idx] = array[i];
            array[i] = tmp;
        }
        return array;
    }

    public int[] insertionSort(int[] array) {
        int len = array.length;
        int tmp;
        for (int i = 1; i < len; i++) {
            for (int j = i; j > 0; j--) {
                if (array[j] < array[j - 1]) {
                    tmp = array[j];
                    array[j] = array[j - 1];
                    array[j - 1] = tmp;
                }
            }
        }
        return array;
    }

    public int[] shellSort(int[] array) {
        return array;
    }

    public int[] quickSort(int[] array) {
        return array;
    }

    public int[] heapSort(int[] array) {
        return array;
    }

    @Override
    public void doMain() {
        int[] array = {5, 54, 15, 12, 37, 7, 315, 74, 95, 35, 124};
        ReentrantLock lock;
        for (int a : array.clone()) {
            System.out.print(a);
            System.out.print(',');
        }
        System.out.println();

        for (int a : bubbleSort(array.clone())) {
            System.out.print(a);
            System.out.print(',');
        }
        System.out.println();

        for (int a : selectionSort(array.clone())) {
            System.out.print(a);
            System.out.print(',');
        }
        System.out.println();

        for (int a : insertionSort(array.clone())) {
            System.out.print(a);
            System.out.print(',');
        }
        System.out.println();
    }


    static class Node {
        int value;
        Node left;
        Node right;
    }

    private static void printTree(Node root) {
        if (root == null) {
            return;
        }
        Stack<Node> toPrint = new Stack<>();
        toPrint.add(root);
        while (!toPrint.isEmpty()) {
            Stack<Node> current = toPrint;
            Stack<Node> newPrint = new Stack<>();
            while (!current.isEmpty()) {
                Node node = current.pop();
                System.out.print(node.value);
                System.out.print(' ');
                if (node.left != null) {
                    newPrint.add(node.left);
                }
                if (node.right != null) {
                    newPrint.add(node.right);
                }
            }
            toPrint = newPrint;
        }
    }

    private static int sizeTrans(String input) {
        Pattern pattern = Pattern.compile("^(\\d+(.?\\d+))([GMK]?)$");
        Matcher matcher = pattern.matcher(input.trim());
        int k = 1024;
        int m = k * k;
        int g = k * m;
        if (matcher.find()) {
            String numStr = matcher.group(1);
            String mes = matcher.group(3);
            double num = Double.parseDouble(numStr);
            if (mes.isEmpty()) {
                return (int) num;
            }
            switch (mes.charAt(0)) {
                case 'G':
                    return (int) (num * g);
                case 'M':
                    return (int) (num * m);
                case 'K':
                    return (int) (num * k);
                default:
                    return -1;
            }
        } else {
            return -1;
        }
    }

    public static void main(String... args) {
        System.out.println(sizeTrans("10K"));
        System.out.println(sizeTrans("5.7G"));
        System.out.println(sizeTrans("5.7M"));
        System.out.println(sizeTrans("5.7"));
    }
}
