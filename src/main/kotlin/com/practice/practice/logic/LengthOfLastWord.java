package com.practice.practice.logic;

import com.practice.practice.Mainable;

public class LengthOfLastWord implements Mainable {
    public int lengthOfLastWord(String s) {
        char[] chars = s.toCharArray();
        int size = 0;
        int len = chars.length;
        for (int i = len - 1; i>= 0; i--) {
            if (chars[i] != ' ') {
                size ++;
            } else {
                if (size != 0) {
                    break;
                }
            }
        }
        return size;
    }

    @Override
    public void doMain() {
        System.out.println(lengthOfLastWord("hello world"));
        System.out.println(lengthOfLastWord("   "));
        System.out.println(lengthOfLastWord("hello world   "));
    }
}
