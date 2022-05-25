package com.practice.practice.logic

import com.practice.practice.Mainable

class MinWindow : Mainable {
    override fun doMain() {
        val s1 = "ADOBECODEBANC"
        val s2 = "ABC"
        println(minWindow(s1, s2))
        val c1 = ""
        val c2 = ""
        println(minWindow(c1, c2))
    }

    /**
     *
     * sort t with the first shown case in s, and it's wrong
     *
     */
    private fun minWindow(s: String, t: String): String {
        if (s.isEmpty()) return ""
        if (t.isEmpty()) return ""
        val stackNode = buildStackNode(t)
        val start = Node('d', -1)
        var current: Node = start
        val chars = s.toCharArray()
        for ((i, c) in chars.withIndex()) {
            val result = stackNode.contains(c)
            if (result == 1) {
                current.next = Node(c, i)
                current = current.next!!

            } else if (result == -1) {
                break
            }
        }

        if (stackNode.next != null) return ""
        val head = start.next!!
        val tail = current
        return minWindow(chars, head, tail)
    }

    private fun buildStackNode(t: String): StackNode {
        val head = StackNode('0')
        var current: StackNode = head
        for (e in t.toCharArray()) {
            val node = StackNode(e)
            current.next = node
            current = node
        }
        return head
    }

    private fun minWindow(s: CharArray, head: Node, tail: Node): String {
        var currentHead = head
        var currentTail = tail
        var minSize = tail.index - head.index
        var minIndex = head.index

        for (i in (currentTail.index).until(s.size)) {
            if (s[i] == currentHead.value) {
                val len = i - currentHead.index
                if (len < minSize) {
                    val node = Node(s[i], i)
                    currentTail.next = node
                    currentTail = node
                    val newHead: Node = head.next!!
                    currentHead.next = null
                    currentHead = newHead
                    minSize = len
                    minIndex = currentHead.index
                }
            }
        }
        val chars = CharArray(minSize) {
           s[minIndex + it]
        }
        return String(chars)
    }

    private class Node(val value: Char, val index: Int) {
        var next: Node? = null
    }

    private class StackNode(val value: Char) {
        var next: StackNode? = null

        fun contains(c: Char): Int {
            var current = this
            while (current.next != null) {
                val node = current.next!!
                if (node.value == c) {
                    current.next = node.next
                    node.next = null
                    if (node == this) {
                        return -1
                    }
                    return 1
                }
                current = node
            }
            return 0
        }
    }
}
