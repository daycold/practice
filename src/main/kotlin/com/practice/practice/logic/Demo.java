package com.practice.practice.logic;

import java.util.*;

/**
 * @author : liuwang
 * @date : 2020-07-06T11:32:01Z
 */
public class Demo {
    public static void main(String... args) {
        long mills = System.nanoTime();
        List<String> list = new LinkedList<>();
        for (char a = 'a'; a <= 'z'; a++) {
            for (char b = 'A'; b <= 'Z'; b++) {
                list.add(new String(new char[]{a, b, 'z', '中', '种', '国', '山'}));
            }
        }
        Link link = new Link();
        link.addWords(list);
        System.out.println(System.nanoTime() - mills);
        String text = "kcnalkgdbanlkybhalkvcndavguanlkdmcaaAAdsckjahbdkiganlcndjkavnl;kdbakj扎钉按掉了对骂空啊对啊;l" +
                "ka;lsdmopaka;m;a寨卡你爱丽kampoehnioandgaklncaklmdsaklcdadagdadcascachbakjgbaklncaklsnadugfabgdlakj" +
                "cajkdngaiedtjuhqoirnfvakldsncaiudjghbakldcjkvanlknalkdcna;kldacasdcajgbhdaikdnjcoiw2dakldgna;klscnajfdgab;lg" +
                "svdajdlb;akjhdfajukbsdkjcabjkdfbahkjsdhkjabcjkadadsfdkvalkndlakjdfgiaklndglacdams;gda;ldcam;adljfkadk" +
                "dsfjhcdagfado3eijaklsnzZz中种国山'";
        System.out.println("test.length = " + text.length());
        mills = System.nanoTime();
        System.out.println(link.matches(text));
        System.out.println(System.nanoTime() - mills);
        System.out.println("list.size = " + list.size());
        mills = System.nanoTime();
        for (String word : list) {
            if (text.contains(word)) {
                System.out.println(true);
                break;
            }
        }
        System.out.println(System.nanoTime() - mills);
    }
}

class Link {
    private final Map<Character, Node> nodes = new HashMap<>();

    void addWords(List<String> words) {
        int size = words.size();
        for (String word : words) {
            addWord(word.toCharArray(), size);
        }
    }

    void addWord(char[] words, int size) {
        if (words.length == 0) {
            return;
        }
        Map<Character, Node> map = nodes;
        Node node = null;
        for (int i = 0; i < words.length; i++) {
            char c = words[i];
            node = map.get(c);
            if (node == null) {
                for (; i < words.length; i++) {
                    c = words[i];
                    size /= 2;
                    node = new Node(c, Math.max(size, 8));
                    map.put(c, node);
                    map = node.getChildren();
                }
                node.setEnd(true);
                return;
            }
            size /= 2;
            map = node.getChildren();
        }
        node.setEnd(true);
    }

    // check not black before
    boolean matches(String word) {
        char[] words = word.toCharArray();
        for (int start = 0; start < word.length(); start++) {
            Node node = nodes.get(words[start]);
            if (node != null) {
                if (node.matches(words, start + 1)) {
                    return true;
                }
            }
        }
        return false;
    }
}

class Node {
    private final char value;
    private final Map<Character, Node> children;
    private boolean isEnd;

    Node(char value) {
        this.value = value;
        children = new HashMap<>();
    }

    Node(char value, int size) {
        this.value = value;
        children = size > 0 ? new HashMap<>(size) : Collections.emptyMap();
    }

    char getValue() {
        return value;
    }

    boolean isEnd() {
        return isEnd;
    }

    void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }

    Map<Character, Node> getChildren() {
        return children;
    }

    boolean hasChild() {
        return !children.isEmpty();
    }

    boolean matches(char[] input, int start) {
        Node child = this;
        if (child.isEnd) {
            return true;
        }
        while (start < input.length) {
            Map<Character, Node> children = child.getChildren();
            child = children.get(input[start++]);
            if (child == null) {
                return false;
            }
            if (child.isEnd) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Node && ((Node) o).getValue() == value;
    }
}
