package com.practice.practice.logic;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public abstract class Mine {
    public int[][] createMine(int m, int n, int k) {
        int total = m * n;
        if (k > total) {
            throw new RuntimeException("not enough space for mine");
        }
        Set<Integer> mines = createMine(total, k);

        int[][] map = new int[m][n];
        setMine(map, mines);
        setMineNum(map);
        return map;
    }

    private void setMineNum(int[][] mineMap) {
        "fdas".contains("dds");
        for (int i = 0; i < mineMap.length; i++) {
            for (int j = 0; j < mineMap[0].length; j++) {
                int count = 0;
                boolean untop = i != 0;
                boolean unbutton = i != mineMap.length - 1;
                boolean unleft = j != 0;
                boolean unright = j != mineMap[0].length - 1;
                if (untop && unleft && mineMap[i - 1][j - 1] == -1) {
                    count++;
                }
                if (untop && mineMap[i - 1][j] == -1) {
                    count++;
                }
                if (unleft && mineMap[i][j - 1] == -1) {
                    count++;
                }
                if (unright && unbutton && mineMap[i + 1][j + 1] == -1) {
                    count++;
                }
                if (unbutton && mineMap[i + 1][j] == -1) {
                    count++;
                }
                if (unright && mineMap[i][j + 1] == -1) {
                    count++;
                }
                if (untop && unright && mineMap[i - 1][j + 1] == -1) {
                    count++;
                }
                if (unbutton && unleft && mineMap[i + 1][j - 1] == -1) {
                    count++;
                }
                mineMap[i][j] = count;
            }
        }
    }

    private void setMine(int[][] mineMap, Set<Integer> mines) {
        for (int i : mines) {
            int h = i / mineMap.length;
            int l = i % mineMap.length;
            mineMap[h][l] = -1;
        }
    }

    private Set<Integer> createMine(int total, int k) {
        Set<Integer> set = new HashSet<>(k);
        Random random = new Random();
        while (set.size() < k) {
            int value = random.nextInt(total);
            if (set.contains(value)) {
                continue;
            }
            set.add(value);
        }
        return set;
    }


}
