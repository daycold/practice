package com.practice.practice.mq

import com.practice.practice.Logger
import com.practice.practice.Mainable
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import java.io.Closeable
import java.time.Duration
import java.util.*
import kotlin.concurrent.thread

/**
 * @author Stefan Liu
 */
class Kafka(private val brokerHost: String) : Mainable {
    override fun doMain() {
        val consumer = Consumer(brokerHost)
        val producer = Producer(brokerHost)
        thread {
           consumer.run()
        }
        producer.run()
        Thread.sleep(5000)
        consumer.close()
    }

    companion object : Logger() {
        private const val TOPIC = "milo2"
    }

    private class Producer(private val brokerHost: String) : Runnable {
        private val kafkaProducer = KafkaProducer<String, String>(initConfig())

        override fun run() {
            val random = Random()
            repeat(10) {
                val record = ProducerRecord<String, String>(TOPIC, "value-${it}")
                kafkaProducer.send(record) { metadata, exception ->
                    if (exception != null) {
                        log.info(exception.message)
                    } else {
                        println("$it-${metadata.timestamp()}")
                    }
                }
                val mills = random.nextInt(5)
                Thread.sleep((mills * 100).toLong())
            }
            kafkaProducer.close()
        }

        private fun initConfig(): Properties = Properties().apply {
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerHost)
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
        }
    }

    private class Consumer(private val brokerHost: String) : Runnable, Closeable {
        private val kafkaConsumer = KafkaConsumer<String, String>(initConfig()).apply {
                subscribe(listOf(TOPIC))
            }
        private var closed = false

        override fun close() {
            closed = true
        }

        override fun run() {
            while (!closed) {
                val records = kafkaConsumer.poll(Duration.ofMillis(100))
                records.forEach { println(it.value()) }
            }
            kafkaConsumer.close()
        }

        private fun initConfig(): Properties = Properties().apply {
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerHost)
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
            put(ConsumerConfig.GROUP_ID_CONFIG, "0")
            put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        }
    }
}