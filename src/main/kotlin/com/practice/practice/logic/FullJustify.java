package com.practice.practice.logic;

import com.practice.practice.Mainable;

import java.util.ArrayList;
import java.util.List;

public class FullJustify implements Mainable {
    public List<String> fullJustify(String[] words, int maxWidth) {
        List<String> results = new ArrayList<>();
        int len = words.length;
        int index = 0;
        int wordsSize = 0;
        int wordsLength = 0;
        while (true) {
            String word = words[index];
            int nextSize = wordsSize + wordsLength + word.length();
            if (nextSize <= maxWidth) {
                wordsSize += word.length();
                wordsLength++;
                index++;
                // last line
                if (index == len) {
                    int start = index - wordsLength;
                    StringBuilder builder = new StringBuilder(words[start]);
                    for (int i = start + 1; i < len; i++) {
                        builder.append(' ').append(words[i]);
                    }
                    for (int currentLen = builder.length(); currentLen < maxWidth; currentLen++) {
                        builder.append(' ');
                    }
                    results.add(builder.toString());
                    break;
                }
            } else {
                int start = index - wordsLength;
                int spaceNum = maxWidth - wordsSize;

                StringBuilder builder = new StringBuilder(words[start]);
                int divided = wordsLength - 1;
                if (divided == 0) {
                    for (int i = 0; i < spaceNum; i++) {
                        builder.append(' ');
                    }
                } else {
                    int spaceRound = spaceNum / divided;
                    int spaceSpare = spaceNum % divided;
                    for (int i = start + 1; i < index; i++) {
                        for (int r = 0; r < spaceRound; r++) {
                            builder.append(' ');
                        }
                        if (i <= start + spaceSpare) {
                            builder.append(' ');
                        }
                        builder.append(words[i]);
                    }
                }
                results.add(builder.toString());
                wordsSize = 0;
                wordsLength = 0;
            }
        }
        return results;
    }

    @Override
    public void doMain() {
        String[] words1 = {"Science", "is", "what", "we", "understand", "well", "enough", "to", "explain", "to", "a", "computer.", "Art", "is", "everything", "else", "we", "do"};
        String[] words2 = {"What", "must", "be", "acknowledgment", "shall", "be"};
        for (String s : fullJustify(words1, 20)) {
            System.out.println(s);
        }
        for (String s : fullJustify(words2, 16)) {
            System.out.println(s);
        }
    }
}
