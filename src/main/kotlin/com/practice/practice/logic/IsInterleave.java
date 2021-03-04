package com.practice.practice.logic;

import com.practice.practice.Mainable;

public class IsInterleave implements Mainable {
    public boolean isInterleave(String s1, String s2, String s3) {
        return isInterleave(s1.toCharArray(), 0, s2.toCharArray(), 0, s3.toCharArray(), 0);
    }

    private boolean isInterleave(char[] chars1, int index1, char[] chars2, int index2, char[] chars3, int index3) {
        if (index1 == chars1.length) {
            return isEqualSequence(chars2, index2, chars3, index3);
        }
        if (index2 == chars2.length) {
            return isEqualSequence(chars1, index1, chars3, index3);
        }
        char c1 = chars1[index1];
        char c2 = chars2[index2];
        char c3 = chars3[index3];
        boolean result = false;
        if (c1 == c3) {
            result = isInterleave(chars1, index1 + 1, chars2, index2, chars3, index3 + 1);
        }
        if (!result && c2 == c3) {
            result = isInterleave(chars1, index1, chars2, index2 + 1, chars3, index3 + 1);
        }
        return result;
    }

    private boolean isEqualSequence(char[] chars1, int index1, char[] chars2, int index2) {
        if (chars1.length - index1 == chars2.length - index2) {
            for (int i = 0; i < chars1.length - index1; i ++) {
                if (chars1[index1 + i] != chars2[index2 + i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void doMain() {
        System.out.println(isInterleave("aabcc", "dbbca", "aadbbcbcac"));
        System.out.println(isInterleave("", "", ""));
        System.out.println(isInterleave("aabcc", "aaeff", "aaabaecffc"));
    }
}
