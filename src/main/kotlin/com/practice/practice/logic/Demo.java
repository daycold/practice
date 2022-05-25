package com.practice.practice.logic;

import com.beust.jcommander.internal.Lists;

import java.util.*;

/**
 * @author : liuwang
 * @date : 2020-07-06T11:32:01Z
 */
public class Demo {
    private <T> List<T> reverse(List<T> origin) {
        int len = origin.size();
        List<T> result = new ArrayList<>(len);
        // 循环处理第一个和第三个数之间的交换
        for (int index = 0; index < len - 3; index = index + 3) {
            result.add(origin.get(index + 2));
            result.add(origin.get(index + 1));
            result.add(origin.get(index));
        }
        // 处理末尾3的余数,当能被三整除时不会有余数
        int remains = len % 3;
        if (remains == 0) {
            return result;
        }
        result.add(origin.get(len - 1));
        if (remains == 2) {
            result.add(origin.get(len - 2));
        }
        return result;
    }

//    public static void main(String... args) {
//        Demo demo = new Demo();
//        List<Character> characters = Lists.newArrayList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h');
//        for (char c : demo.reverse(characters)) {
//            System.out.print(c);
//            System.out.print(' ');
//        }
//        long mills = System.nanoTime();
//        List<String> list = new LinkedList<>();
//        for (char a = 'a'; a <= 'z'; a++) {
//            for (char b = 'A'; b <= 'Z'; b++) {
//                list.add(new String(new char[]{a, b, 'z', '中', '种', '国', '山'}));
//            }
//        }
//        Link link = new Link();
//        link.addWords(list);
//        System.out.println(System.nanoTime() - mills);
//        String text = "kcnalkgdbanlkybhalkvcndavguanlkdmcaaAAdsckjahbdkiganlcndjkavnl;kdbakj扎钉按掉了对骂空啊对啊;l" +
//                "ka;lsdmopaka;m;a寨卡你爱丽kampoehnioandgaklncaklmdsaklcdadagdadcascachbakjgbaklncaklsnadugfabgdlakj" +
//                "cajkdngaiedtjuhqoirnfvakldsncaiudjghbakldcjkvanlknalkdcna;kldacasdcajgbhdaikdnjcoiw2dakldgna;klscnajfdgab;lg" +
//                "svdajdlb;akjhdfajukbsdkjcabjkdfbahkjsdhkjabcjkadadsfdkvalkndlakjdfgiaklndglacdams;gda;ldcam;adljfkadk" +
//                "dsfjhcdagfado3eijaklsnzZz中种国山'";
//        System.out.println("test.length = " + text.length());
//        mills = System.nanoTime();
//        System.out.println(link.matches(text));
//        System.out.println(System.nanoTime() - mills);
//        System.out.println("list.size = " + list.size());
//        mills = System.nanoTime();
//        for (String word : list) {
//            if (text.contains(word)) {
//                System.out.println(true);
//                break;
//            }
//        }
//        System.out.println(System.nanoTime() - mills);
//    }

    public static void main(String... args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        String[] splits = input.split(" ");
        double pi = 3.1415926535897932;
        double r = Double.parseDouble(splits[0]);
        double h = Double.parseDouble(splits[1]);
        double v = pi * r * r * 2 * h;
        double s = pi * 2 * r * h + pi * 2 * r * r;
        System.out.printf("%.2f %.2f", v, s);
    }

    private int buy(int[] prices) {
        int currentBenefit = 0;
        int buyPrice = 0;
        boolean bought = false;
        for (int i = 0; i < prices.length - 1; i++) {
            if (!bought && prices[i + 1] > prices[i]) {
                bought = true;
                buyPrice = prices[i];
            } else if (bought && prices[i + 1] < prices[i]) {
                bought = false;
                currentBenefit += prices[i] - buyPrice;
                buyPrice = 0;
            }
        }
        if (bought) {
            currentBenefit += prices[prices.length - 1] - buyPrice;
        }
        return currentBenefit;
    }

    static class Link {
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

    static class Node {
        private final char value;
        private final Map<Character, Node> children;
        private boolean isEnd;

        @SuppressWarnings("unused")
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

        @SuppressWarnings("unused")
        boolean isEnd() {
            return isEnd;
        }

        void setEnd(boolean isEnd) {
            this.isEnd = isEnd;
        }

        Map<Character, Node> getChildren() {
            return children;
        }

        @SuppressWarnings("unused")
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
}
