package com.practice.practice.logic;

import com.practice.practice.Mainable;

/**
 * Copyright 2021 ChinaMobile Info. Tech Ltd. All rights reserved.
 *
 * @author liuwang
 * @date 2022年05月07日
 */
public class PatternMatch implements Mainable {
    @Override
    public void doMain() {
//        System.out.println(isMatch("a", "*"));
//        System.out.println(isMatch("ab", "a*"));
//        System.out.println(isMatch("abcde", "a*de"));
//        System.out.println(isMatch("abcde", "abcde"));
//        System.out.println(isMatch("adb", "a?b"));
//        System.out.println(isMatch("abcdedeafdfa", "a*e*a"));
//        System.out.println(isMatch("", "*****"));
//        System.out.println(isMatch("a", "*****"));
//        System.out.println(isMatch("a", "a*****"));
//        System.out.println(isMatch("ab", "a*****b"));
//        System.out.println(isMatch("abdc", "a****d*c***"));
//        System.out.println(isMatch("abcabczzzde", "*abc???de*"));
//        System.out.println(isMatch("abcabczzzde", "*abc???de*a"));
        System.out.println(isMatch("abce", "abc*??"));
    }

    /**
     * 给定一个字符串(s) 和一个字符模式(p) ，实现一个支持'?'和'*'的通配符匹配。
     * <p>
     * '?' 可以匹配任何单个字符。
     * '*' 可以匹配任意字符串（包括空字符串）。
     * 两个字符串完全匹配才算匹配成功。
     * <p>
     * 说明:
     * <p>
     * s可能为空，且只包含从a-z的小写字母。
     * p可能为空，且只包含从a-z的小写字母，以及字符?和*。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/wildcard-matching
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */
    public boolean isMatch(String s, String p) {
        return isMatch(s.toCharArray(), p.toCharArray());
    }

    private boolean isMatch(char[] target, char[] pattern) {
        char p;
        int firstAllSymbolIndex = -1;
        int ts = 0;
        for (int i = 0; i < Math.min(target.length, pattern.length); i++) {
            if ((p = pattern[i]) == '*') {
                firstAllSymbolIndex = i;
                ts = i;
                break;
            } else if (p != '?' && p != target[i]) {
                return false;
            }
        }

        if (firstAllSymbolIndex == -1) {
            if (pattern.length > target.length) {
                for (int i = target.length; i < pattern.length; i++) {
                    if (pattern[i] != '*') {
                        return false;
                    }
                }
                return true;
            }
            return pattern.length == target.length;
        }
        // find all '*' symbol indexes
        int nextAllSymbolIndex = -1;
        while (ts < target.length) {
            for (int i = firstAllSymbolIndex + 1; i < pattern.length; i++) {
                if (pattern[i] == '*') {
                    nextAllSymbolIndex = i;
                    break;
                }
            }
            if (nextAllSymbolIndex == -1) {
                return match(target, Math.max(ts, target.length - pattern.length + firstAllSymbolIndex + 1), target.length, pattern, firstAllSymbolIndex + 1, pattern.length);
            } else if (firstAllSymbolIndex == pattern.length - 1) {
                return true;
            } else if (nextAllSymbolIndex == firstAllSymbolIndex + 1) {
                firstAllSymbolIndex = nextAllSymbolIndex;
                nextAllSymbolIndex = -1;
                continue;
            }
            int subIndex = contains(target, ts, target.length, pattern, firstAllSymbolIndex + 1, nextAllSymbolIndex);
            if (subIndex == -1) {
                return false;
            }
            ts = subIndex + nextAllSymbolIndex - firstAllSymbolIndex - 1;
            firstAllSymbolIndex = nextAllSymbolIndex;
            nextAllSymbolIndex = -1;
        }
        for (int i = firstAllSymbolIndex + 1; i < pattern.length; i++) {
            if (pattern[i] != '*') {
                return false;
            }
        }
        return true;
    }

    /**
     * '*' not included
     */
    private boolean match(char[] target, int ts, int te, char[] pattern, int ps, int pe) {
        int len = te - ts;
        if (pe - ps != len) {
            return false;
        }
        char p;
        for (int i = 0; i < len; i++) {
            if ((p = pattern[ps + i]) != '?' && p != target[ts + i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * '*' not included
     * return first index of the first char of the substring
     */
    private int contains(char[] target, int ts, int te, char[] pattern, int ps, int pe) {
        int result = -1;
        int len = pe - ps;
        if (te - ts < len) {
            return result;
        }
        int start;
        for (int i = 0; i <= te - ts - len; i++) {
            if (match(target, (start = ts + i), ts + i + len, pattern, ps, pe)) {
                return start;
            }
        }
        return result;
    }

}
