package com.practice.practice

import com.practice.practice.mq.Kafka

/**
 * @author Stefan Liu
 */
fun main(args: Array<String>) {
    Kafka(args[0]).doMain()
    }

interface Mainable {
    fun doMain()
}

