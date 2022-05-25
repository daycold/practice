package com.practice.practice.logic;

import com.practice.practice.Mainable;

/**
 * Copyright 2021 ChinaMobile Info. Tech Ltd. All rights reserved.
 *
 * @author liuwang
 * @date 2022年05月07日
 */
public class PatternMatchV2 implements Mainable {

    @Override
    public void doMain() {

    }

    /**
     * 给你一个字符串s和一个字符规律p，请你来实现一个支持 '.'和'*'的正则表达式匹配。
     * <p>
     * '.' 匹配任意单个字符
     * '*' 匹配零个或多个前面的那一个元素
     * 所谓匹配，是要涵盖整个字符串s的，而不是部分字符串。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/regular-expression-matching
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */
    public boolean isMatch(String s, String p) {
        return false;
    }

    private boolean match(char[] target, char[] pattern) {
        int i = 0, j = 0;
        char p;
        while (i < target.length && j < pattern.length) {
            if (target[i] == (p = pattern[j]) || p == '.') {
                i++;
                j++;
            } else if (p == '*') {
                char q = pattern[j - 1];
                if (q == '.') {
                } else {
                    char t = target[i - 1];
                    int plen = 0;
                    int tlen = 0;
                    for (; j < pattern.length; j++) {
                        char r = pattern[j];
                        if (r == t) {
                            plen++;
                        } else if (r != '*') {
                            break;
                        }
                    }
                    for (; i < target.length; i++) {
                        if (target[i] == t) {
                            tlen++;
                        } else {
                            break;
                        }
                    }
                    if (plen > tlen) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        return true;
    }

    private int match(char[] target, int ts, int te, char[] pattern, int ps, int pe) {
        return -1;
    }
}
