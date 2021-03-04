package com.practice.practice.logic;

import com.practice.practice.Mainable;

import java.util.concurrent.locks.ReentrantLock;

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
            for (int j = i; j > 0; j --) {
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
}
