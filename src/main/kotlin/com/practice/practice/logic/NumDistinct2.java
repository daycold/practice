package com.practice.practice.logic;

import com.practice.practice.Mainable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Copyright 2021 ChinaMobile Info. Tech Ltd. All rights reserved.
 *
 * @author liuwang
 * @date 2022年04月25日
 */
public class NumDistinct2 implements Mainable {
    @Override
    public void doMain() {
        doSum();
    }

    private void doSum() {
        long time = System.currentTimeMillis();
        String s1 = "adbdadeecadeadeccaeaabdabdbcdabddddabcaaadbabaaedeeddeaeebcdeabcaaaeeaeeabcddcebddebeebedaecccbdcbcedbdaeaedcdebeec";
        String s2 = "bcddceeeebecbc";
        System.out.printf("%d   -- %d\n", sum(s1, s2), System.currentTimeMillis() - time);
        time = System.currentTimeMillis();
        String s3 = "rabbbit";
        String s4 = "rabbit";
        System.out.printf("%d   -- %d\n", sum(s3, s4), System.currentTimeMillis() - time);
        time = System.currentTimeMillis();
        String s5 = "b";
        String s6 = "b";
        System.out.printf("%d   -- %d\n", sum(s5, s6), System.currentTimeMillis() - time);
        time = System.currentTimeMillis();
        String s7 = "aaddbacbbcabdbceaeeaacbabbbaccacaacbabeddbedcdceceeabccabdadccceebcbcbecdbacedcecdeadbaebceaedaaecbbebdecabbddccebaccabaaabbabceddceecadcccdceabbcdadbbadebdadeacbaddccdeebcddaebbcbedbd";
        String s8 = "ccdeddeabb";
        System.out.printf("%d   --- %d\n", sum(s7, s8), System.currentTimeMillis() - time);
        time = System.currentTimeMillis();
        System.out.printf("%d   --- %d\n", numDistinct(s7, s8), System.currentTimeMillis() - time);
    }

    public int numDistinct(String s, String t) {
        char s1[] = s.toCharArray();
        char t1[] = t.toCharArray();

        int dp[] = new int[t1.length + 1];
        dp[0] = 1;//用来叠加

        for (int i = 0; i < s1.length; i++) {
            for (int j = t1.length - 1; j >= 0; j--) {
                if (t1[j] == s1[i]) {
                    dp[j + 1] += dp[j];
                }
            }
        }
        return dp[t1.length];
    }

    /**
     * 给定一个字符串 s 和一个字符串 t ，计算在 s 的子序列中 t 出现的个数。
     * 字符串的一个 子序列 是指，通过删除一些（也可以不删除）字符且不干扰剩余字符相对位置所组成的新字符串。（例如，"ACE"是"ABCDE"的一个子序列，而"AEC"不是）
     * 题目数据保证答案符合 32 位带符号整数范围。
     * 示例1：
     * 输入：s = "rabbbit", t = "rabbit"
     * 输出：3
     * 解释：
     * 如下图所示, 有 3 种可以从 s 中得到 "rabbit" 的方案。
     * rabbbit
     * rabbbit
     * rabbbit
     * 示例2：
     * 输入：s = "babgbag", t = "bag"
     * 输出：5
     * 解释：
     * 如下图所示, 有 5 种可以从 s 中得到 "bag" 的方案。
     * babgbag
     * babgbag
     * babgbag
     * babgbag
     * babgbag
     * 提示：
     * 0 <= s.length, t.length <= 1000
     * s 和 t 由英文字母组成
     */
    // abcd -> a xb c d => x; a yb c c d => y. 锚定两端，只搜寻单个字符的数量
    private int sum(String source, String target) {
        char[] sChars = source.toCharArray();
        char[] tChars = target.toCharArray();
        ValueNode node = scanNodes(sChars, tChars);
        if (node == null) {
            return 0;
        }
        ValueNode t = scanNodes(tChars, tChars);
        return sum(node, t);
    }

    private int sum(ValueNode source, ValueNode target) {
        return sum(source, target, 1);
    }

    private int sum(ValueNode source, ValueNode target, int linkSum) {
        if (target == null) {
            return linkSum;
        }
        if (source == null) {
            return 0;
        }
        int total = 0;
        if (source.value == target.value) {
            int snum = source.num;
            int tnum = target.num;
            int lsum = 0;
            ValueNode current = source;
            while (snum < tnum) {
                current = current.nextV;
                if (current == null) {
                    return 0;
                }
                snum += current.num;
            }
            for (int s = tnum; s > 0; s--) {

            }
            while (true) {
                lsum = calculateTimes(snum, tnum) - lsum;
                total += sum(current.next, target.next, linkSum * lsum);
                current = current.nextV;
                if (current == null) {
                    break;
                }
                snum += current.num;
            }
        }
        total += sum(source.next, target, linkSum);
        return total;
    }

    // c >= ins.length
//    private int calculateTimes(int[] ins, int c) {
//        for (int i = 0; i < ins.length; i++) {
//        }
//    }
//
//    private int[] split(int length, int sum) {
//        int[] splits = new int[length];
//        for (int i = 0; i < length; i++) {
//            splits[i] = 1;
//        }
//    }

    private int calculateTimes(int a, int b) {
        if (b == 1) {
            return a;
        }
        if (a == b) {
            return 1;
        }
        int plus = 1;
        int devide = 1;
        for (int i = 0; i < b; i++) {
            plus *= (a - i);
            devide *= (b - i);
        }
        return plus / devide;
    }

    private ValueNode scanNodes(char[] source, char[] target) {
        ValueNode head = new ValueNode(target[0]);
        ValueNode current = head;
        Map<Character, ValueNode> map = new HashMap<>();
        map.put(target[0], head);
        Set<Character> sets = new HashSet<>();
        for (char c : target) {
            sets.add(c);
        }
        for (char c : source) {
            if (!sets.contains(c)) {
                continue;
            }
            if (current.value == c) {
                current.num++;
            } else {
                ValueNode node = new ValueNode(c);
                node.num++;
                current.next = node;
                node.pre = current;
                current = node;
                ValueNode preV = map.get(c);
                if (preV != null) {
                    preV.nextV = node;
                }
                map.put(c, node);
            }
        }
        char end = target[target.length - 1];
        while (current.value != end) {
            ValueNode pre = current.pre;
            if (pre == null) {
                break;
            }
            pre.next = null;
            current.pre = null;
            current = pre;
        }
        if (head.num > 0) {
            return head;
        }
        char start = head.value;
        head = head.next;
        if (head == null) {
            return null;
        }
        head.pre = null;
        while (head.value != start) {
            head = head.next;
            if (head == null) {
                break;
            }
            head.pre = null;
        }
        return head;
    }

    private static class ValueNode {
        private final char value;
        private int num;
        private ValueNode pre;
        private ValueNode next;
        private ValueNode nextV;

        ValueNode(char value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("%c:%d", value, num);
        }
    }
}
