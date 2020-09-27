package com.practice.practice.mq

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.MessageProperties
import java.io.Closeable

/**
 * @author : liuwang
 * @date : 2020-07-29T17:36:36Z
 */
class RabbitMQ : Closeable {
    fun send(topic: String, msg: String) {
        connection.createChannel().use { channel ->
            channel.queueDeclare(topic, false, false, false, null)
            channel.basicPublish(topic, "open_ad_service", false, MessageProperties.TEXT_PLAIN, msg.toByteArray())
        }
    }

    private val factory = ConnectionFactory().apply {
        host = "hello"
        username = "admin"
        password = "world@123"
        virtualHost = "/cloud"
    }
    private val connection: Connection = factory.newConnection()

    override fun close() {
        connection.close()
    }
}