package com.practice.practice.mq

import com.practice.practice.Mainable
import org.apache.activemq.ActiveMQConnection
import org.apache.activemq.ActiveMQConnectionFactory
import javax.jms.*

/**
 * @author Stefan Liu
 */
class ActiveMQ : Mainable {
    override fun doMain() {
        val sender = MessageSender()
        val thread = Thread(MessageReceiver("first"))
        val thread2 = Thread(MessageReceiver("second"))
        thread.start()
        thread2.start()
        Thread.sleep(300)
        sender.run()
    }

    companion object {
        private val brokerUrl = "tcp://localhost:61616"
        private val destination = "sagedragon.mq.queue"
        private val topic = "mq.topic"
    }

    private class MessageSender : Runnable {
        private val sendNum = 5

        fun sendMessage(session: Session, producer: MessageProducer) {
            repeat(sendNum) {
                val message = "发送消息第${it + 1}条"
                val text = session.createTextMessage(message)
                println(message)
                producer.send(text)
            }
        }

        override fun run() {
            val factory =
                ActiveMQConnectionFactory(
                    ActiveMQConnection.DEFAULT_USER,
                    ActiveMQConnection.DEFAULT_PASSWORD,
                    brokerUrl
                )
            ClosableConnection(factory.createConnection()).use { connection ->
                connection.start()
                val session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE)
//        val destination = session.createQueue(destination)
                val queue = session.createTopic(topic)
                val producer = session.createProducer(queue)
                producer.deliveryMode = DeliveryMode.NON_PERSISTENT
                sendMessage(session, producer)
                session.commit()
                session.close()
            }
        }
    }

    private class MessageReceiver(private val name: String = "") : Runnable {
        override fun run() {
            val factory =
                ActiveMQConnectionFactory(
                    ActiveMQConnection.DEFAULT_USER,
                    ActiveMQConnection.DEFAULT_PASSWORD,
                    brokerUrl
                )
            val connection = factory.createConnection()
            connection.start()
            val session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE)
//        val destination = session.createQueue(destination)
            val queue = session.createTopic(topic)
            val consumer = session.createConsumer(queue)
            repeat(5) {
                val message = consumer.receive(1000)
                println("$name 接收: ${(message as TextMessage).text}")
            }
            session.commit()
            session.close()
            connection.close()
        }
    }

    private class QueueReceiver : Runnable {
        override fun run() {
            val factory =
                ActiveMQConnectionFactory(
                    ActiveMQConnection.DEFAULT_USER,
                    ActiveMQConnection.DEFAULT_PASSWORD,
                    brokerUrl
                )
            val connection = factory.createQueueConnection()
            connection.start()
            val session = connection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE)
            val queue = session.createQueue(destination)
            val receiver = session.createReceiver(queue)
//            receiver.messageListener = MessageListener { println((it as MapMessage)) }
        }
    }

    private class ClosableConnection(private val connection: Connection) : Connection by connection, AutoCloseable {
        override fun close() {
            connection.close()
        }
    }
}
