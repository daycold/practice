package com.practice.practice.logic;

import com.practice.practice.Mainable;

/**
 * Copyright 2021 ChinaMobile Info. Tech Ltd. All rights reserved.
 *
 * @author liuwang
 * @date 2022年04月29日
 */
public class LongestValidParentheses implements Mainable {
    @Override
    public void doMain() {
        System.out.println(longestValidParentheses("(())"));
        System.out.println(longestValidParenthesesV2("(())"));
        System.out.println(longestValidParentheses(")()())"));
        System.out.println(longestValidParenthesesV2(")()())"));
        System.out.println(longestValidParentheses(""));
        System.out.println(longestValidParenthesesV2(""));
        System.out.println(longestValidParentheses("(()"));
        System.out.println(longestValidParenthesesV2("(()"));
        System.out.println(longestValidParentheses("(())))())"));
        System.out.println(longestValidParenthesesV2("(())))())"));
        System.out.println(longestValidParentheses("()(((())"));
        System.out.println(longestValidParenthesesV2("()(((())"));
    }

    public int longestValidParentheses(String s) {
        char[] cs = s.toCharArray();
        char[] rs = new char[cs.length];
        int[] stack = new int[cs.length];
        int stackIndex = -1;
        for (int i = 0; i < cs.length; i++) {
            if (cs[i] == '(') {
                stack[++stackIndex] = i;
            } else {
                if (stackIndex >= 0) {
                    rs[i] = '1';
                    rs[stack[stackIndex--]] = '1';
                }
            }
        }
        int length = 0;
        int maxLen = 0;
        for (int i = 0; i < cs.length; i++) {
            if (rs[i] == '1') {
                length++;
            } else {
                if (length > maxLen) {
                    maxLen = length;
                }
                length = 0;
            }
        }
        if (length > maxLen) {
            return length;
        }
        return maxLen;
    }

    /**
     * c[i] = '(' and c[i+1] = ')' => len(i) = 2 + len(i + 2）'
     * c[i] = '(' and c[i+1] = '(' => len(i) = len(i + 1) + c[i + len(i+1) + 1] == ')' ? len(i + len{i+1) + 2) : 0;
     * c[i] = ')' => 0}
     */
    private int l;
    private int i = -1;

    private int len(char[] cs, int index) {
        i = Math.max(index, i);
        if (index >= cs.length) {
            return 0;
        }
        if (cs[index] == ')') {
            return 0;
        }
        int length;
        if (cs[index + 1] == ')') {
            length = 2 + len(cs, index + 2);
            if (length > l) {
                l = length;
            }
            return length;
        }
        length = len(cs, index + 1);
        if (length <= 0) {
            return 0;
        }
        int p = index + length + 1;
        if (p >= cs.length || cs[p] == '(') {
            return 0;
        }
        length += 2 + len(cs, p + 1);
        if (length > l) {
            l = length;
        }
        return length;
    }

    public int longestValidParenthesesV2(String s) {
        i = -1;
        l = 0;
        while (i < s.length()) {
            len(s.toCharArray(), ++i);
        }
        return l;
    }
}
