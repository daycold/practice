package com.practice.practice.logic

/**
 * @author : liuwang
 * @date : 2020-10-30T14:22:29Z
 */
class MaxSubString {
    /**
     * 1. 保存当前的最长子串，并构造成数组或者树，方便查询，当插入重复的值时，移除比该值上一次插入早的数据，适用于输入较大重复率较低的情况
     * 2. 不构造数据接口，每次查询遍历子串
     * 3. 遍历一遍元素，得到所有的下标；遍历一遍下标，？？？
     */
    fun solution(text: String): String {
        val chars = text.toCharArray()
        var index = 0
        var size = 0
        var maxIndex = 0
        var maxSize = 0
        return ""
    }

    private class NodeK {
        /**
         * remove the nodes before char and move it to the end when it exists
         * return true when char exists.
         */
        fun setChar(char: Char): Boolean {
            return false;
        }
    }
}
